package ua.itaysonlab.homefeeder.utils

class UIBridge {
    lateinit var callback: UIBridgeCallback

    fun callServer(action: String) {
        if (isBridgeAlive()) callback.onClientMessage(action)
    }

    fun isBridgeAlive() = ::callback.isInitialized

    interface UIBridgeCallback {
        fun onClientMessage(action: String)
    }
}