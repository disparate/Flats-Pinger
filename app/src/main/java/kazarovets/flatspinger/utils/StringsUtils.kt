package kazarovets.flatspinger.utils

import android.content.Context
import android.text.format.DateUtils
import kazarovets.flatspinger.R
import java.util.concurrent.TimeUnit


class StringsUtils {
    companion object {
        fun getTimeAgoString(time: Long, context: Context): String {
            var diff = System.currentTimeMillis() - time
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            diff -= days * DateUtils.DAY_IN_MILLIS
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            diff -= hours * DateUtils.HOUR_IN_MILLIS
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            val showDays = days > 0
            val showHours = hours > 0 && (!showDays || days < 2)
            val showMinutes = days < 1 && hours < 1
            return "${if (showDays) "${days}${context.getString(R.string.day_small)}" else ""} " +
                    "${if (showHours) "${hours}${context.getString(R.string.hour_small)}" else ""}" +
                    "${if (showMinutes) "${minutes}${context.getString(R.string.minute_small)}" else ""}" +
                    " " + context.getString(R.string.time_ago)
        }
    }
}