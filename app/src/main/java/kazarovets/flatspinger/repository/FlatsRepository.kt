package kazarovets.flatspinger.repository

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.db.dao.FlatsDao
import kazarovets.flatspinger.db.model.DBFlat
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatFilter
import kazarovets.flatspinger.model.FlatWithStatus
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.rx.SchedulersFacade
import kazarovets.flatspinger.utils.PreferenceUtils


class FlatsRepository(private val flatsDao: FlatsDao,
                      private val schedulersFacade: SchedulersFacade) {

    fun getFavorites(): Flowable<out List<Flat>> {
        return flatsDao.getFavoriteFlats()
                .map { it.map { it } }
                .subscribeOn(schedulersFacade.io())
    }

    fun getRemoteFlats(provider: Provider): Single<out List<Flat>> {
        val minCost = PreferenceUtils.minCost
        val maxCost = PreferenceUtils.maxCost
        val allowAgency = PreferenceUtils.allowAgency
        val rentTypes = PreferenceUtils.rentTypes

        return when (provider) {
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
    }

    fun updateIsFlatFavorite(flat: FlatWithStatus, isFavorite: Boolean) {
        if (isFavorite) {
            flatsDao.addFavoriteFlat(DBFlat.createFromFlat(flat))
        } else {
            flatsDao.removeFavoriteFlat(flat.id)
        }
    }

    fun setFlatSeen(flat: FlatWithStatus) {
        flatsDao.addOrReplaceFlatStatus(DBFlatInfo.createSeenFromFlat(flat))
    }

    fun setFlatHidden(flat: FlatWithStatus) {
        flatsDao.removeFavoriteFlat(flat.id)
        if(flat.imageUrl?.isNotBlank() == true) {
            flatsDao.removeFavoriteFlatByImageUrl(flat.imageUrl)
        }
        flatsDao.addOrReplaceFlatStatus(DBFlatInfo.createHiddenFromFlat(flat))
    }

    fun getSeenFlatsFlowable(): Flowable<List<DBFlatInfo>> {
        return flatsDao.getSeenFlatsFlowable()
    }

    fun getFlatStatuses(): Observable<List<DBFlatInfo>> {
        return flatsDao.getDBFlatInfosFlowable().toObservable()
    }

    fun getHiddenFlatsFlowable(): Flowable<List<DBFlatInfo>> {
        return flatsDao.getHiddenFlatsFlowable()
    }

    //TODO: remove singleton
    fun getFlatsFilter(): Observable<FlatFilter> {
        return PreferenceUtils.flatsFilterObservable
    }

}