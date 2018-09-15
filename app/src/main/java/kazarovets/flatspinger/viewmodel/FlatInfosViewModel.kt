package kazarovets.flatspinger.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import io.reactivex.disposables.Disposable
import kazarovets.flatspinger.base.Mapper
import kazarovets.flatspinger.flats.FlatsListFragment
import kazarovets.flatspinger.flats.adapter.FlatViewState
import kazarovets.flatspinger.model.FlatWithStatus
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.SchedulersFacade
import kazarovets.flatspinger.usecase.GetHomeFlatsInteractor
import kazarovets.flatspinger.usecase.MergeFlatsStrategy
import kazarovets.flatspinger.usecase.SetFlatStatusStrategy
import kazarovets.flatspinger.utils.extensions.combineLatest


class FlatInfosViewModel(private val repository: FlatsRepository,
                         private val flatsMapper: Mapper<FlatWithStatus, FlatViewState>,
                         private val schedulersFacade: SchedulersFacade) : ViewModel() {

    //TODO: provide interactor from dagger
    private val getHomeFlatsInteractor = GetHomeFlatsInteractor(repository, MergeFlatsStrategy(), SetFlatStatusStrategy())

    private val flatsModeLiveData = MutableLiveData<FlatsListFragment.MODE>()
    private val flatsLiveData = MutableLiveData<List<FlatViewState>>()

    private var isLoading = MutableLiveData<Boolean>()

    var flatsMode: FlatsListFragment.MODE? = null
        set(value) {
            field = value
            flatsModeLiveData.value = value
        }

    private var flatsDisposable: Disposable? = null

    fun init() {
        loadFlats()
    }

    fun getFlats() = combineLatest(flatsLiveData, flatsModeLiveData) { flats, mode ->
        flats.orEmpty().filter {
            mode?.statuses?.contains(it.flat.status) ?: true
        }
    }

    fun getIsLoading() = isLoading

    fun loadFlats() {
        val current = flatsLiveData.value?.map { it.flat }.orEmpty()

        flatsDisposable?.dispose()
        flatsDisposable = getHomeFlatsInteractor.getObservable(current)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe { isLoading.value = true }
                .doOnNext {
                    if (it.iNeedAFlatLoadingStatus != GetHomeFlatsInteractor.FlatsLoadingStatus.LOADING
                            && it.onlinerLoadingStatus != GetHomeFlatsInteractor.FlatsLoadingStatus.LOADING) {
                        isLoading.value = false
                    }
                }
                .map { it.flats.map { flatsMapper.mapFrom(it) } }
                .subscribe({
                    flatsLiveData.value = it
                }, {
                    Log.e("FlatInfosViewModel", "error loading flats", it)
                })
    }

    fun setHiddenFlat(flat: FlatWithStatus) {
        schedulersFacade.io().scheduleDirect {
            repository.setFlatHidden(flat)
        }
    }

    fun updateIsFavorite(flat: FlatWithStatus, isSeen: Boolean) {
        schedulersFacade.io().scheduleDirect {
            repository.updateIsFlatFavorite(flat, isSeen)
        }
    }

    public override fun onCleared() {
        flatsDisposable?.dispose()
    }
}