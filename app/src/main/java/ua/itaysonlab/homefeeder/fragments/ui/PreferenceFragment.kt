package ua.itaysonlab.homefeeder.fragments.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import ua.itaysonlab.homefeeder.BuildConfig
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.activites.MainActivity

class PreferenceFragment : FixedPreferencesFragment() {
    private fun reloadTheme(new: String) {
        HFApplication.bridge.callServer("reloadTheme:$new")
    }

    private fun reloadTransparent(new: String) {
        HFApplication.bridge.callServer("reloadTransparent:$new")
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_general, rootKey)
        findPreference<ListPreference>("ovr_theme")!!.summaryProvider =
            ListPreference.SimpleSummaryProvider.getInstance()
        findPreference<ListPreference>("ovr_theme")!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                reloadTheme(newValue as String)
                true
            }
        findPreference<ListPreference>("ovr_transparency")!!.summaryProvider =
            ListPreference.SimpleSummaryProvider.getInstance()
        findPreference<ListPreference>("ovr_transparency")!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                reloadTransparent(newValue as String)
                true
            }

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

        if (BuildConfig.DEBUG) {
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
            debugCategory.addPreference(sendToBridge)
        }
    }
}
