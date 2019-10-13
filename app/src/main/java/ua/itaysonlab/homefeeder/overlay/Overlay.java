package ua.itaysonlab.homefeeder.overlay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.libraries.gsa.d.a.OverlayController;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ua.itaysonlab.homefeeder.HFApplication;
import ua.itaysonlab.homefeeder.R;
import ua.itaysonlab.homefeeder.overlay.notification.NotificationAdapter;
import ua.itaysonlab.homefeeder.overlay.notification.NotificationListener;
import ua.itaysonlab.homefeeder.overlay.notification.NotificationWrapper;
import ua.itaysonlab.homefeeder.overlay.notification.SwipeToDeleteCallback;
import ua.itaysonlab.homefeeder.preferences.HFPreferences;
import ua.itaysonlab.homefeeder.utils.Logger;
import ua.itaysonlab.homefeeder.utils.SNUtils;
import ua.itaysonlab.homefeeder.utils.ThemeUtils;
import ua.itaysonlab.homefeeder.utils.UIBridge;

public class Overlay extends OverlayController implements NotificationListener.NLCallback, UIBridge.UIBridgeCallback {

    private final Context context;
    private NotificationListener mService;
    private boolean mBound = false;
    private List<NotificationWrapper> list = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private TextView nasTitle, nasText;
    private ImageView nasIcon;
    private MaterialButton nasConfig, nasReload;
    private View nasRoot, userRoot;

    private boolean isSNApplied = false;
    private boolean useBackgroundColorHint = false;

    private boolean isLauncherDarkTheme = true;
    private int backgroundColorHint = Color.BLACK;
    private int backgroundColorHintSecondary = Color.WHITE;
    private String currentTransparencyValue = HFPreferences.INSTANCE.getOverlayTransparency();
    private SparseIntArray currentHFTheme;

    private SparseIntArray getHFTheme(String force) {
        SparseIntArray theme;
        String currentTheme;

        if (force != null) {
            currentTheme = force;
        } else {
            currentTheme = HFPreferences.INSTANCE.getOverlayTheme();
        }

        switch (currentTheme) {
            case "auto_launcher":
                if (isLauncherDarkTheme) {
                    theme = ThemeUtils.INSTANCE.getDefaultDarkThemeColors();
                } else {
                    theme = ThemeUtils.INSTANCE.getDefaultLightThemeColors();
                }
                break;
            case "auto_system":
                theme = ThemeUtils.INSTANCE.getThemeBySystem(context);
                break;
            case "dark":
                theme = ThemeUtils.INSTANCE.getDefaultDarkThemeColors();
                break;
            default:
                theme = ThemeUtils.INSTANCE.getDefaultLightThemeColors();
                break;
        }

        useBackgroundColorHint = currentTheme.equals("auto_launcher");
        //theme.put(ThemeUtils.Colors.CARD_BG.getPosition(), backgroundColorHintSecondary);
        return theme;
    }

    @Override
    public void onOptionsUpdated(Bundle bundle) {
        super.onOptionsUpdated(bundle);
        if (HFPreferences.INSTANCE.getDebugging()) {
            for (String s : bundle.keySet()) {
                if (bundle.get(s) != null) Logger.INSTANCE.log(getClass().getSimpleName(), "key: " + s + ", data: " + bundle.get(s).toString());
            }
        }
        if (bundle.containsKey("is_background_dark")) {
            isLauncherDarkTheme = bundle.getBoolean("is_background_dark");
        }
        if (bundle.containsKey("background_color_hint")) {
            backgroundColorHint = bundle.getInt("background_color_hint");
        }
        if (bundle.containsKey("background_secondary_color_hint")) {
            backgroundColorHintSecondary = bundle.getInt("background_secondary_color_hint");
        }
        updateTheme();
    }

    public void updateTheme() {
        currentHFTheme = getHFTheme(null);
        updateStubUi();
        adapter.setTheme(currentHFTheme);
    }

    public void updateTheme(String force) {
        currentHFTheme = getHFTheme(force);
        updateStubUi();
        adapter.setTheme(currentHFTheme);
    }

    private void updateStubUi() {
        // Check to prevent NullPointerException
        if (!isPermissionGranted()) {
            nasTitle.setTextColor(currentHFTheme.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.getPosition()));
            nasText.setTextColor(currentHFTheme.get(ThemeUtils.Colors.TEXT_COLOR_SECONDARY.getPosition()));
            nasIcon.setImageTintList(ColorStateList.valueOf(currentHFTheme.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.getPosition())));
            nasReload.setTextColor(currentHFTheme.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.getPosition()));
        }
    }

    public Overlay(Context context, int i, int i2) {
        super(context, R.style.AppTheme, R.style.WindowTheme);
        this.context = context;
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        View rowView = View.inflate(new ContextThemeWrapper(this, R.style.AppTheme), R.layout.overlay_layout, this.container);
        swipeRefreshLayout = rowView.findViewById(R.id.swipe_to_refresh);
        recyclerView = rowView.findViewById(R.id.recycler);
        userRoot = rowView.findViewById(R.id.user_root);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tryToGetNotifications();
            }
        });

        adapter = new NotificationAdapter();
        adapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        SwipeToDeleteCallback callback = new SwipeToDeleteCallback(new SwipeToDeleteCallback.RecyclerItemTouchHelperListener() {
            @Override
            public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction, int position) {
                String notifyID = adapter.removeItem(position);
                mService.requestNotificationDismiss(notifyID);
            }
        }) {};
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

        // Save resources by findViewById'ing only if permission is not granted
        nasRoot = rowView.findViewById(R.id.no_access_stub);
        if (!isPermissionGranted()) {
            nasTitle = rowView.findViewById(R.id.nas_title);
            nasText = rowView.findViewById(R.id.nas_text);
            nasIcon = rowView.findViewById(R.id.nas_icon);
            nasReload = rowView.findViewById(R.id.nas_reload);
            nasConfig = rowView.findViewById(R.id.nas_action);
            swipeRefreshLayout.setVisibility(View.GONE);
            nasRoot.setVisibility(View.VISIBLE);

            nasReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPermissionGranted()) {
                        bindService();
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        nasRoot.setVisibility(View.GONE);
                    } else {
                        Snackbar.make(userRoot, R.string.overlay_no_permission_snackbar, Snackbar.LENGTH_LONG).show();
                    }
                }
            });

            nasConfig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").putExtra(":settings:fragment_args_key", new ComponentName(context, NotificationListener.class).flattenToString()));
                }
            });
        } else {
            bindService();
        }

        HFApplication.Companion.getBridge().setCallback(this);
    }

    private void bindService() {
        Intent intent = new Intent(this, NotificationListener.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private boolean isPermissionGranted() {
        String enabledNotificationListeners = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(getPackageName()));
    }

    @Override
    public void onScroll(float f) {
        if (f > 1) f = 1f;
        super.onScroll(f);

        if (currentTransparencyValue.equals("transparent")) return;

        boolean shouldUseSN = currentHFTheme.get(ThemeUtils.Colors.IS_LIGHT.getPosition()) == 1;
        if (f > 0.7f) {
            if (shouldUseSN && !isSNApplied) {
                isSNApplied = true;
                SNUtils.INSTANCE.setLight(getWindow().getDecorView());
            }
        } else {
            if (shouldUseSN && isSNApplied) {
                isSNApplied = false;
                SNUtils.INSTANCE.removeLight(getWindow().getDecorView());
            }
        }

        int bgColor;
        if (useBackgroundColorHint) {
            bgColor = backgroundColorHint;
        } else {
            bgColor = currentHFTheme.get(ThemeUtils.Colors.OVERLAY_BG.getPosition());
        }

        float computeAlphaScroll = f;
        if (currentTransparencyValue.equals("half") && f > 0.5f) {
            computeAlphaScroll = 0.5f;
        }

        int color = ((int) (computeAlphaScroll * 255.0f) << 24) | (bgColor & 0x00ffffff);
        getWindow().setBackgroundDrawable(new ColorDrawable(color));
    }

    private void tryToGetNotifications() {
        swipeRefreshLayout.setRefreshing(true);
        if (mBound) {
            list = mService.getNotifications();
            if (HFPreferences.INSTANCE.getDebugging()) {
                for (NotificationWrapper notification: list) {
                    Logger.INSTANCE.log(getClass().getSimpleName(), notification.toString());
                }
            }
            adapter.update(list);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            NotificationListener.LocalBinder binder = (NotificationListener.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.addCallback(Overlay.this);
            tryToGetNotifications();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound) {
            mService.removeCallback(this);
            unbindService(connection);
            mBound = false;
        }
    }

    @Override
    public void onNewNotification(NotificationWrapper notification) {
        if (HFPreferences.INSTANCE.getDebugging()) {
            Logger.INSTANCE.log(getClass().getSimpleName(), "Notification has been posted by: "+notification.getApplicationName());
        }
        tryToGetNotifications();
    }

    @Override
    public void onNotificationRemove(NotificationWrapper notification) {
        if (HFPreferences.INSTANCE.getDebugging()) {
            Logger.INSTANCE.log(getClass().getSimpleName(), "Notification has been removed by: " + notification.getApplicationName());
        }
        tryToGetNotifications();
    }

    @Override
    public void onClientMessage(@NotNull String action) {
        if (HFPreferences.INSTANCE.getDebugging()) {
            Logger.INSTANCE.log(getClass().getSimpleName(), "onClientMessage went in: "+action);
        }
        if (action.startsWith("reloadTheme")) {
            updateTheme(action.split(":")[1]);
        }
        if (action.startsWith("reloadTransparent")) {
            currentTransparencyValue = action.split(":")[1];
        }
    }
}
