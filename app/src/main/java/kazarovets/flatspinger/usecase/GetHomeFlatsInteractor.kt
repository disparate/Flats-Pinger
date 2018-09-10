package kazarovets.flatspinger.usecase

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.*
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.Optional
import kazarovets.flatspinger.utils.extensions.filterFlats
import kazarovets.flatspinger.utils.extensions.wrapInOptional


class GetHomeFlatsInteractor(private val flatsRepository: FlatsRepository) {

    fun getObservable(): Observable<HomeFlatsResponse> {
        return Observables.combineLatest(getFavoriteFlatsWithLoadingStatus(),
                getRemoteFlatsWithLoadingStatus(Provider.ONLINER),
                getRemoteFlatsWithLoadingStatus(Provider.I_NEED_A_FLAT)) { favs, onliner, iNeedAFlat ->
            val allFlats = summWithoutDuplicates(favs.flatsOrEmpty(), onliner.flatsOrEmpty(), iNeedAFlat.flatsOrEmpty())
            HomeFlatsResponse(allFlats.sorted(),
                    onliner.loadingStatus,
                    iNeedAFlat.loadingStatus)
        }
    }

    private fun getFavoriteFlatsWithLoadingStatus(): Observable<FlatsWithLoadingStatus> {
        fun Observable<out List<Flat>>.addFlatStatus(): Observable<List<FlatWithStatus>> {
            return this.map { list ->
                list.map { FlatWithStatusImpl(it, FlatStatus.FAVORITE, true) }
            }
        }

        return flatsRepository.getFavorites().toObservable()
                .addFlatStatus()
                .filterWithFlatFilter(flatsRepository.getFlatsFilter())
                .wrapInOptional(true)
                .addLoadingStatus()
    }

    private fun getRemoteFlatsWithLoadingStatus(provider: Provider): Observable<FlatsWithLoadingStatus> {
        val flatStatuses = flatsRepository.getFlatStatuses()
                .onErrorReturn { emptyList() }


        return flatsRepository.getRemoteFlats(provider).toObservable()
                .addFlatStatus(flatStatuses)
                .filterSeen(flatsRepository.getShowSeenFlats())
                .filterWithFlatFilter(flatsRepository.getFlatsFilter())
                .wrapInOptional(true)
                .addLoadingStatus()
    }

    private fun Observable<out List<Flat>>.addFlatStatus(statusesObs: Observable<List<DBFlatInfo>>)
            : Observable<out List<FlatWithStatus>> {
        return Observables.combineLatest(this, statusesObs) { flats, statuses ->
            flats.map { flat ->
                val status = statuses.find {
                    val sameIds = it.flatId == flat.id
                    val sameImageUrls = it.imageUrl.isNotBlank() && it.imageUrl == flat.imageUrl
                    sameIds && sameImageUrls
                }
                status?.run {
                    FlatWithStatusImpl(flat,
                            this.status,
                            this.isSeen)
                } ?: FlatWithStatusImpl(flat, FlatStatus.REGULAR, false)
            }
        }
    }

    private fun Observable<out List<FlatWithStatus>>.filterSeen(showSeenObservable: Observable<Boolean>)
            : Observable<List<FlatWithStatus>> {
        return Observables.combineLatest(this, showSeenObservable) { flats, showSeen ->
            flats.filter { !it.isSeen || it.status == FlatStatus.FAVORITE || showSeen }
        }
    }

    private fun Observable<out List<FlatWithStatus>>.filterWithFlatFilter(filterObs: Observable<FlatFilter>)
            : Observable<List<FlatWithStatus>> {
        return Observables.combineLatest(this, filterObs) { flats, filter ->

            flats.filterFlats(filter)
        }
    }

    private fun summWithoutDuplicates(favoriteFlats: List<FlatWithStatus>,
                                      onlinerFlats: List<FlatWithStatus>,
                                      iNeedAFlatFlats: List<FlatWithStatus>): List<FlatWithStatus> {
        val onlinerWithoutDuplicates = onlinerFlats.filter { onliner ->
            iNeedAFlatFlats
                    .any { iNeedAFlat -> iNeedAFlat.originalUrl == onliner.originalUrl }
                    .not()
        }

        fun List<FlatWithStatus>.removeDuplicates(filterPredicate: (FlatWithStatus) -> String?): List<FlatWithStatus> {
            val set = HashSet<String>()
            return this.filter {
                filterPredicate(it)?.let {
                    val setContainsKey = set.contains(it)
                    set.add(it)
                    setContainsKey.not()
                } ?: true
            }
        }

        val remotesWithoutDuplicates = (onlinerWithoutDuplicates + iNeedAFlatFlats).removeDuplicates { it.imageUrl }
        val favsWithoutDuplicates = favoriteFlats.filter { fav ->
            remotesWithoutDuplicates.any { it.originalUrl == fav.originalUrl }.not()
        }

        return favsWithoutDuplicates + remotesWithoutDuplicates
    }


    private fun Observable<Optional<List<FlatWithStatus>>>.addLoadingStatus(): Observable<FlatsWithLoadingStatus> {
        return this.map {

            if (it.value == null) {
                FlatsWithLoadingStatus(it, FlatsLoadingStatus.LOADING)
            } else {
                FlatsWithLoadingStatus(it, FlatsLoadingStatus.SUCCESS)
            }
        }.onErrorReturn {
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