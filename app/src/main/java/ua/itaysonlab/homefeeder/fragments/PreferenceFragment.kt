package ua.itaysonlab.homefeeder.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.preference.*
import ua.itaysonlab.homefeeder.BuildConfig
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.activites.MainActivity
import ua.itaysonlab.homefeeder.fragments.base.FixedPreferencesFragment

class PreferenceFragment : FixedPreferencesFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_general, rootKey)

        bindPermissionHeader()
        bindAppearance()
        if (BuildConfig.DEBUG) bindDebug()
    }

    private fun bindPermissionHeader() {
        val permission = findPreference<Preference>("hf_permission")!!
        if ((activity as MainActivity).isNotificationServiceEnabled()) {
            permission.setIcon(R.drawable.ic_notifications_24)
            permission.setSummary(R.string.allow_notify_pref_granted)
        } else {
            permission.setOnPreferenceClickListener {
                (activity as MainActivity).requestNotificationPermission()
                true
            }
        }
    }

    private fun bindAppearance() {
        val theme = findPreference<ListPreference>("ovr_theme")!!
        val transparency = findPreference<ListPreference>("ovr_transparency")!!
        val compact = findPreference<SwitchPreference>("ovr_compact")!!

        theme.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        theme.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            HFApplication.bridge.callServer("reloadTheme:${newValue as String}")
            true
        }
        transparency.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        transparency.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            HFApplication.bridge.callServer("reloadTransparent:${newValue as String}")
            true
        }

        compact.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            HFApplication.bridge.callServer("reloadCompact:${newValue as Boolean}")
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
            title = "Test UIBridge"
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
