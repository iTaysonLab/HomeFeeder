package ua.itaysonlab.homefeeder.preferences

import ua.itaysonlab.homefeeder.utils.PreferenceHelper

object HFPluginPreferences {
    private const val KEY = "HFEnabledPlugins"

    val enabledList get() = PreferenceHelper.getSet(KEY)

    fun add(pkg: String) {
        PreferenceHelper.setSet(KEY, enabledList.apply {
            add(pkg)
        })
    }

    fun remove(pkg: String) {
        PreferenceHelper.setSet(KEY, enabledList.apply {
            remove(pkg)
        })
    }
}