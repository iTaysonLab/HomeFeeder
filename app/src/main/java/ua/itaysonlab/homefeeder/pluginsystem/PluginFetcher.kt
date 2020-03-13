package ua.itaysonlab.homefeeder.pluginsystem

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import ua.itaysonlab.homefeeder.preferences.HFPreferences
import ua.itaysonlab.homefeeder.utils.Logger

object PluginFetcher {
    // List of available packages.
    val availablePlugins = hashMapOf<String, SlimPluginInfo>()

    // Required part for AIDL connection.
    const val INTENT_ACTION_SERVICE = "ua.itaysonlab.hfsdk.HOMEFEEDER_PLUGIN_SERVICE"

    // Plugin settings action
    private const val INTENT_ACTION_SETTINGS = "ua.itaysonlab.hfsdk.HOMEFEEDER_PLUGIN_ENTRYPOINT"

    // Metadata value acting for SDK version.
    private const val METADATA_SDK_VERSION = "HF_PluginSDK_Version"

    private const val METADATA_NAME = "HF_Plugin_Name"
    private const val METADATA_DESCRIPTION = "HF_Plugin_Description"
    private const val METADATA_AUTHOR = "HF_Plugin_Author"
    private const val METADATA_HAS_SETTINGS = "HF_Plugin_HasSettingsActivity"

    fun init(ctx: Context) {
        fillListBy(ctx.packageManager)
    }

    // Fill list of suitable packages.
    private fun fillListBy(packageManager: PackageManager) {
        availablePlugins.clear()

        val hasService = packageManager.queryIntentServices(
            Intent(INTENT_ACTION_SERVICE),
            PackageManager.GET_META_DATA
        ).map {
            Pair(it.serviceInfo.packageName, it.serviceInfo.metaData)
        }

        if (HFPreferences.debugging) {
            Logger.log("PluginFetcher", "Packages that has service: $hasService")
        }

        hasService.forEach {
            //Logger.log("PluginFetcher", "$it")
            availablePlugins[it.first] = SlimPluginInfo(
                it.first,
                hasPluginSettings = it.second.getBoolean(METADATA_HAS_SETTINGS),
                sdkVersion = it.second.getInt(METADATA_SDK_VERSION),
                title = it.second.getString(METADATA_NAME, ""),
                description = it.second.getString(METADATA_DESCRIPTION, ""),
                author = it.second.getString(METADATA_AUTHOR, "")
            )
        }
    }

    data class SlimPluginInfo(
        val pkg: String,
        val hasPluginSettings: Boolean,
        val sdkVersion: Int,
        val title: String,
        val description: String,
        val author: String
    )
}