package kazarovets.flatspinger.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import kazarovets.flatspinger.flats.FlatsListFragment
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.SchedulersFacade
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.utils.filterFlats


class FlatInfosViewModel(val repository: FlatsRepository,
                         val schedulersFacade: SchedulersFacade) : ViewModel() {

    private var flats = Transformations.map(repository.getLocalFlats()) {
        val filtered = it.filterFlats(PreferenceUtils.flatFilter)
        filtered.filter { flatInfo ->
            (!flatInfo.isSeen or showSeen) and (flatsMode?.statuses?.contains(flatInfo.status) ?: true)
        }.sorted()
    }

    private var isLoading = MutableLiveData<Boolean>()

    var showSeen: Boolean = PreferenceUtils.showSeenFlats
    var flatsMode: FlatsListFragment.MODE? = null


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
}