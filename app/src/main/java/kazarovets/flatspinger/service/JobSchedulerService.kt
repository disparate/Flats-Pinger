package kazarovets.flatspinger.service

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
import io.reactivex.observers.DisposableSingleObserver
import kazarovets.flatspinger.R
import kazarovets.flatspinger.activity.MainActivity
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.db.FlatsDatabase
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatInfo
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.utils.FlatsFilterMatcher
import kazarovets.flatspinger.utils.PreferenceUtils


class JobSchedulerService : JobService() {

    companion object {
        val NOTIFICATION_ID = 314
        val NOTIFICATION_CHANNEL_ID = "new_flats"
        val NOTIFICATION_CHANNEL_NAME = "New flats"
    }

    private var disposable: Disposable? = null


    override fun onStopJob(p0: JobParameters?): Boolean {
        releaseSubscription()
        return false
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        loadData(p0)

        return true
    }

    private fun loadData(params: JobParameters?) {
        val minCost = PreferenceUtils.minCost
        val maxCost = PreferenceUtils.maxCost
        val allowAgency = PreferenceUtils.allowAgency
        val rooms = PreferenceUtils.roomNumbers
        val flatsFilter = PreferenceUtils.flatFilter
        disposable = ApiManager.iNeedAFlatApi
                .getFlats(minCost?.toDouble(), maxCost?.toDouble(), allowAgency, rooms)
                .map { it as List<Flat> }
                .mergeWith(ApiManager.onlinerApi.getLatestFlats(minCost, maxCost, !allowAgency, rooms))
                .toObservable()
                .flatMap { Observable.fromIterable(it) }
                .filter { FlatsFilterMatcher.matches(flatsFilter, it) }
                .filter {
                    val db = FlatsDatabase.getInstance(applicationContext)
                    !db.isSeenFlat(it.getId(), it.getProvider())
                            && db.getFlatStatus(it.getId(), it.getProvider()) == FlatStatus.REGULAR

                }
                .toSortedList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Flat>>() {
                    override fun onError(e: Throwable) {
                        Log.e("FlatsListFragment", "Error receiving flats", e)
                        onFlatsReceived(null)
                        releaseSubscription()
                        jobFinished(params, true)
                    }

                    override fun onSuccess(flats: List<Flat>) {
                        onFlatsReceived(flats)
                        releaseSubscription()
                        jobFinished(params, false)
                    }

                })
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
//                .setContentText("${flats.size} ${getString(R.string)}")
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
        notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
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