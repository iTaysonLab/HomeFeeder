package ua.itaysonlab.homefeeder.preferences

import ua.itaysonlab.homefeeder.utils.Preferences

object HFPreferences {
    val debugging get() = Preferences.get("HFDebugging", false)
    val overlayTheme get() = Preferences.get("ovr_theme", "auto_launcher")
    val overlayTransparency get() = Preferences.get("ovr_transparency", "non_transparent")
}