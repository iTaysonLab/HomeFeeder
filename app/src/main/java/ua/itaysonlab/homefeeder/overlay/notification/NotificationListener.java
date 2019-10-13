package ua.itaysonlab.homefeeder.overlay.notification;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.ArrayList;
import java.util.List;

import ua.itaysonlab.homefeeder.preferences.HFPreferences;
import ua.itaysonlab.homefeeder.utils.Logger;

public class NotificationListener extends NotificationListenerService {
    private final IBinder binder = new LocalBinder();
    private final List<NLCallback> callbacks = new ArrayList<>();

    public void addCallback(NLCallback callback) {
        if (callbacks.contains(callback)) {
            throw new IllegalArgumentException("This NLCallback is already in the list");
        } else {
            callbacks.add(callback);
        }
    }

    public void removeCallback(NLCallback callback) {
        if (callbacks.contains(callback)) {
            callbacks.remove(callback);
        } else {
            throw new IllegalArgumentException("Trying to remove NLCallback which is not in the list");
        }
    }

    public class LocalBinder extends Binder {
        public NotificationListener getService() {
            return NotificationListener.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        String action = intent.getAction();
        if (SERVICE_INTERFACE.equals(action)) {
            return super.onBind(intent);
        } else {
            return binder;
        }
    }

    private NotificationWrapper wrapNotification(StatusBarNotification sbn) {
        return new NotificationWrapper(sbn.getNotification(), sbn.getPackageName(), sbn.getId(), sbn.getKey());
    }

    public void requestNotificationDismiss(String id) {
        cancelNotification(id);
    }

    public List<NotificationWrapper> getNotifications() {
        List<NotificationWrapper> list = new ArrayList<>();
        for (StatusBarNotification sbn : getActiveNotifications()) {
            if (sbn.isOngoing()) continue;
            if (HFPreferences.INSTANCE.getDebugging()) {
                Logger.INSTANCE.log(getClass().getSimpleName(), "===========");
                for (String s : sbn.getNotification().extras.keySet()) {
                    if (sbn.getNotification().extras.get(s) != null)
                        Logger.INSTANCE.log(getClass().getSimpleName(), "key: " + s + ", data: " + sbn.getNotification().extras.get(s).toString());
                }
            }
            list.add(wrapNotification(sbn));
        }
        return list;
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        for (NLCallback callback : callbacks) {
            callback.onNewNotification(wrapNotification(sbn));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        for (NLCallback callback: callbacks) {
            callback.onNotificationRemove(wrapNotification(sbn));
        }
    }

    public interface NLCallback {
        void onNewNotification(NotificationWrapper notification);
        void onNotificationRemove(NotificationWrapper notification);
    }
}