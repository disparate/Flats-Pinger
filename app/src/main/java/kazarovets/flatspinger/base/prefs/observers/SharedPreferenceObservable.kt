package kazarovets.flatspinger.base.prefs.observers

import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.Observable.create
import io.reactivex.Observer
import io.reactivex.disposables.Disposables


abstract class SharedPreferenceObservable<T>(val sharedPrefs: SharedPreferences,
                                             private val key: String,
                                             private val defValue: T) : Observable<T>() {

    abstract fun getValueFromPreferences(key: String, defValue: T): T

    private val observable: Observable<T> by lazy {
        create<T>({ emitter ->
            val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == this@SharedPreferenceObservable.key) {
                    if (emitter.isDisposed.not()) {
                        emitter.onNext(getValueFromPreferences(key, defValue))
                    }
                }
            }
            sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
            emitter.setDisposable(Disposables.fromAction {
                sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
            })
            if (emitter.isDisposed.not()) {
                emitter.onNext(getValueFromPreferences(key, defValue))
            }
        })
    }

    override fun subscribeActual(observer: Observer<in T>) {
        observable.subscribe(observer)
    }
}
