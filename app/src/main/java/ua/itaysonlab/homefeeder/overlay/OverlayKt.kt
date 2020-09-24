package ua.itaysonlab.homefeeder.overlay

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.gsa.d.a.OverlayController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.overlay_header.view.*
import kotlinx.android.synthetic.main.overlay_layout.view.*
import ua.itaysonlab.hfsdk.FeedItem
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.activites.MainActivity
import ua.itaysonlab.homefeeder.kt.clearLightFlags
import ua.itaysonlab.homefeeder.kt.isDark
import ua.itaysonlab.homefeeder.kt.setLightFlags
import ua.itaysonlab.homefeeder.overlay.feed.FeedAdapter
import ua.itaysonlab.homefeeder.overlay.launcherapi.LauncherAPI
import ua.itaysonlab.homefeeder.overlay.launcherapi.OverlayThemeHolder
import ua.itaysonlab.homefeeder.pluginsystem.PluginConnector
import ua.itaysonlab.homefeeder.preferences.HFPreferences
import ua.itaysonlab.homefeeder.theming.Theming
import ua.itaysonlab.homefeeder.utils.Logger
import ua.itaysonlab.homefeeder.utils.OverlayBridge
import ua.itaysonlab.replica.vkpopup.DialogActionsVcByPopup
import ua.itaysonlab.replica.vkpopup.PopupItem

class OverlayKt(val context: Context): OverlayController(context, R.style.AppTheme, R.style.WindowTheme), OverlayBridge.OverlayBridgeCallback {
    companion object {
        const val LOG_TAG = "OverlayKt"
    }

    var apiInstance = LauncherAPI()
    private lateinit var themeHolder: OverlayThemeHolder

    private lateinit var rootView: View
    private lateinit var adapter: FeedAdapter

    private val list = mutableListOf<FeedItem>()

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
        // TODO: make stub for no datasources
        /*if (!this.isNotificationServiceGranted()) {
            rootView.nas_title.setTextColor(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
            rootView.nas_text.setTextColor(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_SECONDARY.ordinal))
            rootView.nas_icon.imageTintList = ColorStateList.valueOf(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
            rootView.nas_reload.setTextColor(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
            rootView.nas_action.setBackgroundColor(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
            rootView.nas_action.setTextColor(themeHolder.currentTheme.get(Theming.Colors.CARD_BG.ordinal))
        }*/

        val theme = if (themeHolder.currentTheme.get(Theming.Colors.OVERLAY_BG.ordinal).isDark()) Theming.defaultDarkThemeColors else Theming.defaultLightThemeColors
        rootView.header_preferences.imageTintList = ColorStateList.valueOf(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
        rootView.header_title.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
    }

    private fun initRecyclerView() {
        rootView.swipe_to_refresh.setOnRefreshListener {
            refreshNotifications()
        }

        adapter = FeedAdapter()
        rootView.recycler.layoutManager = LinearLayoutManager(context)
        rootView.recycler.adapter = adapter

        /*val callback = object: SwipeToDeleteCallback(object: RecyclerItemTouchHelperListener {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
                val notifyID = adapter.removeItem(position)
                mService.requestNotificationDismiss(notifyID)
            }
        }) {}
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(rootView.recycler)*/
    }

    private fun initHeader() {
        rootView.header_preferences.setOnClickListener {
            callMenuPopup(it)
        }
    }

    private fun callMenuPopup(view: View) {
        val popup = DialogActionsVcByPopup(view)
        popup.a(createMenuList(), {
            it.first.backgroundTintList = ColorStateList.valueOf(themeHolder.currentTheme.get(Theming.Colors.OVERLAY_BG.ordinal))
            it.second.apply {
                setActionLabelTextColor(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
                setDividerColor(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_SECONDARY.ordinal))
                setActionIconTint(themeHolder.currentTheme.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
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
            /*if (this.isNotificationServiceGranted()) {
                //bindService()
                rootView.overlay_root.visibility = View.VISIBLE
                rootView.no_access_stub.visibility = View.GONE
            } else {
                Snackbar.make(rootView.user_root, R.string.overlay_no_permission_snackbar, Snackbar.LENGTH_LONG).show()
            }*/
        }
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        rootView = View.inflate(ContextThemeWrapper(this, R.style.AppTheme), R.layout.overlay_layout, this.container)

        themeHolder = OverlayThemeHolder(context,this)

        initRecyclerView()
        initHeader()

        HFApplication.bridge.setCallback(this)
    }

    private fun refreshNotifications() {
        list.clear()

        PluginConnector.getFeedAsItLoads(0, { feed ->
            list.addAll(feed)
        }) {
            adapter.replace(list)
            rootView.swipe_to_refresh.isRefreshing = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
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

        val bgColor = themeHolder.currentTheme.get(Theming.Colors.OVERLAY_BG.ordinal)
        val color = (themeHolder.getScrollAlpha(float) * 255.0f).toInt() shl 24 or (bgColor and 0x00ffffff)
        getWindow().setBackgroundDrawable(ColorDrawable(color))
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
        adapter = FeedAdapter()
        //adapter.setCompact(value)
        adapter.setTheme(themeHolder.currentTheme)
        rootView.recycler.adapter = adapter
        refreshNotifications()
    }

    override fun applySysColors(value: Boolean) {
        themeHolder.systemColors = value
        updateTheme()
    }
}