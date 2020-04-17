package ua.itaysonlab.homefeeder.kt

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import ua.itaysonlab.homefeeder.utils.Logger

const val LIGHT_BORDER = 0.5f

fun Int.isLight() = ColorUtils.calculateLuminance(this) > LIGHT_BORDER
fun Int.isDark() = ColorUtils.calculateLuminance(this) < LIGHT_BORDER

fun Bundle.dump(tag: String) {
    keySet().forEach {
        val item = get(it)
        item ?: return@forEach
        Logger.log(tag, "[$it] $item")
    }
}

fun View.setLightFlags() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        systemUiVisibility = flags
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        var flags = systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        systemUiVisibility = flags
    }
}

fun View.clearLightFlags() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = systemUiVisibility
        flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        systemUiVisibility = flags
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        var flags = systemUiVisibility
        flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        systemUiVisibility = flags
    }
}

@ColorInt
fun Color?.toInt(): Int {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return Color.BLACK
    this ?: Color.BLACK
    return Color.rgb(this!!.red(), this.green(), this.blue())
}