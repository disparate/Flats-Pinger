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
import kazarovets.flatspinger.views.ContentViewState


class FlatInfosViewModel(private val repository: FlatsRepository,
                         private val flatsMapper: Mapper<FlatWithStatus, FlatViewState>,
                         private val schedulersFacade: SchedulersFacade) : ViewModel() {

    //TODO: provide interactor from dagger
    private val getHomeFlatsInteractor = GetHomeFlatsInteractor(repository, MergeFlatsStrategy(), SetFlatStatusStrategy())

    private val flatsModeLiveData = MutableLiveData<FlatsListFragment.MODE>()
    private val flatsLiveData = MutableLiveData<List<FlatViewState>>()
    private val showSeenLiveData = MutableLiveData<Boolean>()
    val loadingStateData = LoadingStateData()

    var flatsMode: FlatsListFragment.MODE? = null
        set(value) {
            field = value
            flatsModeLiveData.value = value
        }

    private var flatsDisposable: Disposable? = null

    fun init() {
        loadFlats(false)
    }

    fun getFlats() = combineLatest(flatsLiveData, flatsModeLiveData, showSeenLiveData) { flats, mode, showSeen ->
        flats.orEmpty()
                .filter { mode?.statuses?.contains(it.flat.status) ?: true }
                .filter { it.showAsSeen == false || showSeen == true }
                .apply {
                    if (loadingStateData.isLoading.value == true) {
                        if (isEmpty().not()) {
                            loadingStateData.setState(ContentViewState.CONTENT)
                        }
                    } else {
                        loadingStateData.setState(if (isEmpty()) ContentViewState.NO_DATA else ContentViewState.CONTENT)
                    }
                }
    }

    fun refresh() = loadFlats(true)

    fun setShowSeen(show: Boolean) {
        showSeenLiveData.value = show
    }

    private fun loadFlats(isRefresh: Boolean) {

        val current = flatsLiveData.value?.map { it.flat }.orEmpty()

        loadingStateData.setIsRefreshing(isRefresh)
        if (isRefresh.not()) loadingStateData.setState(ContentViewState.LOADING)
        loadingStateData.setIsLoading(true)

        flatsDisposable?.dispose()
        flatsDisposable = getHomeFlatsInteractor.getObservable(current)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnNext {
                    setLoadingState(it)
                    setIsRefreshState(isRefresh, it)
                }
                .map { it.flats.map { flatsMapper.mapFrom(it) } }
                .subscribe({
                    flatsLiveData.value = it
                }, {
                    Log.e("FlatInfosViewModel", "error loading flats", it)
                })
    }

    private fun setLoadingState(response: GetHomeFlatsInteractor.HomeFlatsResponse) {
        if (response.iNeedAFlatLoadingStatus.isError()
                && response.onlinerLoadingStatus.isError()) {
            loadingStateData.setState(ContentViewState.ERROR)
        }

        if (response.iNeedAFlatLoadingStatus.isLoading().not()
                && response.onlinerLoadingStatus.isLoading().not()) {
            loadingStateData.setIsLoading(false)
        }
    }

    private fun setIsRefreshState(isRefresh: Boolean, response: GetHomeFlatsInteractor.HomeFlatsResponse) {
        if (isRefresh) {
            if (response.iNeedAFlatLoadingStatus.isLoading().not()
                    && response.onlinerLoadingStatus.isLoading().not()) {
                loadingStateData.setIsRefreshing(false)
            }
        }
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

