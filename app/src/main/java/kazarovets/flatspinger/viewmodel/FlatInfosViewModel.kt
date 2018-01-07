package kazarovets.flatspinger.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import kazarovets.flatspinger.fragments.FlatsListFragment
import kazarovets.flatspinger.model.FlatInfo
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.SchedulersFacade
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.utils.filterFlats


class FlatInfosViewModel(val repository: FlatsRepository,
                         val schedulersFacade: SchedulersFacade) : ViewModel() {

    private var flats = MutableLiveData<List<FlatInfo>>()
    private var isLoading = MutableLiveData<Boolean>()

    var showSeen: Boolean = PreferenceUtils.showSeenFlats
    var flatsMode: FlatsListFragment.MODE? = null

    private var localDisposable: Disposable? = null

    private var remoteDisposable: Disposable? = null

    fun init() {

        localDisposable = repository.getLocalFlats()
                .map {
                    val filtered = it.filterFlats()
                    filtered.filter { flatInfo ->
                        (!flatInfo.isSeen or showSeen) and (flatsMode?.statuses?.contains(flatInfo.status) ?: true)
                    }
                }
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe {
                    flats.setValue(it)
                }
        loadFlats()
    }

    public override fun onCleared() {
        localDisposable?.dispose()
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
}