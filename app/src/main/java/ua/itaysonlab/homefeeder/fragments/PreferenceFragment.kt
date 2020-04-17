package ua.itaysonlab.homefeeder.fragments

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.preference.*
import ua.itaysonlab.homefeeder.BuildConfig
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.activites.MainActivity
import ua.itaysonlab.homefeeder.fragments.base.FixedPreferencesFragment
import ua.itaysonlab.homefeeder.prefui.HFElementPreview

class PreferenceFragment : FixedPreferencesFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_general, rootKey)

        bindAppearance()
        if (BuildConfig.DEBUG) bindDebug()
    }

    private fun bindAppearance() {
        val summaryProviderInstance = ListPreference.SimpleSummaryProvider.getInstance()

        val theme = findPreference<ListPreference>("ovr_theme")!!
        val transparency = findPreference<ListPreference>("ovr_transparency")!!
        val overlayBackground = findPreference<ListPreference>("ovr_bg")!!
        val cardBackground = findPreference<ListPreference>("ovr_card_bg")!!

        val compact = findPreference<SwitchPreference>("ovr_compact")!!
        val sysColors = findPreference<SwitchPreference>("ovr_syscolors")!!

        val preview = findPreference<HFElementPreview>("preview")!!

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            sysColors.apply {
                isEnabled = false
                setSummary(R.string.pref_syscolors_disabled)
            }
        }

        theme.summaryProvider = summaryProviderInstance
        transparency.summaryProvider = summaryProviderInstance
        cardBackground.summaryProvider = summaryProviderInstance
        overlayBackground.summaryProvider = summaryProviderInstance

        theme.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            preview.applyNewTheme(newValue as String)
            HFApplication.bridge.getCallback()?.applyNewTheme(newValue)
            true
        }

        transparency.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            preview.applyNewTransparency(newValue as String)
            HFApplication.bridge.getCallback()?.applyNewTransparency(newValue)
            true
        }

        compact.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            preview.applyCompact(newValue as Boolean)
            HFApplication.bridge.getCallback()?.applyCompactCard(newValue)
            true
        }

        sysColors.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            HFApplication.bridge.getCallback()?.applySysColors(newValue as Boolean)
            true
        }

        overlayBackground.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            preview.applyNewOverlayBg(newValue as String)
            HFApplication.bridge.getCallback()?.applyNewOverlayBg(newValue)
            true
        }

        cardBackground.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            preview.applyNewCardBg(newValue as String)
            HFApplication.bridge.getCallback()?.applyNewCardBg(newValue)
            true
        }
    }

    private fun bindDebug() {
        val debugCategory = PreferenceCategory(preferenceManager.context).apply {
            title = "Debug"
            isIconSpaceReserved = false
        }
        val loggingSwitch = SwitchPreference(preferenceManager.context).apply {
            key = "HFDebugging"
            title = "Extensive logcat printing"
            setDefaultValue(false)
            isIconSpaceReserved = false
        }
        val contentLoggingSwitch = SwitchPreference(preferenceManager.context).apply {
            key = "HFContentDebugging"
            title = "Log notification content"
            setDefaultValue(false)
            isIconSpaceReserved = false
        }
        val sendToBridge = Preference(preferenceManager.context).apply {
            key = "HFBridgeTest"
            title = "Test OverlayBridge"
            summary = "Send message \"uiBridgeTest\" to Overlay"
            isIconSpaceReserved = false
        }
        sendToBridge.setOnPreferenceClickListener {
            if (HFApplication.bridge.isBridgeAlive()) {
                HFApplication.bridge.callServer("uiBridgeTest")
            } else {
                Toast.makeText(activity, "Bridge is not connected!", Toast.LENGTH_LONG).show()
            }
            true
        }
        preferenceScreen.addPreference(debugCategory)
        debugCategory.addPreference(loggingSwitch)
        debugCategory.addPreference(contentLoggingSwitch)
        debugCategory.addPreference(sendToBridge)
    }
}
