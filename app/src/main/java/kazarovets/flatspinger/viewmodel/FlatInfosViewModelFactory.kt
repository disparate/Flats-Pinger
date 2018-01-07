package kazarovets.flatspinger.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.SchedulersFacade


class FlatInfosViewModelFactory(val flatsRepository: FlatsRepository, val schedulersFacade: SchedulersFacade)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlatInfosViewModel::class.java)) {
            return FlatInfosViewModel(flatsRepository, schedulersFacade) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}