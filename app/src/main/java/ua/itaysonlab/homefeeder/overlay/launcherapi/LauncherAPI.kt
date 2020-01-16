package ua.itaysonlab.homefeeder.overlay.launcherapi

import android.graphics.Color
import android.os.Bundle
import androidx.annotation.ColorInt
import ua.itaysonlab.homefeeder.kt.dump
import ua.itaysonlab.homefeeder.preferences.HFPreferences

/**
 * A class which parses data from launcher's options [android.os.Bundle].
 * */
class LauncherAPI(bundle: Bundle = Bundle()) {
    companion object {
        const val LOG_TAG = "LauncherAPI"

        const val DARK_THEME_KEY = "is_background_dark"
        const val BG_HINT_KEY = "background_color_hint"
        const val BG_SECONDARY_HINT_KEY = "background_secondary_color_hint"
        const val BG_TERTIARY_HINT_KEY = "background_tertiary_color_hint"
    }

    /**
     * If the wallpaper is dark enough.
     * Available on almost every launcher
     */
    var darkTheme = false

    /**
     * Primary wallpaper color.
     * Seems like it's available only on Lawnchair.
     * On Shade, it returns applied theme color.
     */
    @ColorInt
    var backgroundColorHint = Color.BLACK

    /**
     * Secondary wallpaper color.
     * Only on Lawnchair (and Librechair too).
     */
    @ColorInt
    var backgroundColorHintSecondary = Color.BLACK

    /**
     * Tertiary wallpaper color.
     * Available only on Librechair.
     */
    @ColorInt
    var backgroundColorHintTertiary = Color.BLACK

    init {
        if (HFPreferences.contentDebugging) {
            bundle.dump(LOG_TAG)
        }

        darkTheme = bundle.getBoolean(DARK_THEME_KEY, true)
        backgroundColorHint = bundle.getInt(BG_HINT_KEY, Color.BLACK)
        backgroundColorHintSecondary = bundle.getInt(BG_SECONDARY_HINT_KEY, Color.BLACK)
        backgroundColorHintTertiary = bundle.getInt(BG_TERTIARY_HINT_KEY, Color.BLACK)
    }
}