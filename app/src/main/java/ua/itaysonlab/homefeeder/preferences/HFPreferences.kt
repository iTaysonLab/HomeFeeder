package ua.itaysonlab.homefeeder.preferences

import ua.itaysonlab.homefeeder.utils.PreferenceHelper

object HFPreferences {
    val debugging get() = PreferenceHelper.get("HFDebugging", false)
    val contentDebugging get() = PreferenceHelper.get("HFContentDebugging", false)
    val overlayCompact get() = PreferenceHelper.get("ovr_compact", false)
    val systemColors get() = PreferenceHelper.get("ovr_syscolors", false)
    val overlayTheme get() = PreferenceHelper.get("ovr_theme", "auto_launcher")
    val overlayTransparency get() = PreferenceHelper.get("ovr_transparency", "non_transparent")
    val overlayBackground get() = PreferenceHelper.get("ovr_bg", "theme")
    val cardBackground get() = PreferenceHelper.get("ovr_card_bg", "theme")
}