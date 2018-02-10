package kazarovets.flatspinger.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.db.dao.FlatsDao
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatInfo
import kazarovets.flatspinger.model.FlatInfoImpl
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.utils.mergeWith


class FlatsRepository(val flatsDao: FlatsDao) {

    private val onlinerFlatInfos = Transformations.map(flatsDao.getOnlinerFlats()) {
        val dest = ArrayList<FlatInfo>()
        it.mapTo(dest) { FlatInfoImpl(it.flat, it.status, it.isSeen)}
    }


    private val iNeedAFlatFlatInfos = Transformations.map(flatsDao.getINeedAFlatFlats()) {
        val dest = ArrayList<FlatInfo>()
        it.mapTo(dest) { FlatInfoImpl(it.flat, it.status, it.isSeen)}
    }

    private val flatsLiveData: LiveData<List<FlatInfo>> = onlinerFlatInfos.mergeWith(
            iNeedAFlatFlatInfos)

    fun getLocalFlats(): LiveData<List<FlatInfo>> = flatsLiveData


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