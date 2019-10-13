package ua.itaysonlab.homefeeder.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import ua.itaysonlab.homefeeder.BuildConfig
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.fragments.base.FixedPreferencesFragment

class AboutFragment : FixedPreferencesFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_about, rootKey)

        findPreference<Preference>("about_app")!!.summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

        val tg = findPreference<Preference>("about_tg")!!
        val tgdev = findPreference<Preference>("about_tg_dev")!!
        val git = findPreference<Preference>("about_source")!!

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
        git.setOnPreferenceClickListener {
            openLink(activity!!, "https://gitlab.com/iTaysonLab/homefeeder/")
            true
        }
    }

    private fun openLink(context: Context, url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
