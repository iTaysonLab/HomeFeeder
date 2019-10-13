package ua.itaysonlab.homefeeder.utils

import ua.itaysonlab.homefeeder.overlay.notification.NotificationListener

class UIBridge {
    private lateinit var callback: UIBridgeCallback

    fun callServer(action: String) {
        if (isBridgeAlive()) callback.onClientMessage(action)
    }

    fun getNotificationListener(): NotificationListener {
        return callback.getNotificationListener()
    }

    fun setCallback(callback: UIBridgeCallback) {
        this.callback = callback
    }

    fun isBridgeAlive() = ::callback.isInitialized

    interface UIBridgeCallback {
        fun onClientMessage(action: String)
        fun getNotificationListener(): NotificationListener
    }
}