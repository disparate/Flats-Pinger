package kazarovets.flatspinger.service

import android.app.job.JobParameters
import android.app.job.JobService


class JobSchedulerService : JobService() {
    override fun onStopJob(p0: JobParameters?): Boolean = false

    override fun onStartJob(p0: JobParameters?): Boolean = false


}