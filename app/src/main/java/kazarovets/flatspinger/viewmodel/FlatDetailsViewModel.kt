package kazarovets.flatspinger.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.SchedulersFacade


class FlatDetailsViewModel(val repository: FlatsRepository,
                           private val schedulersFacade: SchedulersFacade) : ViewModel() {

    lateinit var flatIsFavoriteLiveData: LiveData<Boolean>
        private set

    fun init(flat: Flat) {
        flatIsFavoriteLiveData = repository.getIsFlatFavoriteLiveData(flat)
    }

    fun updateIsFavorite(flat: Flat, isSeen: Boolean) {
        schedulersFacade.io().scheduleDirect {
            repository.updateIsFlatFavorite(flat, isSeen)
        }
    }

    fun setSeenFlat(flat: Flat) {
        schedulersFacade.io().scheduleDirect {
            repository.setFlatSeen(flat, true)
        }
    }
}