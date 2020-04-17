package ua.itaysonlab.homefeeder.pluginsystem

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import ua.itaysonlab.hfsdk.FeedCategory
import ua.itaysonlab.hfsdk.FeedItem
import ua.itaysonlab.hfsdk.IFeedInterface
import ua.itaysonlab.hfsdk.IFeedInterfaceCallback
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.preferences.HFPluginPreferences
import ua.itaysonlab.homefeeder.utils.Logger

object PluginConnector {
    const val TAG = "PluginConnector"
    private var hasInitialized = false

    private var interfaces = hashMapOf<String, IFeedInterface?>()
    private var callbacks = hashMapOf<String, IFeedInterfaceCallback?>()

    private var index = 0
    private var serviceSize = 0

    private val handler = Handler(Looper.getMainLooper())

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Logger.log(TAG, "Connected to service ${componentName.packageName} / ${componentName.className}")
            interfaces[componentName.packageName] = IFeedInterface.Stub.asInterface(iBinder)
            interfaces[componentName.packageName]!!.getFeed(callbacks[componentName.packageName], 0, null, null)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Logger.log(TAG, "Disconnected from service ${componentName.packageName} / ${componentName.className}")
            interfaces[componentName.packageName] = null
        }
    }

    private fun connectTo(pkg: String) {
        Logger.log(TAG, "Connecting to: $pkg")
        val intent = Intent("$pkg.HFPluginService")
        intent.action = PluginFetcher.INTENT_ACTION_SERVICE
        intent.setPackage(pkg)
        HFApplication.instance.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
    }

    fun clear() {
        index = 0
        serviceSize = 0
        interfaces.clear()
        callbacks.clear()
        hasInitialized = false
    }

    fun getFeedAsItLoads(page: Int, onNewFeed: (List<FeedItem>) -> Unit, onLoadFinished: () -> Unit) {
        Logger.log(TAG, "getFeedAsItLoads")

        val stub = object: IFeedInterfaceCallback.Stub() {
            override fun onCategoriesReceive(categories: MutableList<FeedCategory>?) {

            }

            override fun onFeedReceive(feed: MutableList<FeedItem>?) {
                feed ?: return

                Logger.log(TAG, "Received feed: $feed")

                onNewFeed(feed)

                index++
                if (index >= serviceSize) {
                    Logger.log(TAG, "Finished chain!")
                    handler.post {
                        onLoadFinished()
                    }
                }
            }
        }

        if (hasInitialized) {
            index = 0
            interfaces.forEach {
                it.value?.getFeed(stub, page, null, null)
            }
            return
        }

        chainLoad(stub)
        hasInitialized = true
    }

    private fun chainLoad(cb: IFeedInterfaceCallback) {
        index = 0
        serviceSize = HFPluginPreferences.enabledList.size
        HFPluginPreferences.enabledList.forEach {
            callbacks[it] = cb
            connectTo(it)
        }
    }
}