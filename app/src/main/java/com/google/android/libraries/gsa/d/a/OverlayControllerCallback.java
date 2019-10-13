package com.google.android.libraries.gsa.d.a;

import android.content.ComponentName;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Message;
import android.util.Pair;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

import java.io.PrintWriter;

abstract class OverlayControllerCallback extends BaseCallback {
    final OverlayControllerBinder overlayControllerBinder;
    private final int uor;
    OverlayController overlayController;

    abstract OverlayController createController(Configuration configuration);

    OverlayControllerCallback(OverlayControllerBinder overlayControllerBinderVar, int i) {
        this.overlayControllerBinder = overlayControllerBinderVar;
        this.uor = i;
    }

    public boolean handleMessage(Message message) {
        boolean z = false;
        OverlayController overlayControllerVar;
        switch (message.what) {
            case 0:
                if (message.arg1 == 0) {
                    return true;
                }
                OverlayController overlayControllerVar2;
                Bundle bundle;
                if (this.overlayController != null) {
                    overlayControllerVar2 = this.overlayController;
                    Bundle bundle2 = new Bundle();
                    if (overlayControllerVar2.panelState == PanelState.OPEN_AS_DRAWER) {//Todo: PanelState.uog was default
                        bundle2.putBoolean("open", true);
                    }
                    bundle2.putParcelable("view_state", overlayControllerVar2.window.saveHierarchyState());
                    this.overlayController.cnC();
                    this.overlayController = null;
                    bundle = bundle2;
                } else {
                    bundle = null;
                }
                Pair pair = (Pair) message.obj;
                LayoutParams layoutParams = ((Bundle) pair.first).getParcelable("layout_params");
                this.overlayController = createController((Configuration) ((Bundle) pair.first).getParcelable("configuration"));
                try {
                    int i;
                    OverlayController overlayControllerVar3 = this.overlayController;
                    String str = this.overlayControllerBinder.mPackageName;
                    Bundle bundle3 = (Bundle) pair.first;
                    overlayControllerVar3.mIsRtl = SlidingPanelLayout.isRtl(overlayControllerVar3.getResources());
                    overlayControllerVar3.mPackageName = str;
                    overlayControllerVar3.window.setWindowManager(null, layoutParams.token, new ComponentName(overlayControllerVar3, overlayControllerVar3.getBaseContext().getClass()).flattenToShortString(), true);
                    overlayControllerVar3.windowManager = overlayControllerVar3.window.getWindowManager();
                    Point point = new Point();
                    overlayControllerVar3.windowManager.getDefaultDisplay().getRealSize(point);
                    overlayControllerVar3.mWindowShift = -Math.max(point.x, point.y);
                    overlayControllerVar3.slidingPanelLayout = new OverlayControllerSlidingPanelLayout(overlayControllerVar3);
                    overlayControllerVar3.container = new FrameLayout(overlayControllerVar3);
                    overlayControllerVar3.slidingPanelLayout.el(overlayControllerVar3.container);
                    overlayControllerVar3.slidingPanelLayout.uoH = overlayControllerVar3.overlayControllerStateChanger;
                    layoutParams.width = -1;
                    layoutParams.height = -1;
                    layoutParams.flags |= 8650752;
                    layoutParams.dimAmount = 0.0f;
                    layoutParams.gravity = 3;
                    if (VERSION.SDK_INT >= 25) {
                        i = 4;
                    } else {
                        i = 2;
                    }
                    layoutParams.type = i;
                    layoutParams.softInputMode = 16;
                    overlayControllerVar3.window.setAttributes(layoutParams);
                    overlayControllerVar3.window.clearFlags(1048576);
                    overlayControllerVar3.onCreate(bundle3);
                    overlayControllerVar3.window.setContentView(overlayControllerVar3.slidingPanelLayout);
                    overlayControllerVar3.windowView = overlayControllerVar3.window.getDecorView();
                    overlayControllerVar3.windowManager.addView(overlayControllerVar3.windowView, overlayControllerVar3.window.getAttributes());
                    overlayControllerVar3.slidingPanelLayout.setSystemUiVisibility(1792);
                    overlayControllerVar3.setVisible(false);
                    overlayControllerVar3.window.getDecorView().addOnLayoutChangeListener(new OverlayControllerLayoutChangeListener(overlayControllerVar3));
                    if (bundle != null) {
                        overlayControllerVar = this.overlayController;
                        overlayControllerVar.window.restoreHierarchyState(bundle.getBundle("view_state"));
                        if (bundle.getBoolean("open")) {
                            SlidingPanelLayout slidingPanelLayoutVar = overlayControllerVar.slidingPanelLayout;
                            slidingPanelLayoutVar.mPanelPositionRatio = 1.0f;
                            slidingPanelLayoutVar.uoC = slidingPanelLayoutVar.getMeasuredWidth();
                            slidingPanelLayoutVar.uoA.setTranslationX(slidingPanelLayoutVar.mIsRtl ? (float) (-slidingPanelLayoutVar.uoC) : (float) slidingPanelLayoutVar.uoC);
                            slidingPanelLayoutVar.cnF();
                            slidingPanelLayoutVar.cnG();
                        }
                    }
                    overlayControllerVar2 = this.overlayController;
                    overlayControllerVar2.uoa = (com.google.android.libraries.i.d) pair.second;
                    overlayControllerVar2.bP(true);
                    this.overlayControllerBinder.a((com.google.android.libraries.i.d) pair.second, this.uor);
                    this.overlayController.onOptionsUpdated(bundle3);
                    return true;
                } catch (Throwable e) {
                    Message obtain = Message.obtain();
                    obtain.what = 2;
                    handleMessage(obtain);
                    obtain.recycle();
                    return true;
                }
            case 1:
                if (this.overlayController == null) {
                    return true;
                }
                this.overlayController.BJ((Integer) message.obj);
                return true;
            case 2:
                if (this.overlayController == null) {
                    return true;
                }
                com.google.android.libraries.i.d cnC = this.overlayController.cnC();
                this.overlayController = null;
                if (message.arg1 != 0) {
                    return true;
                }
                this.overlayControllerBinder.a(cnC, 0);
                return true;
            case 6:
                if (this.overlayController == null) {
                    return true;
                }
                int i2 = message.arg2 & 1;
                if (message.arg1 == 1) {
                    this.overlayController.BK(i2);
                    return true;
                }
                this.overlayController.fI(i2);
                return true;
            case 7:
                if (this.overlayController == null) {
                    return true;
                }
                overlayControllerVar = this.overlayController;
                if (message.arg1 == 1) {
                    z = true;
                }
                overlayControllerVar.bP(z);
                return true;
            case 8:
                if (this.overlayController == null) {
                    return true;
                }
                this.overlayController.a((ByteBundleHolder) message.obj);
                return true;
            default:
                return false;
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        OverlayController overlayControllerVar = this.overlayController;
        String valueOf = String.valueOf(overlayControllerVar);
        printWriter.println(str + " mView: " + valueOf);
        if (overlayControllerVar != null) {
            overlayControllerVar.dump(printWriter, String.valueOf(str).concat("  "));
        }
    }
}
