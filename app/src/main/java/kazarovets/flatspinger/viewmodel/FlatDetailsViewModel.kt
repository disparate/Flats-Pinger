package kazarovets.flatspinger.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import io.reactivex.disposables.Disposable
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatWithStatus
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.SchedulersFacade


class FlatDetailsViewModel(val repository: FlatsRepository,
                           private val schedulersFacade: SchedulersFacade) : ViewModel() {

    private val isFavoriteMutableLiveData = MutableLiveData<Boolean>()

    val flatIsFavoriteLiveData: LiveData<Boolean> = isFavoriteMutableLiveData

    private var isFavoriteDisposable: Disposable? = null

    fun init(flat: Flat) {
        observeIsFavorite(flat)
    }

    private fun observeIsFavorite(flat: Flat) {
        repository.getFavorites()
                .map { favs -> favs.any { it.id == flat.id && it.provider == flat.provider } }
                .observeOn(schedulersFacade.ui())
                .subscribe({
                    isFavoriteMutableLiveData.postValue(it)
                }, {
                    Log.e("FlatDetailsViewModel", "error observing favorite status", it)
                })
    }

    fun updateIsFavorite(flat: FlatWithStatus, isSeen: Boolean) {
        schedulersFacade.io().scheduleDirect {
            repository.updateIsFlatFavorite(flat, isSeen)
        }
    }

    fun setSeenFlat(flat: FlatWithStatus) {
        schedulersFacade.io().scheduleDirect {
            repository.setFlatSeen(flat)
        }
    }

    override fun onCleared() {
        super.onCleared()
        isFavoriteDisposable?.dispose()
    }
}