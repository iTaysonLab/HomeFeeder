package ua.itaysonlab.homefeeder.utils

import android.os.Build
import android.view.View

object SNUtils {
    fun setLight(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            view.systemUiVisibility = flags
        }
    }

    fun removeLight(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var flags = view.systemUiVisibility
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            view.systemUiVisibility = flags
        }
    }
}