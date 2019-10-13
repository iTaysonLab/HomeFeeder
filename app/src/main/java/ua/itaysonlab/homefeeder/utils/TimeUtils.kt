package ua.itaysonlab.homefeeder.utils

import android.content.Context
import ua.itaysonlab.homefeeder.R
import java.util.Calendar
import java.util.Locale
import kotlin.math.min

object TimeUtils {
    private val calendar: Calendar
        get() = Calendar.getInstance()

    private val currentTime: Long
        get() = System.currentTimeMillis() / 1000

    private fun getAbsoluteDate(context: Context, i: Long): String {
        val resources = context.resources
        val j = i * 1000
        val calendar = calendar
        val i2 = calendar.get(Calendar.YEAR)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val timeInMillis = calendar.timeInMillis
        var j2 = 86400000.toLong()
        var j3 = timeInMillis + j2
        var j4 = j3 + j2
        j2 = timeInMillis - j2
        calendar.timeInMillis = j
        j4--
        val locale: Locale
        val str: String
        var format: String
        if (j in j3..j4) {
            locale = Locale.ENGLISH
            str = "%s %s %d:%02d"
            val tomorrow = resources.getString(R.string.tomorrow)
            val at = resources.getString(if (calendar.get(Calendar.HOUR_OF_DAY) == 1) R.string.date_at_1am else R.string.date_at)
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            format = String.format(locale, str, tomorrow, at, hourOfDay, minute)
            return format
        }
        j3--
        if (j in timeInMillis..j3) {
            locale = Locale.ENGLISH
            str = "%s %s %d:%02d"
            val today = resources.getString(R.string.today)
            val at = resources.getString(if (calendar.get(Calendar.HOUR_OF_DAY) == 1) R.string.date_at_1am else R.string.date_at)
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            format = String.format(locale, str, today, at, hourOfDay, minute)
            return format
        } else if (j < j2 || j >= timeInMillis) {
            val string: String = if (calendar.get(Calendar.YEAR) != i2) {
                resources.getString(R.string.date_format_day_month_year, calendar.get(Calendar.DAY_OF_MONTH), resources.getStringArray(R.array.date_shortmonths)[min(calendar.get(2), Calendar.HOUR_OF_DAY)], calendar.get(Calendar.YEAR))
            } else {
                resources.getString(R.string.date_format_day_month, calendar.get(Calendar.DAY_OF_MONTH), resources.getStringArray(R.array.date_shortmonths)[min(calendar.get(2), Calendar.HOUR_OF_DAY)])
            }
            val stringBuilder = StringBuilder()
            stringBuilder.append(string)
            locale = Locale.ENGLISH
            val str2 = " %s %d:%02d"
            val at = resources.getString(if (calendar.get(Calendar.HOUR_OF_DAY) == 1) R.string.date_at_1am else R.string.date_at)
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            format = String.format(locale, str2, at, hourOfDay, minute)
            stringBuilder.append(format)
            format = stringBuilder.toString()
            return format
        } else {
            locale = Locale.ENGLISH
            str = "%s %s %d:%02d"
            val yesterday = resources.getString(R.string.yesterday)
            val at = resources.getString(if (calendar.get(Calendar.HOUR_OF_DAY) == 1) R.string.date_at_1am else R.string.date_at)
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            format = String.format(locale, str, yesterday, at, hourOfDay, minute)
            return format
        }
    }

    /**
     * @param context - Context to use with resources
     * @param i - Time in seconds (Unix timestamp)
     * @return Time relative to this
     */
    fun getDateFormattedRelative(context: Context, i: Long): String {
        val resources = context.resources
        val currentTime = currentTime - i
        if (currentTime >= 14400 || currentTime < 0) {
            return getAbsoluteDate(context, i)
        }
        return when {
            currentTime >= 10800 -> {
                resources.getStringArray(R.array.date_ago_hrs)[2]
            }
            currentTime >= 7200 -> {
                resources.getStringArray(R.array.date_ago_hrs)[1]
            }
            currentTime >= 3600 -> {
                resources.getStringArray(R.array.date_ago_hrs)[0]
            }
            currentTime >= 60 -> {
                resources.getQuantityString(R.plurals.date_ago_mins, (currentTime / 60).toInt(), (currentTime / 60).toInt())
            }
            currentTime <= 10 -> {
                resources.getString(R.string.date_ago_now)
            }
            else -> {
                resources.getQuantityString(R.plurals.date_ago_secs, currentTime.toInt(), currentTime.toInt())
            }
        }
    }
}