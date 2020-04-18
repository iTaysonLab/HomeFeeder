package ua.itaysonlab.homefeeder.overlay.launcherapi

import android.annotation.TargetApi
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.SparseIntArray
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.kt.clearLightFlags
import ua.itaysonlab.homefeeder.kt.isLight
import ua.itaysonlab.homefeeder.kt.toInt
import ua.itaysonlab.homefeeder.overlay.OverlayKt
import ua.itaysonlab.homefeeder.preferences.HFPreferences
import ua.itaysonlab.homefeeder.theming.Theming
import ua.itaysonlab.homefeeder.utils.Logger

/**
 * A class which manages overlay styling.
 * */
class OverlayThemeHolder(private val context: Context, private val overlay: OverlayKt) {
    /**
     * Current theme colors mapping
     */
    var currentTheme = Theming.defaultDarkThemeColors

    /**
     * Card background
     */
    var cardBgPref = HFPreferences.cardBackground

    /**
     * Overlay background
     */
    var overlayBgPref = HFPreferences.overlayBackground

    /**
     * Overlay transparency
     */
    var transparencyBgPref = HFPreferences.overlayTransparency

    /**
     * If we should apply light statusbar/navbar
     */
    var shouldUseSN = false

    /**
     * If we are applied light statusbar/navbar
     */
    var isSNApplied = false

    /**
     * If we are using system colors instead of [LauncherAPI]
     */
    var systemColors = false

    /**
     * Parses [cardBgPref] into color integer
     */
    private val cardBackground: Int get() = when (cardBgPref) {
        "white" -> ContextCompat.getColor(context, R.color.card_bg)
        "dark" -> ContextCompat.getColor(context, R.color.card_bg_dark)
        "launcher_primary" -> if (HFPreferences.systemColors) primaryWallColor() else overlay.apiInstance.backgroundColorHint
        "launcher_secondary" -> if (HFPreferences.systemColors) secondaryWallColor() else overlay.apiInstance.backgroundColorHintSecondary
        "launcher_tertiary" -> if (HFPreferences.systemColors) tertiaryWallColor() else overlay.apiInstance.backgroundColorHintTertiary
        else -> Color.BLACK
    }

    /**
     * Parses [overlayBgPref] into color integer
     */
    private val overlayBackground: Int get() = when (overlayBgPref) {
        "white" -> Color.WHITE
        "dark" -> ContextCompat.getColor(context, R.color.bg_dark)
        "launcher_primary" -> if (HFPreferences.systemColors) primaryWallColor() else overlay.apiInstance.backgroundColorHint
        "launcher_secondary" -> if (HFPreferences.systemColors) secondaryWallColor() else overlay.apiInstance.backgroundColorHintSecondary
        "launcher_tertiary" -> if (HFPreferences.systemColors) tertiaryWallColor() else overlay.apiInstance.backgroundColorHintTertiary
        else -> Color.BLACK
    }

    /**
     * If [transparencyBgPref] is fully transparent
     */
    val isTransparentBg get() = transparencyBgPref == "transparent"

    /**
     * Parses scroll alpha to match theme
     */
    fun getScrollAlpha(alpha: Float): Float {
        return if (transparencyBgPref == "half" && alpha > 0.5f) {
            0.5f
        } else if (transparencyBgPref == "less_half" && alpha > 0.25f) {
            0.25f
        } else if (transparencyBgPref == "more_half" && alpha > 0.75f) {
            0.75f
        } else alpha
    }

    /**
     * Replaces the color mapping ([currentTheme]) with config-specified values
     */
    fun setTheme(theme: SparseIntArray) {
        currentTheme = theme

        shouldUseSN = if (overlayBgPref != "theme") {
            currentTheme.put(Theming.Colors.OVERLAY_BG.ordinal, overlayBackground)
            overlayBackground.isLight()
        } else {
            currentTheme.get(Theming.Colors.IS_LIGHT.ordinal) == 1
        }

        if (!shouldUseSN && isSNApplied) {
            isSNApplied = false
            overlay.window.decorView.clearLightFlags()
        }

        if (cardBgPref != "theme") {
            currentTheme.put(Theming.Colors.CARD_BG.ordinal, cardBackground)
        }

        Logger.log("OTH", currentTheme.toString())
    }

    companion object {
        @ColorInt
        @TargetApi(Build.VERSION_CODES.O_MR1)
        fun primaryWallColor(): Int {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return Color.BLACK
            val wc = WallpaperManager.getInstance(HFApplication.instance).getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
            wc ?: Color.BLACK
            return wc!!.primaryColor.toInt()
        }

        @ColorInt
        @TargetApi(Build.VERSION_CODES.O_MR1)
        fun secondaryWallColor(): Int {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return Color.BLACK
            val wc = WallpaperManager.getInstance(HFApplication.instance).getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
            wc ?: Color.BLACK
            return wc!!.secondaryColor.toInt()
        }

        @ColorInt
        @TargetApi(Build.VERSION_CODES.O_MR1)
        fun tertiaryWallColor(): Int {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return Color.BLACK
            val wc = WallpaperManager.getInstance(HFApplication.instance).getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
            wc ?: Color.BLACK
            return wc!!.tertiaryColor.toInt()
        }
    }
}