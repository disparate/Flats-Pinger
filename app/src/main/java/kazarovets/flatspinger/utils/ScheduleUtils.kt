package kazarovets.flatspinger.utils

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.text.format.DateUtils
import kazarovets.flatspinger.activity.MainActivity
import kazarovets.flatspinger.service.JobSchedulerService


class ScheduleUtils {

    companion object {
        fun scheduleFlatsNotificationsJob(context: Context) {
            val periodMs = DateUtils.HOUR_IN_MILLIS / 2
            val serviceName = ComponentName(context, JobSchedulerService::class.java)
            val job = JobInfo.Builder(MainActivity.FLAT_NOTIFICATIONS_JOB_ID, serviceName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(periodMs)
                    .setPersisted(true)
                    .build()

            val tm = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            if (!tm.allPendingJobs.contains(job)) {
                tm.schedule(job)
            }
        }
    }
}