package kazarovets.flatspinger.di

import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton
import dagger.BindsInstance
import dagger.android.AndroidInjectionModule
import kazarovets.flatspinger.FlatsApplication


@Singleton
@Component(modules = arrayOf(AndroidInjectionModule::class, AndroidSupportInjectionModule::class,
        AppModule::class, BinderModule::class, FlatsModule::class))
interface AppComponent {

    fun inject(app: FlatsApplication)
}