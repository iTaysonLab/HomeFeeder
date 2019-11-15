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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.gsa.d.a.OverlayController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.overlay_header.view.*
import kotlinx.android.synthetic.main.overlay_layout.view.*
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.activites.MainActivity
import ua.itaysonlab.homefeeder.isDark
import ua.itaysonlab.homefeeder.isLight
import ua.itaysonlab.homefeeder.overlay.notification.NotificationAdapter
import ua.itaysonlab.homefeeder.overlay.notification.NotificationListener
import ua.itaysonlab.homefeeder.overlay.notification.NotificationWrapper
import ua.itaysonlab.homefeeder.overlay.rvutils.SwipeToDeleteCallback
import ua.itaysonlab.homefeeder.preferences.HFPreferences
import ua.itaysonlab.homefeeder.utils.Logger
import ua.itaysonlab.homefeeder.utils.StatusbarHelper
import ua.itaysonlab.homefeeder.theming.Theming
import ua.itaysonlab.homefeeder.utils.OverlayBridge

class OverlayKt(val context: Context): OverlayController(context, R.style.AppTheme, R.style.WindowTheme), NotificationListener.NLCallback, OverlayBridge.OverlayBridgeCallback {
    override fun getNotificationListener(): NotificationListener {
        return mService
    }

    private lateinit var rootView: View
    private lateinit var adapter: NotificationAdapter
    private lateinit var mService: NotificationListener
    private var mBound = false

    private var isLauncherDarkTheme = false
    private var currentTransparencyValue = HFPreferences.overlayTransparency
    private var shouldUseSN = false
    private var isSNApplied = false
    private var currentHFTheme = Theming.defaultDarkThemeColors

    @ColorInt private var backgroundColorHint = Color.BLACK
    @ColorInt private var backgroundColorHintSecondary = Color.BLACK
    @ColorInt private var backgroundColorHintTertiary = Color.BLACK

    private var cardBgColorChoice = HFPreferences.cardBackground
    private var overlayBgColorChoice = HFPreferences.overlayBackground

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

    private fun injectTheme(theme: SparseIntArray) {
        if (overlayBgColorChoice != "theme") {
            val choice = when (overlayBgColorChoice) {
                "white" -> Color.WHITE
                "dark" -> ContextCompat.getColor(context, R.color.bg_dark)
                "amoled" -> Color.BLACK
                "launcher_primary" -> backgroundColorHint
                "launcher_secondary" -> backgroundColorHintSecondary
                "launcher_tertiary" -> backgroundColorHintTertiary
                else -> Color.BLACK
            }

            theme.put(
                Theming.Colors.OVERLAY_BG.position, choice
            )

            shouldUseSN = choice.isLight()
        } else {
            shouldUseSN = theme.get(Theming.Colors.IS_LIGHT.position) == 1
        }

        if (!shouldUseSN && isSNApplied) {
            isSNApplied = false
            StatusbarHelper.removeLight(getWindow().decorView)
        }

        if (cardBgColorChoice != "theme") {
            theme.put(
                Theming.Colors.CARD_BG.position, when (cardBgColorChoice) {
                    "white" -> ContextCompat.getColor(context, R.color.card_bg)
                    "dark" -> ContextCompat.getColor(context, R.color.card_bg_dark)
                    "amoled" -> Color.BLACK
                    "launcher_primary" -> backgroundColorHint
                    "launcher_secondary" -> backgroundColorHintSecondary
                    "launcher_tertiary" -> backgroundColorHintTertiary
                    else -> Color.BLACK
                }
            )
        }
    }

    private fun getHFTheme(force: String?): SparseIntArray {
        val theme = when (force ?: HFPreferences.overlayTheme) {
            "auto_launcher" -> {
                if (isLauncherDarkTheme) {
                    Theming.defaultDarkThemeColors
                } else {
                    Theming.defaultLightThemeColors
                }
            }
            "auto_system" -> Theming.getThemeBySystem(context)
            "dark" -> Theming.defaultDarkThemeColors
            else -> Theming.defaultLightThemeColors
        }

        injectTheme(theme)

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

        isLauncherDarkTheme = bundle.getBoolean("is_background_dark", true)
        backgroundColorHint = bundle.getInt("background_color_hint", Color.BLACK)
        backgroundColorHintSecondary = bundle.getInt("background_secondary_color_hint", Color.BLACK)
        backgroundColorHintTertiary = bundle.getInt("background_tertiary_color_hint", Color.BLACK)

        updateTheme()
    }

    private fun updateTheme(force: String? = null) {
        currentHFTheme = getHFTheme(force)
        updateStubUi()
        adapter.setTheme(currentHFTheme)
    }

    private fun updateStubUi() {
        if (!permissionGranted) {
            rootView.nas_title.setTextColor(currentHFTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
            rootView.nas_text.setTextColor(currentHFTheme.get(Theming.Colors.TEXT_COLOR_SECONDARY.position))
            rootView.nas_icon.imageTintList = ColorStateList.valueOf(currentHFTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
            rootView.nas_reload.setTextColor(currentHFTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
            rootView.nas_action.setBackgroundColor(currentHFTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
            rootView.nas_action.setTextColor(currentHFTheme.get(Theming.Colors.CARD_BG.position))
        }

        val theme = if (currentHFTheme.get(Theming.Colors.OVERLAY_BG.position).isDark()) Theming.defaultDarkThemeColors else Theming.defaultLightThemeColors
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
            HFApplication.instance.startActivity(Intent(HFApplication.instance, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun initPermissionStub() {
        rootView.overlay_root.visibility = View.GONE
        rootView.no_access_stub.visibility = View.VISIBLE
        rootView.nas_action.setOnClickListener {
            startActivity(Intent(HFApplication.ACTION_MANAGE_LISTENERS))
        }
        rootView.nas_reload.setOnClickListener {
            if (permissionGranted) {
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

        initRecyclerView()
        initHeader()

        if (!permissionGranted) {
            initPermissionStub()
        } else {
            bindService()
        }

        HFApplication.bridge.setCallback(this)
    }

    private fun refreshNotifications() {
        //rootView.swipe_to_refresh.isRefreshing = true
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
        HFApplication.bridge.setCallback(null)
    }

    override fun onScroll(f: Float) {
        super.onScroll(f)
        val float = if (f > 1) 1f else f

        if (currentTransparencyValue == "transparent") return

        if (f > 0.7f) {
            if (shouldUseSN && !isSNApplied) {
                isSNApplied = true
                StatusbarHelper.setLight(getWindow().decorView)
            }
        } else {
            if (shouldUseSN && isSNApplied) {
                isSNApplied = false
                StatusbarHelper.removeLight(getWindow().decorView)
            }
        }

        val bgColor = currentHFTheme.get(Theming.Colors.OVERLAY_BG.position)

        var computeAlphaScroll = float
        if (currentTransparencyValue == "half" && float > 0.5f) {
            computeAlphaScroll = 0.5f
        } else if (currentTransparencyValue == "less_half" && float > 0.25f) {
            computeAlphaScroll = 0.25f
        } else if (currentTransparencyValue == "more_half" && float > 0.75f) {
            computeAlphaScroll = 0.75f
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
            Logger.log(javaClass.simpleName, "New message by OverlayBridge: $action")
        }
    }

    override fun applyNewTheme(value: String) {
        updateTheme(value)
    }

    override fun applyNewTransparency(value: String) {
        currentTransparencyValue = value
    }

    override fun applyNewCardBg(value: String) {
        cardBgColorChoice = value
        updateTheme()
    }

    override fun applyNewOverlayBg(value: String) {
        overlayBgColorChoice = value
        updateTheme()
    }

    override fun applyCompactCard(value: Boolean) {
        adapter = NotificationAdapter()
        adapter.setHasStableIds(true)
        adapter.setCompact(value)
        rootView.recycler.adapter = adapter
        refreshNotifications()
    }
}