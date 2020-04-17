package ua.itaysonlab.homefeeder.utils

class OverlayBridge {
    private var callback: OverlayBridgeCallback? = null

    fun callServer(action: String) {
        if (isBridgeAlive()) callback!!.onClientMessage(action)
    }

    fun getCallback(): OverlayBridgeCallback? {
        return callback
    }

    fun setCallback(callback: OverlayBridgeCallback?) {
        this.callback = callback
    }

    fun isBridgeAlive() = callback !== null

    interface OverlayBridgeCallback {
        fun applyNewTheme(value: String)
        fun applyNewCardBg(value: String)
        fun applyNewOverlayBg(value: String)
        fun applyCompactCard(value: Boolean)
        fun applySysColors(value: Boolean)
        fun applyNewTransparency(value: String)

        fun onClientMessage(action: String)
    }
}