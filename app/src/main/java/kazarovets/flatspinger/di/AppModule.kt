package kazarovets.flatspinger.di

import android.content.Context
import dagger.Module
import dagger.Provides
import kazarovets.flatspinger.FlatsApplication

@Module
class AppModule(private val application: FlatsApplication) {

    @Provides
    fun provideContext(): Context = application.applicationContext

}