package com.google.android.libraries.gsa.d.a;

import android.os.Build;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.WindowManager.LayoutParams;

final class OverlayControllerLayoutChangeListener implements OnLayoutChangeListener {

    private final OverlayController overlayController;

    OverlayControllerLayoutChangeListener(OverlayController overlayControllerVar) {
        this.overlayController = overlayControllerVar;
    }

    public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.overlayController.window.getDecorView().removeOnLayoutChangeListener(this);
        if (this.overlayController.panelState == PanelState.CLOSED) {//Todo: PanelState.uoe was default
            OverlayController overlayControllerVar = this.overlayController;
            LayoutParams attributes = overlayControllerVar.window.getAttributes();
            if (Build.VERSION.SDK_INT >= 26) {
                float f = attributes.alpha;
                attributes.alpha = 0.0f;
                if (f != attributes.alpha) {
                    overlayControllerVar.window.setAttributes(attributes);
                    return;
                }
                return;
            }
            attributes.x = overlayControllerVar.mWindowShift;
            attributes.flags |= 512;
            overlayControllerVar.unZ = false;
            overlayControllerVar.window.setAttributes(attributes);
        }
    }
}
