package ua.itaysonlab.homefeeder.utils

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ua.itaysonlab.homefeeder.HFApplication

object PreferenceHelper {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(HFApplication.instance)

    fun get(key: String, default: Boolean): Boolean {
        return prefs.getBoolean(key, default)
    }

    fun get(key: String, default: String): String {
        return prefs.getString(key, default)!!
    }

    fun get(key: String, default: Int): Int {
        return prefs.getInt(key, default)
    }
}