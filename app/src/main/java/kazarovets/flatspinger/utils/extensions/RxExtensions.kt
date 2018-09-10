package kazarovets.flatspinger.utils.extensions

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kazarovets.flatspinger.rx.Optional


fun <T> Observable<out T>.wrapInOptional(startWithEmpty: Boolean): Observable<Optional<T>> {
    val obs = this.map { Optional.of(it) }
    return if (startWithEmpty) {
        Observable.just(Optional.empty<T>()).mergeWith(obs)
//        obs.startWith { Optional.empty<T>() }
    } else {
        obs
    }
}