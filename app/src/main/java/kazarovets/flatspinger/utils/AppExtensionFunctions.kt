package kazarovets.flatspinger.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
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

fun <A> LiveData<out List<A>>.mergeWith(other: LiveData<out List<A>>): LiveData<List<A>> {
    return MediatorLiveData<List<A>>().apply {
        var lastThis: List<A>? = null
        var lastOther: List<A>? = null

        fun update() {
            this.value = (lastThis ?: ArrayList()).plus(lastOther ?: emptyList())
        }

        addSource(this@mergeWith) {
            lastThis = it
            update()
        }
        addSource(other) {
            lastOther = it
            update()
        }
    }
}