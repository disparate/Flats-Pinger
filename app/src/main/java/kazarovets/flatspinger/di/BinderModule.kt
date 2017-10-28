package kazarovets.flatspinger.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kazarovets.flatspinger.activity.MainActivity
import kazarovets.flatspinger.fragments.FlatsListFragment

@Module
abstract class BinderModule {

    @ContributesAndroidInjector
    abstract fun bindFlatsListFragment(): FlatsListFragment


    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
}