package kazarovets.flatspinger.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.db.dao.FlatsDao
import kazarovets.flatspinger.db.model.DBFlat
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatFilter
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.utils.PreferenceUtils


class FlatsRepository(private val flatsDao: FlatsDao) {

    fun getFavorites(): Flowable<out List<Flat>> {
        return flatsDao.getFavoriteFlats()
                .doOnSubscribe { Log.d("NOCOMMIT", "favorites emit") }
                .map { it.map { it } }
    }

    fun getRemoteFlats(provider: Provider): Single<out List<Flat>> {
        val minCost = PreferenceUtils.minCost
        val maxCost = PreferenceUtils.maxCost
        val allowAgency = PreferenceUtils.allowAgency
        val rentTypes = PreferenceUtils.rentTypes

        val obs = when (provider) {
            Provider.ONLINER ->
                ApiManager.onlinerApi.getLatestFlats(minCost,
                        maxCost,
                        !allowAgency,
                        rentTypes)
            Provider.I_NEED_A_FLAT ->
                ApiManager.iNeedAFlatApi.getFlats(minCost?.toDouble(),
                        maxCost?.toDouble(),
                        allowAgency,
                        rentTypes)
        }

        return obs.doOnSuccess { flats ->
            flats.forEach {
                flatsDao.addFlatStatus(DBFlatInfo.createFromFlat(it))
            }
        }
    }

    fun updateIsFlatFavorite(flat: Flat, isFavorite: Boolean) {
        if (isFavorite) {
            flatsDao.addFavoriteFlat(DBFlat.createFromFlat(flat))
        } else {
            flatsDao.removeFavoriteFlat(DBFlat.createFromFlat(flat))
        }
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

    fun getSeenFlatsFlowable(): Flowable<List<DBFlatInfo>> {
        return flatsDao.getSeenFlatsFlowable(true)
    }

    fun getFlatStatuses(): Observable<List<DBFlatInfo>> {
        return flatsDao.getDBFlatInfosFlowable().toObservable()
    }

    fun getNotRegularFlatsFlowable(): Flowable<List<DBFlatInfo>> {
        return flatsDao.getFlatsExcludingStatusFlowable(FlatStatus.REGULAR)
    }

    //TODO: remove singleton
    fun getFlatsFilter(): Observable<FlatFilter> {
        return PreferenceUtils.flatsFilterObservable
    }

    //TODO: remove singleton
    fun getShowSeenFlats(): Observable<Boolean> {
        return PreferenceUtils.showSeenFlatObservable
    }
}