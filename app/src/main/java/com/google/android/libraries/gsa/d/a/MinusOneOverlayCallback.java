package com.google.android.libraries.gsa.d.a;

import android.content.res.Configuration;
import android.os.Message;

import java.io.PrintWriter;

public final class MinusOneOverlayCallback extends OverlayControllerCallback {

    private final OverlaysController overlaysController;

    public MinusOneOverlayCallback(OverlaysController overlaysControllerVar, OverlayControllerBinder overlayControllerBinderVar) {
        super(overlayControllerBinderVar, 3);
        this.overlaysController = overlaysControllerVar;
    }

    final OverlayController createController(Configuration configuration) {
        return this.overlaysController.createController(configuration, this.overlayControllerBinder.mServerVersion, this.overlayControllerBinder.mClientVersion);
    }

    public final void dump(PrintWriter printWriter, String str) {
        printWriter.println(String.valueOf(str).concat("MinusOneOverlayCallback"));
        super.dump(printWriter, str);
    }

    public final boolean handleMessage(Message message) {
        if (super.handleMessage(message)) {
            return true;
        }
        OverlayController overlayControllerVar;
        long when;
        switch (message.what) {
            case 3:
                if (this.overlayController != null) {
                    overlayControllerVar = this.overlayController;
                    when = message.getWhen();
                    if (!overlayControllerVar.cnD()) {
                        SlidingPanelLayout slidingPanelLayoutVar = overlayControllerVar.slidingPanelLayout;
                        if (slidingPanelLayoutVar.uoC < slidingPanelLayoutVar.mTouchSlop) {
                            overlayControllerVar.slidingPanelLayout.BM(0);
                            overlayControllerVar.mAcceptExternalMove = true;
                            overlayControllerVar.unX = 0;
                            overlayControllerVar.slidingPanelLayout.mForceDrag = true;
                            overlayControllerVar.obZ = when - 30;
                            overlayControllerVar.b(0, overlayControllerVar.unX, overlayControllerVar.obZ);
                            overlayControllerVar.b(2, overlayControllerVar.unX, when);
                        }
                    }
                }
                return true;
            case 4:
                if (this.overlayController != null) {
                    overlayControllerVar = this.overlayController;
                    float floatValue = (float) message.obj;
                    when = message.getWhen();
                    if (overlayControllerVar.mAcceptExternalMove) {
                        overlayControllerVar.unX = (int) (floatValue * ((float) overlayControllerVar.slidingPanelLayout.getMeasuredWidth()));
                        overlayControllerVar.b(2, overlayControllerVar.unX, when);
                    }
                }
                return true;
            case 5:
                if (this.overlayController != null) {
                    overlayControllerVar = this.overlayController;
                    when = message.getWhen();
                    if (overlayControllerVar.mAcceptExternalMove) {
                        overlayControllerVar.b(1, overlayControllerVar.unX, when);
                    }
                    overlayControllerVar.mAcceptExternalMove = false;
                }
                return true;
            default:
                return false;
        }
    }
}
