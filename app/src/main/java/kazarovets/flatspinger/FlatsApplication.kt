package kazarovets.flatspinger

import android.app.Application
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.di.AppComponent
import kazarovets.flatspinger.di.AppModule
import kazarovets.flatspinger.di.DaggerAppComponent
import kazarovets.flatspinger.di.FlatsModule
import kazarovets.flatspinger.utils.PreferenceUtils


class FlatsApplication : Application() {

    lateinit var appComponent: AppComponent
        private set

    companion object {
        val DEFAULT_NUMBER_OF_DAYS_AD_IS_ACTUAL = 10
    }

    override fun onCreate() {
        super.onCreate()

        ApiManager.init(this)
        PreferenceUtils.init(this)

        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .flatsModule(FlatsModule())
                .build()
    }
}


