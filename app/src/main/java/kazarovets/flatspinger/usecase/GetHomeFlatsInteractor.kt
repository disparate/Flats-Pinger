package kazarovets.flatspinger.usecase

import android.util.Log
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.*
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.Optional
import kazarovets.flatspinger.utils.extensions.filterFlats
import kazarovets.flatspinger.utils.extensions.wrapInOptional


class GetHomeFlatsInteractor(private val flatsRepository: FlatsRepository,
                             private val mergeFlatsStrategy: MergeFlatsStrategy,
                             private val setFlatStatusStrategy: SetFlatStatusStrategy) {
    private val flatStatusesObservable = flatsRepository
            .getFlatStatuses()
            .onErrorReturn { emptyList() }

    private val favoriteFlatsObservable = flatsRepository.getFavorites().toObservable()
            .map { list -> list.map { FlatWithStatusImpl(it, FlatStatus.FAVORITE, true) } }
            .filterWithFlatFilter(flatsRepository.getFlatsFilter())


    fun getObservable(currentFlats: List<FlatWithStatus>): Observable<HomeFlatsResponse> {
        return Observables.combineLatest(getFavoriteFlatsWithLoadingStatus(),
                getRemoteFlatsWithLoadingStatus(Provider.ONLINER),
                getRemoteFlatsWithLoadingStatus(Provider.I_NEED_A_FLAT),
                getCurrentFlatsFiltered(currentFlats)) { favs, onliner, iNeedAFlat, current ->
            val allFlats = sumWithoutDuplicates(favs.flatsOrEmpty(),
                    onliner.flatsOrEmpty(),
                    iNeedAFlat.flatsOrEmpty(),
                    current)
            HomeFlatsResponse(allFlats.sorted(),
                    onliner.loadingStatus,
                    iNeedAFlat.loadingStatus)
        }
    }

    private fun getFavoriteFlatsWithLoadingStatus(): Observable<FlatsWithLoadingStatus> {
        return favoriteFlatsObservable
                .filterHidden()
                .wrapInOptional(true)
                .addLoadingStatus()
    }

    private fun getCurrentFlatsFiltered(currentFlats: List<FlatWithStatus>): Observable<List<FlatWithStatus>> {
        return Observable.just(currentFlats)
                .addFlatStatus(flatStatusesObservable, setFlatStatusStrategy)
                .filterHidden()
                .filterSeen(flatsRepository.getShowSeenFlats())
                .filterWithFlatFilter(flatsRepository.getFlatsFilter())
    }

    private fun getRemoteFlatsWithLoadingStatus(provider: Provider): Observable<FlatsWithLoadingStatus> {
        return flatsRepository.getRemoteFlats(provider).toObservable()
                .addFlatStatus(flatStatusesObservable, setFlatStatusStrategy)
                .filterHidden()
                .filterSeen(flatsRepository.getShowSeenFlats())
                .filterWithFlatFilter(flatsRepository.getFlatsFilter())
                .wrapInOptional(true)
                .addLoadingStatus()
    }


    private fun Observable<out List<FlatWithStatus>>.filterHidden(): Observable<List<FlatWithStatus>> {
        return this.map { it.filter { it.status != FlatStatus.HIDDEN } }
    }



    private fun Observable<out List<FlatWithStatus>>.filterWithFlatFilter(filterObs: Observable<FlatFilter>)
            : Observable<List<FlatWithStatus>> {
        return Observables.combineLatest(this, filterObs) { flats, filter ->
            flats.filterFlats(filter)
        }
    }


    private fun Observable<out List<Flat>>.addFlatStatus(statusesObs: Observable<List<DBFlatInfo>>,
                                                         setFlatStatusStrategy: SetFlatStatusStrategy)
            : Observable<out List<FlatWithStatus>> {
        return Observables.combineLatest(this, statusesObs) { flats, statuses ->
            flats.map { flat ->
                setFlatStatusStrategy.convertWithStatus(flat, statuses)
            }
        }
    }

    private fun Observable<out List<FlatWithStatus>>.filterSeen(showSeenObservable: Observable<Boolean>)
            : Observable<List<FlatWithStatus>> {
        return Observables.combineLatest(this, showSeenObservable) { flats, showSeen ->
            flats.filter { !it.isSeen || it.status == FlatStatus.FAVORITE || showSeen }
        }
    }


    private fun sumWithoutDuplicates(favoriteFlats: List<FlatWithStatus>,
                                     onlinerFlats: List<FlatWithStatus>,
                                     iNeedAFlatFlats: List<FlatWithStatus>,
                                     currentFlats: List<FlatWithStatus>): List<FlatWithStatus> {

        return mergeFlatsStrategy.mergeFlats(favoriteFlats,
                onlinerFlats,
                iNeedAFlatFlats,
                currentFlats)
    }


    private fun Observable<Optional<List<FlatWithStatus>>>.addLoadingStatus(): Observable<FlatsWithLoadingStatus> {
        return this.map {

            if (it.value == null) {
                FlatsWithLoadingStatus(it, FlatsLoadingStatus.LOADING)
            } else {
                FlatsWithLoadingStatus(it, FlatsLoadingStatus.SUCCESS)
            }
        }.onErrorReturn {
            Log.e("GetHomeFlats", "error loading", it)
            FlatsWithLoadingStatus(Optional.empty(), FlatsLoadingStatus.ERROR)
        }
    }

    class FlatsWithLoadingStatus(val flats: Optional<List<FlatWithStatus>>,
                                 val loadingStatus: FlatsLoadingStatus) {
        fun flatsOrEmpty() = flats.value.orEmpty()
    }


    class HomeFlatsResponse(val flats: List<FlatWithStatus>,
                            val onlinerLoadingStatus: FlatsLoadingStatus,
                            val iNeedAFlatLoadingStatus: FlatsLoadingStatus
    )

    enum class FlatsLoadingStatus {
        LOADING, ERROR, SUCCESS
    }
}