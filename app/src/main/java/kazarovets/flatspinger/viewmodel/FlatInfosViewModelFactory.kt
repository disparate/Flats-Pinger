package kazarovets.flatspinger.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import kazarovets.flatspinger.flats.adapter.FlatViewStateMapper
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.SchedulersFacade


class FlatInfosViewModelFactory(val flatsRepository: FlatsRepository,
                                val schedulersFacade: SchedulersFacade,
                                val flatsMapper: FlatViewStateMapper)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FlatInfosViewModel(flatsRepository, flatsMapper, schedulersFacade) as T
    }


}