package ua.itaysonlab.homefeeder.theming

import android.content.Context
import android.content.res.Configuration
import android.util.SparseIntArray
import androidx.core.content.ContextCompat
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R

object Theming {
    val defaultLightThemeColors = createLightTheme()
    val defaultDarkThemeColors = createDarkTheme()

    private fun createLightTheme(): SparseIntArray {
        return SparseIntArray().apply {
            addBasicThings(this)
            put(Colors.CARD_BG.ordinal, ContextCompat.getColor(HFApplication.instance, R.color.card_bg))
            put(Colors.TEXT_COLOR_PRIMARY.ordinal, ContextCompat.getColor(HFApplication.instance, R.color.text_color_primary))
            put(Colors.TEXT_COLOR_SECONDARY.ordinal, ContextCompat.getColor(HFApplication.instance, R.color.text_color_secondary))
            put(Colors.OVERLAY_BG.ordinal, ContextCompat.getColor(HFApplication.instance, R.color.bg_overlay))
            put(Colors.IS_LIGHT.ordinal, 1)
        }
    }

    private fun createDarkTheme(): SparseIntArray {
        return SparseIntArray().apply {
            addBasicThings(this)
            put(Colors.CARD_BG.ordinal, ContextCompat.getColor(HFApplication.instance, R.color.card_bg_dark))
            put(Colors.TEXT_COLOR_PRIMARY.ordinal, ContextCompat.getColor(HFApplication.instance, R.color.text_color_primary_dark))
            put(Colors.TEXT_COLOR_SECONDARY.ordinal, ContextCompat.getColor(HFApplication.instance, R.color.text_color_secondary_dark))
            put(Colors.OVERLAY_BG.ordinal, ContextCompat.getColor(HFApplication.instance, R.color.bg_dark))
            put(Colors.IS_LIGHT.ordinal, 0)
        }
    }

    private fun addBasicThings(array: SparseIntArray): SparseIntArray {
        return array.apply {
            put(Colors.ACCENT_COLOR.ordinal, ContextCompat.getColor(HFApplication.instance, R.color.globalAccent))
        }
    }

    fun getThemeBySystem(ctx: Context): SparseIntArray {
        return if (ctx.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) createDarkTheme() else createLightTheme()
    }

    enum class Colors {
        CARD_BG,
        TEXT_COLOR_PRIMARY,
        TEXT_COLOR_SECONDARY,
        ACCENT_COLOR,
        OVERLAY_BG,
        IS_LIGHT
    }
}