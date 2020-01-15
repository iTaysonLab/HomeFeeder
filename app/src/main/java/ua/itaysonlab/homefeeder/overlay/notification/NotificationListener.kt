package ua.itaysonlab.homefeeder.overlay.notification

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import ua.itaysonlab.homefeeder.kt.dump
import ua.itaysonlab.homefeeder.preferences.HFPreferences
import ua.itaysonlab.homefeeder.utils.Logger
import java.util.ArrayList

class NotificationListener: NotificationListenerService() {
    companion object {
        const val LOG_TAG = "NotificationListener"
    }

    private val binder = LocalBinder()
    private val callbacks = ArrayList<NLCallback>()

    val notifications: List<NotificationWrapper> get() {
        val list = ArrayList<NotificationWrapper>()
        for (sbn in activeNotifications) {
            if (sbn.isOngoing && !sbn.notification.extras.containsKey(Notification.EXTRA_MEDIA_SESSION)) continue
            if (sbn.notification.extras.getString(Notification.EXTRA_TEMPLATE) == "android.app.Notification\$MessagingStyle") continue // TODO: make MessagingStyle
            if (HFPreferences.contentDebugging) {
                Logger.log(javaClass.simpleName, "===========")
                sbn.notification.extras.dump(LOG_TAG)
            }
            list.add(wrapNotification(sbn))
        }
        return list
    }

    fun addCallback(callback: NLCallback) {
        require(!callbacks.contains(callback)) { "This NLCallback is already in the list" }
        callbacks.add(callback)
    }

    fun removeCallback(callback: NLCallback) {
        require(callbacks.contains(callback)) { "Trying to remove NLCallback which is not in the list" }
        callbacks.remove(callback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return if (intent.action == SERVICE_INTERFACE) super.onBind(intent) else binder
    }

    private fun wrapNotification(sbn: StatusBarNotification): NotificationWrapper {
        return NotificationWrapper(sbn.notification, sbn.packageName, sbn.id, sbn.key)
    }

    fun requestNotificationDismiss(id: String) {
        cancelNotification(id)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        for (callback in callbacks) {
            callback.onNewNotification(wrapNotification(sbn))
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        for (callback in callbacks) {
            callback.onNotificationRemove(wrapNotification(sbn))
        }
    }

    inner class LocalBinder : Binder() {
        val service: NotificationListener get() = this@NotificationListener
    }

    interface NLCallback {
        fun onNewNotification(notification: NotificationWrapper)
        fun onNotificationRemove(notification: NotificationWrapper)
    }
}