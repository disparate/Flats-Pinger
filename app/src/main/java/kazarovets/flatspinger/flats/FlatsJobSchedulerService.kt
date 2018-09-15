package kazarovets.flatspinger.flats

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.Singles
import kazarovets.flatspinger.R
import kazarovets.flatspinger.activity.MainActivity
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatFilter
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.utils.FlatsFilterMatcher
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.utils.extensions.getAppComponent
import javax.inject.Inject


class FlatsJobSchedulerService : JobService() {

    companion object {
        const val NOTIFICATION_ID = 314
        const val NOTIFICATION_CHANNEL_ID = "new_flats"
        const val NOTIFICATION_CHANNEL_NAME = "New flats"

        const val TAG = "FlatsJob"
    }

    @Inject
    lateinit var flatsRepository: FlatsRepository

    private var disposable: Disposable? = null

    override fun onCreate() {
        super.onCreate()

        getAppComponent().inject(this)
    }


    override fun onStopJob(p0: JobParameters?): Boolean {
        releaseSubscription()
        return false
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        loadData(p0)

        return true
    }

    private fun loadData(params: JobParameters?) {
        Log.d("FlatsJob", "Starting job")

        disposable = getFilteredObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            onFlatsReceived(it)
                            Log.d(TAG, "Received ${it.size} flats")
                            releaseSubscription()
                            jobFinished(params, false)
                        },
                        { e ->
                            Log.e(TAG, "Error receiving flats", e)
                            onFlatsReceived(null)
                            releaseSubscription()
                            jobFinished(params, true)
                        }

                )
    }

    private fun getFilteredObservable(): Observable<List<Flat>> {
        val flatsFilter = PreferenceUtils.flatFilter
        val seenFlats = flatsRepository.getSeenFlatsFlowable().take(1).toObservable()
        val notRegularFlats = flatsRepository.getHiddenFlatsFlowable().take(1).toObservable()
        val flats = Singles.zip(flatsRepository.getRemoteFlats(Provider.I_NEED_A_FLAT),
                flatsRepository.getRemoteFlats(Provider.ONLINER)) { iNeedAFlat, onliner ->
            iNeedAFlat + onliner
        }

        return Observables.zip(flats.toObservable(),
                seenFlats,
                notRegularFlats, { remoteFlats, seenDbFlats, notRegularDbFlats ->
            remoteFlats.filter { filterFlat(it, seenDbFlats, notRegularDbFlats, flatsFilter) }
        })
    }

    private fun filterFlat(flat: Flat,
                           seenDbFlats: List<DBFlatInfo>,
                           notRegularDbFlats: List<DBFlatInfo>,
                           flatsFilter: FlatFilter): Boolean {
        return FlatsFilterMatcher.matches(flatsFilter, flat)
                && seenDbFlats.find { it.isSameIdAndProvider(flat) } == null
                && notRegularDbFlats.find { it.isSameIdAndProvider(flat) } == null
    }

    private fun onFlatsReceived(flats: List<Flat>?) {
        if (flats != null && flats.isNotEmpty()) {
            showNotification(flats)
        }
    }

    private fun showNotification(flats: List<Flat>) {
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.notification_title_new_flats))
                .setContentText(getString(R.string.notification_text_format, flats.size.toString()))
                .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNewFlatsChannel()
        } else {
            builder.setVibrate(longArrayOf(500, 500, 500, 500))
            builder.setLights(Color.RED, 3000, 3000)
            builder.priority = NotificationCompat.PRIORITY_HIGH
        }

        val resultIntent = Intent(this, MainActivity::class.java)

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        val resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentIntent(resultPendingIntent)

        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifManager.notify(NOTIFICATION_ID, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNewFlatsChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME, importance)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern = longArrayOf(500, 400, 500)
        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifManager.createNotificationChannel(notificationChannel)
    }

    private fun releaseSubscription() {
        if (disposable != null) {
            disposable?.dispose()
        }
        disposable = null
    }


}