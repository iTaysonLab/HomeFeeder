package ua.itaysonlab.homefeeder

import androidx.core.graphics.ColorUtils

const val LIGHT_BORDER = 0.5f

fun Int.isLight(): Boolean {
    return ColorUtils.calculateLuminance(this) > LIGHT_BORDER
}

fun Int.isDark(): Boolean {
    return ColorUtils.calculateLuminance(this) < LIGHT_BORDER
}