package kazarovets.flatspinger

import android.app.Application
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.utils.PreferenceUtils


class FlatsApplication : Application() {

    companion object {
        val DEFAULT_NUMBER_OF_DAYS_AD_IS_ACTUAL = 10
    }

    override fun onCreate() {
        ApiManager.init(this)
        PreferenceUtils.init(this)
    }
}