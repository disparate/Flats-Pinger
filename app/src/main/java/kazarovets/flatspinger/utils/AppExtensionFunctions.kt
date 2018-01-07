package kazarovets.flatspinger.utils

import android.content.Context
import kazarovets.flatspinger.FlatsApplication
import kazarovets.flatspinger.di.AppComponent
import kazarovets.flatspinger.model.FlatInfo


public fun Iterable<FlatInfo>.filterFlats() = filter {
    val flatsFilter = PreferenceUtils.flatFilter
    FlatsFilterMatcher.matches(flatsFilter, it)
}

public fun Context.getAppComponent(): AppComponent {
    return (this.applicationContext as FlatsApplication).appComponent
}