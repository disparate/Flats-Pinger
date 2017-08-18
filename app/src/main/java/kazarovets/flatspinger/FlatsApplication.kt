package kazarovets.flatspinger

import android.app.Application
import kazarovets.flatspinger.api.ApiManager


class FlatsApplication : Application() {

    override fun onCreate() {
        ApiManager.init(this)
    }
}