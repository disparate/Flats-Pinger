package kazarovets.flatspinger.repository

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.Singles
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.db.dao.FlatsDao
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatInfo
import kazarovets.flatspinger.model.FlatInfoImpl
import kazarovets.flatspinger.utils.PreferenceUtils


class FlatsRepository(val flatsDao: FlatsDao) {

    private val flatsObservable: Flowable<List<FlatInfo>> = Flowables.zip(flatsDao.getOnlinerFlats(), flatsDao.getINeedAFlatFlats()) { list1, list2 ->
        val res = ArrayList<FlatInfo>()
        list1.mapTo(res) { FlatInfoImpl(it.flat, it.status, it.isSeen) }
        list2.mapTo(res) { FlatInfoImpl(it.flat, it.status, it.isSeen) }
        res
    }

    fun getLocalFlats(): Flowable<List<FlatInfo>> = flatsObservable


    fun getRemoteFlats(): Single<List<Flat>> {

        val minCost = PreferenceUtils.minCost
        val maxCost = PreferenceUtils.maxCost
        val allowAgency = PreferenceUtils.allowAgency
        val rentTypes = PreferenceUtils.rentTypes

        return Singles.zip(ApiManager.onlinerApi.getLatestFlats(minCost, maxCost, !allowAgency, rentTypes)
                .doOnSuccess { flatsDao.addOnlinerFlats(it) },
                ApiManager.iNeedAFlatApi.getFlats(minCost?.toDouble(), maxCost?.toDouble(), allowAgency, rentTypes)
                        .doOnSuccess { flatsDao.addINeedAFlatFlats(it) }) { onlinerFlats, iNeedAFlatFlats ->

            val res = ArrayList<Flat>()
            res.addAll(onlinerFlats)
            res.addAll(iNeedAFlatFlats)
            res
        }
    }
}