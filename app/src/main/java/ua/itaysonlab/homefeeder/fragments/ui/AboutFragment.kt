package ua.itaysonlab.homefeeder.fragments.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import ua.itaysonlab.homefeeder.BuildConfig
import ua.itaysonlab.homefeeder.R

class AboutFragment : FixedPreferencesFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_about, rootKey)

        findPreference<Preference>("about_app")!!.summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

        val tg = findPreference<Preference>("about_tg")!!
        val tgdev = findPreference<Preference>("about_tg_dev")!!

        tg.summary = "@homefeeder"
        tg.setOnPreferenceClickListener {
            openLink(activity!!, "https://t.me/homefeeder")
            true
        }
        tgdev.summary = "@itaysonlab"
        tgdev.setOnPreferenceClickListener {
            openLink(activity!!, "https://t.me/itaysonlab")
            true
        }
    }

    private fun openLink(context: Context, url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
