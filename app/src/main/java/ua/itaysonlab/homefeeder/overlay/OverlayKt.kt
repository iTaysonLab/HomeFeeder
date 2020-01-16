package ua.itaysonlab.homefeeder.overlay

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.gsa.d.a.OverlayController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.overlay_header.view.*
import kotlinx.android.synthetic.main.overlay_layout.view.*
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.activites.MainActivity
import ua.itaysonlab.homefeeder.kt.clearLightFlags
import ua.itaysonlab.homefeeder.kt.isDark
import ua.itaysonlab.homefeeder.kt.isNotificationServiceGranted
import ua.itaysonlab.homefeeder.kt.setLightFlags
import ua.itaysonlab.homefeeder.overlay.launcherapi.LauncherAPI
import ua.itaysonlab.homefeeder.overlay.launcherapi.OverlayThemeHolder
import ua.itaysonlab.homefeeder.overlay.notification.NotificationAdapter
import ua.itaysonlab.homefeeder.overlay.notification.NotificationListener
import ua.itaysonlab.homefeeder.overlay.notification.NotificationWrapper
import ua.itaysonlab.homefeeder.overlay.rvutils.SwipeToDeleteCallback
import ua.itaysonlab.homefeeder.preferences.HFPreferences
import ua.itaysonlab.homefeeder.theming.Theming
import ua.itaysonlab.homefeeder.utils.Logger
import ua.itaysonlab.homefeeder.utils.OverlayBridge
import ua.itaysonlab.replica.vkpopup.DialogActionsVcByPopup
import ua.itaysonlab.replica.vkpopup.PopupItem

class OverlayKt(val context: Context): OverlayController(context, R.style.AppTheme, R.style.WindowTheme), NotificationListener.NLCallback, OverlayBridge.OverlayBridgeCallback {
    override fun getNotificationListener(): NotificationListener {
        return mService
    }
    
    companion object {
        const val LOG_TAG = "OverlayKt"
    }

    var apiInstance = LauncherAPI()
    private lateinit var themeHolder: OverlayThemeHolder

    private lateinit var rootView: View
    private lateinit var adapter: NotificationAdapter
    private lateinit var mService: NotificationListener
    private var mBound = false

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

    private fun setTheme(force: String?) {
        themeHolder.setTheme(when (force ?: HFPreferences.overlayTheme) {
            "auto_launcher" -> {
                if (apiInstance.darkTheme) {
                    Theming.defaultDarkThemeColors
                } else {
                    Theming.defaultLightThemeColors
                }
            }
            "auto_system" -> Theming.getThemeBySystem(context)
            "dark" -> Theming.defaultDarkThemeColors
            else -> Theming.defaultLightThemeColors
        })
    }

    override fun onOptionsUpdated(bundle: Bundle) {
        super.onOptionsUpdated(bundle)
        apiInstance = LauncherAPI(bundle)
        updateTheme()
    }

    private fun updateTheme(force: String? = null) {
        setTheme(force)
        updateStubUi()
        adapter.setTheme(themeHolder.currentTheme)
    }

    private fun updateStubUi() {
        if (!this.isNotificationServiceGranted()) {
            rootView.nas_title.setTextColor(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
            rootView.nas_text.setTextColor(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_SECONDARY.position))
            rootView.nas_icon.imageTintList = ColorStateList.valueOf(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
            rootView.nas_reload.setTextColor(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
            rootView.nas_action.setBackgroundColor(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
            rootView.nas_action.setTextColor(themeHolder.currentTheme.get(Theming.Colors.CARD_BG.position))
        }

        val theme = if (themeHolder.currentTheme.get(Theming.Colors.OVERLAY_BG.position).isDark()) Theming.defaultDarkThemeColors else Theming.defaultLightThemeColors
        rootView.header_preferences.imageTintList = ColorStateList.valueOf(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
        rootView.header_title.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
    }

    private fun initRecyclerView() {
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
    }

    private fun initHeader() {
        rootView.header_preferences.setOnClickListener {
            callMenuPopup(it)
        }
    }

    private fun callMenuPopup(view: View) {
        val popup = DialogActionsVcByPopup(view)
        popup.a(createMenuList(), {
            it.first.backgroundTintList = ColorStateList.valueOf(themeHolder.currentTheme.get(Theming.Colors.OVERLAY_BG.position))
            it.second.apply {
                setActionLabelTextColor(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
                setDividerColor(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_SECONDARY.position))
                setActionIconTint(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
            }
        }) {
            popup.dismiss()
            when (it.id) {
                "config" -> {
                    HFApplication.instance.startActivity(Intent(HFApplication.instance, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
                "reload" -> {
                    refreshNotifications()
                }
            }
        }
    }

    private fun createMenuList(): List<PopupItem> {
        return listOf(
            PopupItem(R.drawable.ic_settings_24, R.string.title_settings, 0, "config"),
            PopupItem(R.drawable.ic_replay_24, R.string.action_reload, 0, "reload")
        )
    }

    private fun initPermissionStub() {
        rootView.overlay_root.visibility = View.GONE
        rootView.no_access_stub.visibility = View.VISIBLE
        rootView.nas_action.setOnClickListener {
            startActivity(Intent(HFApplication.ACTION_MANAGE_LISTENERS))
        }
        rootView.nas_reload.setOnClickListener {
            if (this.isNotificationServiceGranted()) {
                bindService()
                rootView.overlay_root.visibility = View.VISIBLE
                rootView.no_access_stub.visibility = View.GONE
            } else {
                Snackbar.make(rootView.user_root, R.string.overlay_no_permission_snackbar, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        rootView = View.inflate(ContextThemeWrapper(this, R.style.AppTheme), R.layout.overlay_layout, this.container)

        themeHolder = OverlayThemeHolder(context,this)

        initRecyclerView()
        initHeader()

        if (!this.isNotificationServiceGranted()) {
            initPermissionStub()
        } else {
            bindService()
        }

        HFApplication.bridge.setCallback(this)
    }

    private fun refreshNotifications() {
        if (mBound) {
            val list = mService.notifications
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
        HFApplication.bridge.setCallback(null)
    }

    override fun onScroll(f: Float) {
        super.onScroll(f)
        if (themeHolder.isTransparentBg) return

        val float = if (f > 1) 1f else f

        if (f > 0.7f) {
            if (themeHolder.shouldUseSN && !themeHolder.isSNApplied) {
                themeHolder.isSNApplied = true
                window.decorView.setLightFlags()
            }
        } else {
            if (themeHolder.shouldUseSN && themeHolder.isSNApplied) {
                themeHolder.isSNApplied = false
                window.decorView.clearLightFlags()
            }
        }

        val bgColor = themeHolder.currentTheme.get(Theming.Colors.OVERLAY_BG.position)
        val color = (themeHolder.getScrollAlpha(float) * 255.0f).toInt() shl 24 or (bgColor and 0x00ffffff)
        getWindow().setBackgroundDrawable(ColorDrawable(color))
    }

    private fun bindService() {
        bindService(Intent(this, NotificationListener::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onNewNotification(notification: NotificationWrapper) {
        if (HFPreferences.debugging) {
            Logger.log(LOG_TAG, "Notification has been posted by: ${notification.applicationName}")
        }
        refreshNotifications()
    }

    override fun onNotificationRemove(notification: NotificationWrapper) {
        if (HFPreferences.debugging) {
            Logger.log(LOG_TAG, "Notification has been removed by: ${notification.applicationName}")
        }
        refreshNotifications()
    }

    override fun onClientMessage(action: String) {
        if (HFPreferences.debugging) {
            Logger.log(LOG_TAG, "New message by OverlayBridge: $action")
        }
    }

    override fun applyNewTheme(value: String) {
        updateTheme(value)
    }

    override fun applyNewTransparency(value: String) {
        themeHolder.transparencyBgPref = value
    }

    override fun applyNewCardBg(value: String) {
        themeHolder.cardBgPref = value
        updateTheme()
    }

    override fun applyNewOverlayBg(value: String) {
        themeHolder.overlayBgPref = value
        updateTheme()
    }

    override fun applyCompactCard(value: Boolean) {
        adapter = NotificationAdapter()
        adapter.setHasStableIds(true)
        adapter.setCompact(value)
        adapter.setTheme(themeHolder.currentTheme)
        rootView.recycler.adapter = adapter
        refreshNotifications()
    }

    override fun applySysColors(value: Boolean) {
        themeHolder.systemColors = value
        updateTheme()
    }
}