package kazarovets.flatspinger.di

import dagger.Component
import kazarovets.flatspinger.activity.MainActivity
import kazarovets.flatspinger.flats.FlatsJobSchedulerService
import kazarovets.flatspinger.fragments.FlatsListFragment
import kazarovets.flatspinger.fragments.FlatsMapFragment
import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(AppModule::class, FlatsModule::class))
interface AppComponent {

    fun inject(fragment: FlatsListFragment)

    fun inject(fragment: FlatsMapFragment)

    fun inject(service: FlatsJobSchedulerService)

    fun inject(activity: MainActivity)
}