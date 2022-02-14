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
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.databinding.FeedCardTextBinding
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
        view.findViewById<View>(R.id.wallpaper_dimmer).apply {
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
        view.findViewById<ViewGroup>(R.id.previewer).removeAllViews()
        bindLayout()
    }

    private fun bindLayout() {
        val not = FeedCardTextBinding.inflate(layoutInflater,null, false)
        view.findViewById<ViewGroup>(R.id.previewer).addView(not.root)
        bindPreview(not)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        view = holder.itemView
        if (!::layoutInflater.isInitialized) layoutInflater = LayoutInflater.from(holder.itemView.context)

        if (ContextCompat.checkSelfPermission(view.context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            view.findViewById<ImageView>(R.id.wallpaper_preview).setImageDrawable(WallpaperManager.getInstance(HFApplication.instance).drawable)
        }

        theme = getHFTheme(null)
        updateDimmer(overlayBgColorChoice, transparency)
        bindLayout()
    }

    @SuppressLint("SetTextI18n")
    private fun bindPreview(view: FeedCardTextBinding) {
        view.header.notAppIcon.setImageResource(R.drawable.ic_info_24)
        view.header.notAppName.text = "HomeFeeder"
        view.header.notAppSubtitle.text = "Subtitle"
        view.header.notAppDate.text = "Date"
        view.header.notTitle.text = "Settings Preview"
        view.header.notText.text = "This is a test of your HomeFeeder appearance settings."

        val cardBg = theme.get(Theming.Colors.CARD_BG.ordinal)
        if (!isCompact) {
            (view.root as? CardView)?.setCardBackgroundColor(cardBg)
        }

        val theme = if (cardBg.isDark()) Theming.defaultDarkThemeColors else Theming.defaultLightThemeColors
        view.header.notTitle.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
        view.header.notAppName.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
        view.header.notText.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
        view.header.notAppDate.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_SECONDARY.ordinal))
        view.header.notAppSubtitle.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_SECONDARY.ordinal))
        view.header.notAppIcon.imageTintList = ColorStateList.valueOf(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
    }
}