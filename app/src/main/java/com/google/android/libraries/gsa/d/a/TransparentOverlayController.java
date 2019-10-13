package com.google.android.libraries.gsa.d.a;

import android.os.Build;
import android.view.WindowManager.LayoutParams;

final class TransparentOverlayController implements t {

    private final OverlayController overlayController;

    TransparentOverlayController(OverlayController overlayControllerVar) {
        this.overlayController = overlayControllerVar;
    }

    public final void drag() {

    }

    public final void cnF() {
    }

    public final void oc(boolean z) {
    }

    public final void open() {
        this.overlayController.setVisible(true);
        OverlayController overlayControllerVar = this.overlayController;
        LayoutParams attributes = overlayControllerVar.window.getAttributes();
        if (Build.VERSION.SDK_INT >= 26) {
            float f = attributes.alpha;
            attributes.alpha = 1.0f;
            if (f != attributes.alpha) {
                overlayControllerVar.window.setAttributes(attributes);
            }
        } else {
            attributes.x = 0;
            attributes.flags &= -513;
            overlayControllerVar.unZ = true;
            overlayControllerVar.window.setAttributes(attributes);
        }
        overlayControllerVar = this.overlayController;
        PanelState panelStateVar = PanelState.OPEN_AS_LAYER;//Todo: PanelState.uoh was default
        if (overlayControllerVar.panelState != panelStateVar) {
            overlayControllerVar.panelState = panelStateVar;
            overlayControllerVar.setState(overlayControllerVar.panelState);
        }
    }

    public final void close() {
        OverlayController overlayControllerVar = this.overlayController;
        LayoutParams attributes = overlayControllerVar.window.getAttributes();
        if (Build.VERSION.SDK_INT >= 26) {
            float f = attributes.alpha;
            attributes.alpha = 0.0f;
            if (f != attributes.alpha) {
                overlayControllerVar.window.setAttributes(attributes);
            }
        } else {
            attributes.x = overlayControllerVar.mWindowShift;
            attributes.flags |= 512;
            overlayControllerVar.unZ = false;
            overlayControllerVar.window.setAttributes(attributes);
        }
        this.overlayController.setVisible(false);
        overlayControllerVar = this.overlayController;
        PanelState panelStateVar = PanelState.CLOSED;//Todo: PanelState.uoe was default
        if (overlayControllerVar.panelState != panelStateVar) {
            overlayControllerVar.panelState = panelStateVar;
            overlayControllerVar.setState(overlayControllerVar.panelState);
        }
        this.overlayController.slidingPanelLayout.uoH = this.overlayController.overlayControllerStateChanger;
    }

    public final void D(float f) {
    }

    public final boolean cnI() {
        return true;
    }
}
