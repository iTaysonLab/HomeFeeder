package ua.itaysonlab.homefeeder.prefui

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import kotlinx.android.synthetic.main.notification_generic_content.view.*
import kotlinx.android.synthetic.main.notification_simple.view.*
import kotlinx.android.synthetic.main.preview.view.*
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.kt.isDark
import ua.itaysonlab.homefeeder.overlay.launcherapi.OverlayThemeHolder
import ua.itaysonlab.homefeeder.preferences.HFPreferences
import ua.itaysonlab.homefeeder.theming.Theming

class HFElementPreview @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    init {
        widgetLayoutResource = R.layout.preview
    }

    private lateinit var view: View
    private lateinit var layoutInflater: LayoutInflater

    private var overlayBgColorChoice = HFPreferences.overlayBackground
    private var cardBgColorChoice = HFPreferences.cardBackground
    private var isCompact = HFPreferences.overlayCompact
    private var transparency = HFPreferences.overlayTransparency
    private var theme = SparseIntArray()

    fun applyNewTheme(value: String?) {
        theme = getHFTheme(value)
        requestRedraw()
    }

    fun applyNewOverlayBg(value: String) {
        overlayBgColorChoice = value
        updateDimmer(value, transparency)
    }

    fun applyNewCardBg(value: String) {
        cardBgColorChoice = value
        applyNewTheme(null)
    }

    fun applyNewTransparency(value: String) {
        transparency = value
        updateDimmer(overlayBgColorChoice, value)
    }

    private fun updateDimmer(bg: String, t: String) {
        view.wallpaper_dimmer.apply {
            alpha = when (t) {
                "less_half" -> 0.25f
                "half" -> 0.5f
                "more_half" -> 0.75f
                "non_transparent" -> 1f
                else -> 0f
            }

            setBackgroundColor(when(bg) {
                "white" -> ContextCompat.getColor(context, R.color.card_bg)
                "dark" -> ContextCompat.getColor(context, R.color.card_bg_dark)
                "amoled" -> Color.BLACK
                "launcher_primary" -> OverlayThemeHolder.primaryWallColor()
                "launcher_secondary" -> OverlayThemeHolder.secondaryWallColor()
                "launcher_tertiary" -> OverlayThemeHolder.tertiaryWallColor()
                else -> Color.BLACK
            })
        }
    }

    fun applyCompact(value: Boolean) {
        isCompact = value
        requestRedraw()
    }

    private fun getHFTheme(force: String?): SparseIntArray {
        val theme = when (force ?: HFPreferences.overlayTheme) {
            "auto_system" -> Theming.getThemeBySystem(context)
            "dark" -> Theming.defaultDarkThemeColors
            else -> Theming.defaultLightThemeColors
        }

        if (cardBgColorChoice != "theme") {
            theme.put(
                Theming.Colors.CARD_BG.ordinal, when (cardBgColorChoice) {
                    "white" -> ContextCompat.getColor(context, R.color.card_bg)
                    "dark" -> ContextCompat.getColor(context, R.color.card_bg_dark)
                    "amoled" -> Color.BLACK
                    "launcher_primary" -> OverlayThemeHolder.primaryWallColor()
                    "launcher_secondary" -> OverlayThemeHolder.secondaryWallColor()
                    "launcher_tertiary" -> OverlayThemeHolder.tertiaryWallColor()
                    else -> Color.BLACK
                }
            )
        }

        return theme
    }


    private fun requestRedraw() {
        view.previewer.removeAllViews()
        bindLayout()
    }

    private fun bindLayout() {
        val not: View = layoutInflater.inflate(R.layout.feed_card_text, null, false)
        view.previewer.addView(not)
        bindPreview(not)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        view = holder.itemView
        if (!::layoutInflater.isInitialized) layoutInflater = LayoutInflater.from(holder.itemView.context)

        if (ContextCompat.checkSelfPermission(view.context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            view.wallpaper_preview.setImageDrawable(WallpaperManager.getInstance(HFApplication.instance).drawable)
        }

        theme = getHFTheme(null)
        updateDimmer(overlayBgColorChoice, transparency)
        bindLayout()
    }

    @SuppressLint("SetTextI18n")
    private fun bindPreview(view: View) {
        view.not_app_icon.setImageResource(R.drawable.ic_info_24)
        view.not_app_name.text = "HomeFeeder"
        view.not_app_subtitle.text = "Subtitle"
        view.not_app_date.text = "Date"
        view.not_title.text = "Settings Preview"
        view.not_text.text = "This is a test of your HomeFeeder appearance settings."

        val cardBg = theme.get(Theming.Colors.CARD_BG.ordinal)
        if (!isCompact) {
            if (view is CardView) view.setCardBackgroundColor(cardBg)
        }

        val theme = if (cardBg.isDark()) Theming.defaultDarkThemeColors else Theming.defaultLightThemeColors
        view.not_title.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
        view.not_app_name.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
        view.not_text.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
        view.not_app_date.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_SECONDARY.ordinal))
        view.not_app_subtitle.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_SECONDARY.ordinal))
        view.not_app_icon.imageTintList = ColorStateList.valueOf(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
    }

}