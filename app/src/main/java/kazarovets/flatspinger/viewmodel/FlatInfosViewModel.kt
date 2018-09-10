package kazarovets.flatspinger.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import io.reactivex.disposables.Disposable
import kazarovets.flatspinger.flats.FlatsListFragment
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatWithStatus
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.SchedulersFacade
import kazarovets.flatspinger.usecase.GetHomeFlatsInteractor
import kazarovets.flatspinger.utils.extensions.combineLatest


class FlatInfosViewModel(val repository: FlatsRepository,
                         private val schedulersFacade: SchedulersFacade) : ViewModel() {

    private val getHomeFlatsInteractor = GetHomeFlatsInteractor(repository)

    private val flatsModeLiveData = MutableLiveData<FlatsListFragment.MODE>()
    private val flatsLiveData = MutableLiveData<List<FlatWithStatus>>()

    private var flats = combineLatest(flatsLiveData, flatsModeLiveData) { flats, mode ->
        //TODO: move to interactor and prefs
        flats.orEmpty().filter {
            mode?.statuses?.contains(it.status) ?: true
        }
    }

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

    fun getFlats() = flats

    fun getIsLoading() = isLoading

    fun loadFlats() {
        flatsDisposable = getHomeFlatsInteractor.getObservable()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe { isLoading.value = true }
                .subscribe({
                    flatsLiveData.value = it.flats
                    if (it.iNeedAFlatLoadingStatus != GetHomeFlatsInteractor.FlatsLoadingStatus.LOADING
                            && it.onlinerLoadingStatus != GetHomeFlatsInteractor.FlatsLoadingStatus.LOADING) {
                        isLoading.value = false
                    }
                }, {
                    Log.e("FlatInfosViewModel", "error loading flats", it)
                })
    }

    fun setHiddenFlat(flat: Flat) {
        schedulersFacade.io().scheduleDirect {
            repository.setFlatHidden(flat)
        }
    }

    fun updateIsFavorite(flat: Flat, isSeen: Boolean) {
        schedulersFacade.io().scheduleDirect {
            repository.updateIsFlatFavorite(flat, isSeen)
        }
    }

    public override fun onCleared() {
        flatsDisposable?.dispose()
    }
}