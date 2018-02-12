package kazarovets.flatspinger.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import kazarovets.flatspinger.flats.FlatsListFragment
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.SchedulersFacade
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.utils.filterFlats
import kazarovets.flatspinger.utils.zip


class FlatInfosViewModel(val repository: FlatsRepository,
                         private val schedulersFacade: SchedulersFacade) : ViewModel() {

    private val showSeenLiveData = PreferenceUtils.getBooleanLiveData(PreferenceUtils.SETTINGS_SHOW_SEEN_FLATS, false)
    private val flatsFilterLiveData = PreferenceUtils.getFlatsFilterLiveData()
    private val flatsModeLiveData = MutableLiveData<FlatsListFragment.MODE>()
    private var flats = zip(repository.getLocalFlats(), flatsFilterLiveData,
            showSeenLiveData, flatsModeLiveData) { flats, filter, seen, mode ->
        val showSeen = seen ?: false
        val filtered = flats?.filterFlats(filter)
        filtered?.filter { flatInfo ->
            (!flatInfo.isSeen or (flatInfo.status == FlatStatus.FAVORITE) or showSeen)
                    && (mode?.statuses?.contains(flatInfo.status) ?: true)
        }?.sorted()
    }
    private val favoriteFlatsLiveData = repository.getFavoriteFlats()

    private var isLoading = MutableLiveData<Boolean>()

    var flatsMode: FlatsListFragment.MODE? = null
        set(value) {
            field = value
            flatsModeLiveData.value = value
        }


    private var remoteDisposable: Disposable? = null

    fun init() {
        loadFlats()
    }

    public override fun onCleared() {
        remoteDisposable?.dispose()
    }

    fun getFlats() = flats

    fun getIsLoading() = isLoading

    fun loadFlats() {
        remoteDisposable = repository.getRemoteFlats()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe { isLoading.value = true }
                .doAfterTerminate { isLoading.value = false }
                .subscribe()
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

    fun isFavoriteSync(flat: Flat): Boolean {
        return favoriteFlatsLiveData.value?.find {
            it.isInfoFor(flat)
        } != null
    }
}