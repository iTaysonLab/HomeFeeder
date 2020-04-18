package com.google.android.libraries.gsa.d.a;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.SparseArray;

import java.io.PrintWriter;
import java.util.Arrays;

public abstract class OverlaysController {

    private final Service service;
    private final SparseArray<OverlayControllerBinder> clients = new SparseArray<>();
    public final Handler handler = new Handler();

    protected OverlaysController(Service service) {
        this.service = service;
    }

    public abstract OverlayController createController(Configuration configuration, int i, int i2);

    public final synchronized IBinder onBind(Intent intent) {
        OverlayControllerBinder iBinder;
        int i = Integer.MAX_VALUE;
        synchronized (this) {
            Uri data = intent.getData();
            int port = data.getPort();
            if (port == -1) {
                iBinder = null;
            } else {
                int parseInt;
                try {
                    parseInt = Integer.parseInt(data.getQueryParameter("v"));
                } catch (Exception e) {
                    parseInt = i;
                }
                try {
                    i = Integer.parseInt(data.getQueryParameter("cv"));
                } catch (Exception ignored) {

                }
                String[] packagesForUid = this.service.getPackageManager().getPackagesForUid(port);
                String host = data.getHost();
                if (packagesForUid == null || !Arrays.asList(packagesForUid).contains(host)) {
                    iBinder = null;
                } else {
                    try {
                        int i2 = this.service.getPackageManager().getApplicationInfo(host, 0).flags;
                        /*if ((i2 & 1) == 0 && (i2 & 2) == 0) {
                            Log.e("OverlaySController", "Only system apps are allowed to connect");
                            iBinder = null;
                        } else {
                            iBinder = this.clients.get(port);
                            if (!(iBinder == null || iBinder.mServerVersion == parseInt)) {
                                iBinder.destroy();
                                iBinder = null;
                            }
                            if (iBinder == null) {
                                iBinder = new OverlayControllerBinder(this, port, host, parseInt, i);
                                this.clients.put(port, iBinder);
                            }
                        }*/
                        iBinder = this.clients.get(port);
                        if (!(iBinder == null || iBinder.mServerVersion == parseInt)) {
                            iBinder.destroy();
                            iBinder = null;
                        }
                        if (iBinder == null) {
                            iBinder = new OverlayControllerBinder(this, port, host, parseInt, i);
                            this.clients.put(port, iBinder);
                        }
                    } catch (NameNotFoundException e3) {
                        iBinder = null;
                    }
                }
            }
        }
        return iBinder;
    }

    public final synchronized void unUnbind(Intent intent) {
        int port = intent.getData().getPort();
        if (port != -1) {
            OverlayControllerBinder overlayControllerBinderVar = this.clients.get(port);
            if (overlayControllerBinderVar != null) {
                overlayControllerBinderVar.destroy();
            }
            this.clients.remove(port);
        }
    }

    /*public final synchronized void dump(PrintWriter printWriter) {
        printWriter.println("OverlayServiceController, num clients : " + this.clients.size());
        for (int size = this.clients.size() - 1; size >= 0; size--) {
            OverlayControllerBinder overlayControllerBinderVar = this.clients.valueAt(size);
            if (overlayControllerBinderVar != null) {
                printWriter.println("  dump of client " + size);
                String str = "    ";
                printWriter.println(str + "mCallerUid: " + overlayControllerBinderVar.mCallerUid);
                printWriter.println(str + "mServerVersion: " + overlayControllerBinderVar.mServerVersion);
                printWriter.println(str + "mClientVersion: " + overlayControllerBinderVar.mClientVersion);
                String str2 = overlayControllerBinderVar.mPackageName;
                printWriter.println(str + "mPackageName: " + str2);
                printWriter.println(str + "mOptions: " + overlayControllerBinderVar.mOptions);
                printWriter.println(str + "mLastAttachWasLandscape: " + overlayControllerBinderVar.mLastAttachWasLandscape);
                BaseCallback baseCallbackVar = overlayControllerBinderVar.baseCallback;
                if (baseCallbackVar != null) {
                    baseCallbackVar.dump(printWriter, str);
                }
            } else {
                printWriter.println("  null client: " + size);
            }
        }
    }*/

    public final synchronized void onDestroy() {
        for (int size = this.clients.size() - 1; size >= 0; size--) {
            OverlayControllerBinder overlayControllerBinderVar = this.clients.valueAt(size);
            if (overlayControllerBinderVar != null) {
                overlayControllerBinderVar.destroy();
            }
        }
        this.clients.clear();
    }

    public v HA() {
        return new v();
    }

    //Todo: maybe remove
    public int Hx() {
        return 24;
    }
}
