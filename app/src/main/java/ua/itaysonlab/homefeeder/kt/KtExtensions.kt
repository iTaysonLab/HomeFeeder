package ua.itaysonlab.homefeeder.kt

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import androidx.core.graphics.ColorUtils
import ua.itaysonlab.homefeeder.utils.Logger

const val LIGHT_BORDER = 0.5f

fun Int.isLight() = ColorUtils.calculateLuminance(this) > LIGHT_BORDER
fun Int.isDark() = ColorUtils.calculateLuminance(this) < LIGHT_BORDER

fun Context.isNotificationServiceGranted(): Boolean {
    val enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
    return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
}

fun Bundle.dump(tag: String) {
    keySet().forEach {
        val item = get(it)
        item ?: return@forEach
        Logger.log(tag, "[$it] $item")
    }
}