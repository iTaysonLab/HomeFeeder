package ua.itaysonlab.homefeeder.utils

import android.content.Context
import android.content.res.Configuration
import android.util.SparseIntArray
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R

object ThemeUtils {
    val defaultLightThemeColors = createLightTheme()
    val defaultDarkThemeColors = createDarkTheme()

    private fun createLightTheme(): SparseIntArray {
        return SparseIntArray().apply {
            addBasicThings(this)
            put(Colors.CARD_BG.position, ContextCompat.getColor(HFApplication.instance, R.color.card_bg))
            put(Colors.TEXT_COLOR_PRIMARY.position, ContextCompat.getColor(HFApplication.instance, R.color.text_color_primary))
            put(Colors.TEXT_COLOR_SECONDARY.position, ContextCompat.getColor(HFApplication.instance, R.color.text_color_secondary))
            put(Colors.OVERLAY_BG.position, ContextCompat.getColor(HFApplication.instance, R.color.bg_overlay))
            put(Colors.IS_LIGHT.position, 1)
        }
    }

    private fun createDarkTheme(): SparseIntArray {
        return SparseIntArray().apply {
            addBasicThings(this)
            put(Colors.CARD_BG.position, ContextCompat.getColor(HFApplication.instance, R.color.card_bg_dark))
            put(Colors.TEXT_COLOR_PRIMARY.position, ContextCompat.getColor(HFApplication.instance, R.color.text_color_primary_dark))
            put(Colors.TEXT_COLOR_SECONDARY.position, ContextCompat.getColor(HFApplication.instance, R.color.text_color_secondary_dark))
            put(Colors.OVERLAY_BG.position, ContextCompat.getColor(HFApplication.instance, R.color.bg_dark))
            put(Colors.IS_LIGHT.position, 0)
        }
    }

    private fun addBasicThings(array: SparseIntArray): SparseIntArray {
        return array.apply {
            put(Colors.ACCENT_COLOR.position, ContextCompat.getColor(HFApplication.instance, R.color.globalAccent))
        }
    }

    fun getThemeBySystem(ctx: Context): SparseIntArray {
        return if (ctx.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) createDarkTheme() else createLightTheme()
    }

    enum class Colors(val position: Int) {
        CARD_BG(0), TEXT_COLOR_PRIMARY(1), TEXT_COLOR_SECONDARY(2), ACCENT_COLOR(3), OVERLAY_BG(4), IS_LIGHT(5)
    }
}