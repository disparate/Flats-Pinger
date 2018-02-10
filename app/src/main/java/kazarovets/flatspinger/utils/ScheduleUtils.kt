package kazarovets.flatspinger.utils

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import kazarovets.flatspinger.flats.FlatsJobSchedulerService


object ScheduleUtils {

    private val FLAT_NOTIFICATIONS_JOB_ID = 228

    fun scheduleFlatsNotificationsJob(context: Context) {
        val periodMs = DateUtils.HOUR_IN_MILLIS / 2
        val serviceName = ComponentName(context, FlatsJobSchedulerService::class.java)
        val job = JobInfo.Builder(FLAT_NOTIFICATIONS_JOB_ID, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(periodMs)
                .setPersisted(true)
                .build()

        val tm = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        if (!tm.allPendingJobs.contains(job)) {
            tm.schedule(job)
            Log.d("ScheduleUtils", "Scheduling job")
        } else {
            Log.d("ScheduleUtils", "job already scheduled")
        }
    }

    fun cancelScheduledJob(context: Context) {
        val tm = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        tm.cancel(FLAT_NOTIFICATIONS_JOB_ID)
        Log.d("ScheduleUtils", "Cancel job")
    }
}