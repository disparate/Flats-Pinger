package kazarovets.flatspinger.utils.extensions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.content.Context
import android.util.Log
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kazarovets.flatspinger.FlatsApplication
import kazarovets.flatspinger.di.AppComponent
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatFilter
import kazarovets.flatspinger.utils.FlatsFilterMatcher


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
            Single.just(lastThis.orEmpty())
                    .map { it + lastOther.orEmpty() }
                    .subscribe { list ->
                        postValue(list)
                    }
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

fun <A, B, T> combineLatest(liveDataA: LiveData<A>, liveDataB: LiveData<B>,
                            zipFunction: (A?, B?) -> T): LiveData<T> {
    return MediatorLiveData<T>().apply {
        var lastA: A? = null
        var lastB: B? = null

        var disposable: Disposable? = null

        fun update() {
            disposable?.dispose()
            disposable = Single.fromCallable { zipFunction(lastA, lastB) }
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
    }
}

