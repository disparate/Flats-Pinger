package kazarovets.flatspinger

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.di.AppModule
import kazarovets.flatspinger.di.DaggerAppComponent
import kazarovets.flatspinger.utils.PreferenceUtils
import javax.inject.Inject


class FlatsApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    companion object {
        val DEFAULT_NUMBER_OF_DAYS_AD_IS_ACTUAL = 10
    }

    override fun onCreate() {
        super.onCreate()

        ApiManager.init(this)
        PreferenceUtils.init(this)

        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
                .inject(this)

    }


}