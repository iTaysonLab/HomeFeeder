package ua.itaysonlab.homefeeder.fragments.ui

import android.view.MenuItem
import androidx.preference.*
import ua.itaysonlab.homefeeder.utils.Logger

abstract class FixedPreferencesFragment : PreferenceFragmentCompat() {
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            activity!!.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setPreferenceScreen(preferenceScreen: PreferenceScreen?) {
        super.setPreferenceScreen(preferenceScreen)
        if (preferenceScreen != null) {
            val count = preferenceScreen.preferenceCount
            for (i in 0 until count) {
                val pref = preferenceScreen.getPreference(i)!!
                pref.isIconSpaceReserved = false
                if (pref is PreferenceCategory) {
                    val pcount = pref.preferenceCount
                    for (pi in 0 until pcount) {
                        pref.getPreference(pi).isIconSpaceReserved = false
                    }
                }
            }
        }
    }
}