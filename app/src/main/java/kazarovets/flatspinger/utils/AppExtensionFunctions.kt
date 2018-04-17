package kazarovets.flatspinger.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.content.Context
import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
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

fun <A, B, C, D, T> zip(liveDataA: LiveData<A>, liveDataB: LiveData<B>,
                        liveDataC: LiveData<C>, liveDataD: LiveData<D>,
                        zipFunction: (A?, B?, C?, D?) -> T?): LiveData<T> {
    return MediatorLiveData<T>().apply {
        var lastA: A? = null
        var lastB: B? = null
        var lastC: C? = null
        var lastD: D? = null

        fun update() {
            Single.fromCallable { zipFunction(lastA, lastB, lastC, lastD) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe({
                        postValue(it)
                    }, { error ->
                        Log.d("ZipLiveData", "error zipping data", error)
                    })
        }

        addSource(liveDataA) {
            lastA = it
            update()
        }
        addSource(liveDataB) {
            lastB = it
            update()
        }
        addSource(liveDataC) {
            lastC = it
            update()
        }
        addSource(liveDataD) {
            lastD = it
            update()
        }
    }
}