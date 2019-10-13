package ua.itaysonlab.homefeeder.overlay

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.SparseIntArray
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.gsa.d.a.OverlayController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.overlay_layout.view.*
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.overlay.notification.NotificationAdapter
import ua.itaysonlab.homefeeder.overlay.notification.NotificationListener
import ua.itaysonlab.homefeeder.overlay.notification.NotificationWrapper
import ua.itaysonlab.homefeeder.overlay.notification.SwipeToDeleteCallback
import ua.itaysonlab.homefeeder.preferences.HFPreferences
import ua.itaysonlab.homefeeder.utils.Logger
import ua.itaysonlab.homefeeder.utils.SNUtils
import ua.itaysonlab.homefeeder.utils.ThemeUtils
import ua.itaysonlab.homefeeder.utils.UIBridge

class OverlayKt(val context: Context): OverlayController(context, R.style.AppTheme, R.style.WindowTheme), NotificationListener.NLCallback, UIBridge.UIBridgeCallback {
    override fun getNotificationListener(): NotificationListener {
        return mService
    }

    private lateinit var rootView: View
    private lateinit var adapter: NotificationAdapter
    private lateinit var mService: NotificationListener
    private var mBound = false

    private var isLauncherDarkTheme = false
    private var useBackgroundColorHint = false
    private var currentTransparencyValue = HFPreferences.overlayTransparency
    private var shouldUseSN = false
    private var isSNApplied = false
    private var currentHFTheme = ThemeUtils.defaultDarkThemeColors

    @ColorInt private var backgroundColorHint = Color.BLACK
    @ColorInt private var backgroundColorHintSecondary = Color.BLACK

    private val permissionGranted: Boolean get() {
        val enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as NotificationListener.LocalBinder
            mService = binder.service
            mBound = true
            mService.addCallback(this@OverlayKt)
            refreshNotifications()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private fun getHFTheme(force: String?): SparseIntArray {
        val currentTheme = force ?: HFPreferences.overlayTheme
        val theme = when (currentTheme) {
            "auto_launcher" -> {
                if (isLauncherDarkTheme) {
                    ThemeUtils.defaultDarkThemeColors
                } else {
                    ThemeUtils.defaultLightThemeColors
                }
            }
            "auto_system" -> ThemeUtils.getThemeBySystem(context)
            "dark" -> ThemeUtils.defaultDarkThemeColors
            else -> ThemeUtils.defaultLightThemeColors
        }

        useBackgroundColorHint = currentTheme == "auto_launcher";
        shouldUseSN = theme.get(ThemeUtils.Colors.IS_LIGHT.position) == 1;
        //theme.put(ThemeUtils.Colors.CARD_BG.getPosition(), backgroundColorHintSecondary);
        return theme
    }

    override fun onOptionsUpdated(bundle: Bundle) {
        super.onOptionsUpdated(bundle)
        if (HFPreferences.contentDebugging) {
            bundle.keySet().forEach {
                val item = bundle.get(it)
                item ?: return@forEach
                Logger.log(javaClass.simpleName, "key: $it, data: $item")
            }
        }
        if (bundle.containsKey("is_background_dark")) {
            isLauncherDarkTheme = bundle.getBoolean("is_background_dark")
        }
        if (bundle.containsKey("background_color_hint")) {
            backgroundColorHint = bundle.getInt("background_color_hint")
        }
        if (bundle.containsKey("background_secondary_color_hint")) {
            backgroundColorHintSecondary = bundle.getInt("background_secondary_color_hint")
        }
        updateTheme(null)
    }

    private fun updateTheme(force: String?) {
        currentHFTheme = getHFTheme(force)
        updateStubUi()
        adapter.setTheme(currentHFTheme)
    }

    private fun updateCompact(value: Boolean) {
        adapter = NotificationAdapter()
        adapter.setHasStableIds(true)
        adapter.setCompact(value)
        rootView.recycler.adapter = adapter
        refreshNotifications()
    }

    private fun updateStubUi() {
        if (!permissionGranted) {
            rootView.nas_title.setTextColor(currentHFTheme.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.position))
            rootView.nas_text.setTextColor(currentHFTheme.get(ThemeUtils.Colors.TEXT_COLOR_SECONDARY.position))
            rootView.nas_icon.imageTintList = ColorStateList.valueOf(currentHFTheme.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.position))
            rootView.nas_reload.setTextColor(currentHFTheme.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.position))
            rootView.nas_action.setBackgroundColor(currentHFTheme.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.position))
            rootView.nas_action.setTextColor(currentHFTheme.get(ThemeUtils.Colors.CARD_BG.position))
        }
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        rootView = View.inflate(ContextThemeWrapper(this, R.style.AppTheme), R.layout.overlay_layout, this.container)

        rootView.swipe_to_refresh.setOnRefreshListener {
            refreshNotifications()
        }

        adapter = NotificationAdapter()
        adapter.setHasStableIds(true)
        rootView.recycler.layoutManager = LinearLayoutManager(context)
        rootView.recycler.adapter = adapter

        val callback = object: SwipeToDeleteCallback(object: RecyclerItemTouchHelperListener {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
                val notifyID = adapter.removeItem(position)
                mService.requestNotificationDismiss(notifyID)
            }
        }) {}
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(rootView.recycler)

        if (!permissionGranted) {
            rootView.swipe_to_refresh.visibility = View.GONE
            rootView.no_access_stub.visibility = View.VISIBLE
            rootView.nas_action.setOnClickListener {
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            }
            rootView.nas_reload.setOnClickListener {
                if (permissionGranted) {
                    bindService()
                    rootView.swipe_to_refresh.visibility = View.VISIBLE
                    rootView.no_access_stub.visibility = View.GONE
                } else {
                    Snackbar.make(rootView.user_root, R.string.overlay_no_permission_snackbar, Snackbar.LENGTH_LONG).show()
                }
            }
        } else {
            bindService()
        }

        HFApplication.bridge.setCallback(this)
    }

    private fun refreshNotifications() {
        rootView.swipe_to_refresh.isRefreshing = true
        if (mBound) {
            val list = mService.notifications
            if (HFPreferences.contentDebugging) {
                list.forEach {
                    Logger.log(javaClass.simpleName, it.toString())
                }
            }
            adapter.update(list)
        }
        rootView.swipe_to_refresh.isRefreshing = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBound) {
            mService.removeCallback(this)
            unbindService(connection)
            mBound = false
        }
    }

    override fun onScroll(f: Float) {
        super.onScroll(f)
        val float = if (f > 1) 1f else f

        if (currentTransparencyValue == "transparent") return

        if (f > 0.7f) {
            if (shouldUseSN && !isSNApplied) {
                isSNApplied = true
                SNUtils.setLight(getWindow().decorView)
            }
        } else {
            if (shouldUseSN && isSNApplied) {
                isSNApplied = false
                SNUtils.removeLight(getWindow().decorView)
            }
        }

        val bgColor = if (useBackgroundColorHint) {
            backgroundColorHint
        } else {
            currentHFTheme.get(ThemeUtils.Colors.OVERLAY_BG.position)
        }

        var computeAlphaScroll = float
        if (currentTransparencyValue == "half" && float > 0.5f) {
            computeAlphaScroll = 0.5f
        }

        val color = (computeAlphaScroll * 255.0f).toInt() shl 24 or (bgColor and 0x00ffffff)
        getWindow().setBackgroundDrawable(ColorDrawable(color))
    }

    private fun bindService() {
        bindService(Intent(this, NotificationListener::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onNewNotification(notification: NotificationWrapper) {
        if (HFPreferences.debugging) {
            Logger.log(javaClass.simpleName, "Notification has been posted by: ${notification.applicationName}")
        }
        refreshNotifications()
    }

    override fun onNotificationRemove(notification: NotificationWrapper) {
        if (HFPreferences.debugging) {
            Logger.log(javaClass.simpleName, "Notification has been removed by: ${notification.applicationName}")
        }
        refreshNotifications()
    }

    override fun onClientMessage(action: String) {
        if (HFPreferences.debugging) {
            Logger.log(javaClass.simpleName, "New message by UIBridget: $action")
        }
        if (action.startsWith("reloadTheme")) {
            updateTheme(action.split(":")[1])
        }
        if (action.startsWith("reloadTransparent")) {
            currentTransparencyValue = action.split(":")[1]
        }
        if (action.startsWith("reloadCompact")) {
            updateCompact(action.split(":")[1].toBoolean())
        }
    }
}