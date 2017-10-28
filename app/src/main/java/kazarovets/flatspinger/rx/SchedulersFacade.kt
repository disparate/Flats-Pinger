package kazarovets.flatspinger.rx

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class SchedulersFacade @Inject constructor() {

    /**
     * IO thread pool scheduler
     */
    fun io(): Scheduler = Schedulers.io()

    /**
     * Computation thread pool scheduler
     */
    fun computation(): Scheduler = Schedulers.computation()

    /**
     * Main Thread scheduler
     */
    fun ui(): Scheduler = AndroidSchedulers.mainThread()
}