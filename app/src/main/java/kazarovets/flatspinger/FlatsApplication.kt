package kazarovets.flatspinger

import android.app.Application
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.utils.PreferenceUtils


class FlatsApplication : Application() {

    override fun onCreate() {
        ApiManager.init(this)
        PreferenceUtils.init(this)

    }
}