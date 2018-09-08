package kazarovets.flatspinger.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.db.dao.FlatsDao
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatInfo
import kazarovets.flatspinger.model.FlatInfoImpl
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.utils.PreferenceUtils


class FlatsRepository(private val flatsDao: FlatsDao) {

    private val onlinerFlatInfos = Transformations.map(flatsDao.getOnlinerFlats()) {
        val dest = ArrayList<FlatInfo>()
        it.mapTo(dest) { FlatInfoImpl(it.flat, it.status, it.isSeen) }
    }

    private val iNeedAFlatFlatInfos = Transformations.map(flatsDao.getFavoriteFlats()) {
        val dest = ArrayList<FlatInfo>()
        it.mapTo(dest) { FlatInfoImpl(it.flat, it.status, it.isSeen) }
    }

    private val flatsLiveData: LiveData<List<FlatInfo>> = onlinerFlatInfos.mergeWith(
            iNeedAFlatFlatInfos)

    fun getLocalFlats(): LiveData<List<FlatInfo>> = flatsLiveData


    fun getRemoteFlats(): Single<List<Flat>> {
        val minCost = PreferenceUtils.minCost
        val maxCost = PreferenceUtils.maxCost
        val allowAgency = PreferenceUtils.allowAgency
        val rentTypes = PreferenceUtils.rentTypes

        fun addFlatStatuses(flats: List<Flat>) {
            flats.forEach {
                flatsDao.addFlatStatus(DBFlatInfo(provider = it.provider, flatId = it.id))
            }
        }

        return Singles.zip(ApiManager.onlinerApi.getLatestFlats(minCost, maxCost, !allowAgency, rentTypes)
                .doOnSuccess {
                    flatsDao.addFavoriteOnlinerFlat(it)
                    addFlatStatuses(it)
                },
                ApiManager.iNeedAFlatApi.getFlats(minCost?.toDouble(), maxCost?.toDouble(), allowAgency, rentTypes)
                        .doOnSuccess {
                            flatsDao.addFavoriteFlat(it)
                            addFlatStatuses(it)
                        }) { onlinerFlats, iNeedAFlatFlats ->
            val res = ArrayList<Flat>()
            res.addAll(onlinerFlats)
            res.addAll(iNeedAFlatFlats)
            res
        }
    }

    fun updateIsFlatFavorite(flat: Flat, isFavorite: Boolean) {
        val status = if (isFavorite) FlatStatus.FAVORITE else FlatStatus.REGULAR
        flatsDao.updateFlatStatus(flat.id, status, flat.provider)
    }

    fun setFlatSeen(flat: Flat, isSeen: Boolean) {
        flatsDao.updateFlatIsSeen(flat.id, isSeen, flat.provider)
    }

    fun setFlatHidden(flat: Flat) {
        flatsDao.updateFlatStatus(flat.id, FlatStatus.HIDDEN, flat.provider)
    }

    fun getIsFlatFavoriteLiveData(flat: Flat): LiveData<Boolean> {
        return Transformations.map(
                flatsDao.getFlatInfo(flat.id, flat.provider)) {
            it?.status == FlatStatus.FAVORITE
        }
    }

    fun getFavoriteFlats(): LiveData<List<DBFlatInfo>> {
        return flatsDao.getFlatsByStatus(FlatStatus.FAVORITE)
    }

    fun getSeenFlatsFlowable(): Flowable<List<DBFlatInfo>> {
        return flatsDao.getSeenFlatsFlowable(true)
    }

    fun getNotRegularFlatsFlowable(): Flowable<List<DBFlatInfo>> {
        return flatsDao.getFlatsExcludingStatusFlowable(FlatStatus.REGULAR)
    }
}