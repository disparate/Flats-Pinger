package kazarovets.flatspinger.usecase

import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.*
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.utils.extensions.filterFlats


class GetRemoteFlatsInteractor(private val flatsRepository: FlatsRepository,
                               private val mergeFlatsStrategy: MergeFlatsStrategy,
                               private val setFlatStatusStrategy: SetFlatStatusStrategy) {

    private val flatStatusesObservable = flatsRepository
            .getFlatStatuses()
            .firstOrError()
            .onErrorReturn { emptyList() }

    private val flatsFilterSafe = flatsRepository
            .getFlatsFilter()
            .firstOrError()
            .onErrorReturn { FlatFilter() }


    fun getObservable(): Single<List<Flat>> {
        return Singles.zip(
                getRemoteFlatsFiltered(Provider.ONLINER),
                getRemoteFlatsFiltered(Provider.I_NEED_A_FLAT)) { onliner, iNeedAFlat ->
            mergeFlatsStrategy.mergeFlats(onlinerRemoteFlats = onliner,
                    iNeedAFlatRemoteFlats = iNeedAFlat)
        }
    }

    private fun getRemoteFlatsFiltered(provider: Provider): Single<List<FlatWithStatus>> {
        return flatsRepository.getRemoteFlats(provider)
                .addFlatStatus(flatStatusesObservable, setFlatStatusStrategy)
                .map { it.filterNotHidden() }
                .map { it.filterNotSeen() }
                .filterWithFlatFilter(flatsFilterSafe)
                .onErrorReturn { emptyList() }
    }


    private fun Single<out List<Flat>>.addFlatStatus(statusesObs: Single<List<DBFlatInfo>>,
                                                     setFlatStatusStrategy: SetFlatStatusStrategy)
            : Single<out List<FlatWithStatus>> {
        return Singles.zip(this, statusesObs) { flats, statuses ->
            flats.map { flat ->
                setFlatStatusStrategy.convertWithStatus(flat, statuses)
            }
        }
    }

    private fun List<FlatWithStatus>.filterNotHidden(): List<FlatWithStatus> {
        return this.filter { it.status != FlatStatus.HIDDEN }
    }

    private fun List<FlatWithStatus>.filterNotSeen(): List<FlatWithStatus> {
        return this.filter { it.isSeen.not() }
    }

    private fun Single<out List<FlatWithStatus>>.filterWithFlatFilter(filterObs: Single<FlatFilter>)
            : Single<List<FlatWithStatus>> {
        return Singles.zip(this, filterObs) { flats, filter ->
            flats.filterFlats(filter)
        }
    }

}