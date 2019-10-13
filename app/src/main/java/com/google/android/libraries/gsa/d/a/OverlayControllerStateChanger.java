package com.google.android.libraries.gsa.d.a;

import android.os.Build;
import android.view.WindowManager.LayoutParams;

final class OverlayControllerStateChanger implements t {

    private final OverlayController overlayController;

    OverlayControllerStateChanger(OverlayController overlayControllerVar) {
        this.overlayController = overlayControllerVar;
    }

    public final void drag() {
        OverlayController overlayControllerVar = this.overlayController;
        PanelState panelStateVar = PanelState.DRAGGING;//Todo: PanelState.uof was default
        if (overlayControllerVar.panelState != panelStateVar) {
            overlayControllerVar.panelState = panelStateVar;
            overlayControllerVar.setState(overlayControllerVar.panelState);
        }
        overlayControllerVar = this.overlayController;
        LayoutParams attributes = overlayControllerVar.window.getAttributes();
        if (Build.VERSION.SDK_INT >= 26) {
            float f = attributes.alpha;
            attributes.alpha = 1.0f;
            if (f != attributes.alpha) {
                overlayControllerVar.window.setAttributes(attributes);
                return;
            }
            return;
        }
        attributes.x = 0;
        attributes.flags &= -513;
        overlayControllerVar.unZ = true;
        overlayControllerVar.window.setAttributes(attributes);
    }

    public final void cnF() {
        OverlayController overlayControllerVar = this.overlayController;
        PanelState panelStateVar = PanelState.DRAGGING;//Todo: PanelState.uof was default
        if (overlayControllerVar.panelState != panelStateVar) {
            overlayControllerVar.panelState = panelStateVar;
            overlayControllerVar.setState(overlayControllerVar.panelState);
        }
        this.overlayController.setVisible(true);
        overlayControllerVar = this.overlayController;
        LayoutParams attributes = overlayControllerVar.window.getAttributes();
        if (Build.VERSION.SDK_INT >= 26) {
            float f = attributes.alpha;
            attributes.alpha = 1.0f;
            if (f != attributes.alpha) {
                overlayControllerVar.window.setAttributes(attributes);
                return;
            }
            return;
        }
        attributes.x = 0;
        attributes.flags &= -513;
        overlayControllerVar.unZ = true;
        overlayControllerVar.window.setAttributes(attributes);
    }

    public final void oc(boolean z) {
        if (z) {
            this.overlayController.Hn();
        }
        OverlayController overlayControllerVar = this.overlayController;
        PanelState panelStateVar = PanelState.DRAGGING;//Todo: PanelState.uof was default
        if (overlayControllerVar.panelState != panelStateVar) {
            overlayControllerVar.panelState = panelStateVar;
            overlayControllerVar.setState(overlayControllerVar.panelState);
        }
        this.overlayController.setVisible(false);
    }

    public final void open() {
        OverlayController overlayControllerVar = this.overlayController;
        PanelState panelStateVar = PanelState.OPEN_AS_DRAWER;//Todo: PanelState.uog was default
        if (overlayControllerVar.panelState != panelStateVar) {
            overlayControllerVar.panelState = panelStateVar;
            overlayControllerVar.setState(overlayControllerVar.panelState);
        }
    }

    public final void D(float f) {
        if (this.overlayController.uoa != null && !Float.isNaN(f)) {
            try {
                this.overlayController.uoa.aK(f);
                this.overlayController.onScroll(f);
            } catch (Throwable ignored) {

            }
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
        overlayControllerVar = this.overlayController;
        PanelState panelStateVar = PanelState.CLOSED;//Todo: PanelState.uoe was default
        if (overlayControllerVar.panelState != panelStateVar) {
            overlayControllerVar.panelState = panelStateVar;
            overlayControllerVar.setState(overlayControllerVar.panelState);
        }
    }

    public final boolean cnI() {
        return this.overlayController.Ho();
    }
}
