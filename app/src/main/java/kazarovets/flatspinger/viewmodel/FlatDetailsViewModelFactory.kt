package kazarovets.flatspinger.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import kazarovets.flatspinger.flats.adapter.FlatViewStateMapper
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.SchedulersFacade


class FlatDetailsViewModelFactory(val flatsRepository: FlatsRepository,
                                  val schedulersFacade: SchedulersFacade)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FlatDetailsViewModel(flatsRepository, schedulersFacade) as T
    }


}