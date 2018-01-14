package kazarovets.flatspinger.utils

import android.content.Context
import kazarovets.flatspinger.FlatsApplication
import kazarovets.flatspinger.di.AppComponent
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatFilter


fun <T : Flat> Iterable<T>.filterFlats(flatsFilter: FlatFilter?) = filter {
    FlatsFilterMatcher.matches(flatsFilter, it)
}

fun Context.getAppComponent(): AppComponent {
    return (this.applicationContext as FlatsApplication).appComponent
}