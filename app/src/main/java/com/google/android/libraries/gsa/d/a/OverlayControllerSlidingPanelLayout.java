package com.google.android.libraries.gsa.d.a;

import android.graphics.Rect;
import android.view.MotionEvent;

public final class OverlayControllerSlidingPanelLayout extends SlidingPanelLayout {

    private final OverlayController overlayController;

    public OverlayControllerSlidingPanelLayout(OverlayController overlayControllerVar) {
        super(overlayControllerVar);
        this.overlayController = overlayControllerVar;
    }

    protected final void determineScrollingStart(MotionEvent motionEvent, float f) {
        Object obj = 1;
        if (motionEvent.findPointerIndex(this.mActivePointerId) != -1) {
            float x = motionEvent.getX() - this.mDownX;
            float abs = Math.abs(x);
            float abs2 = Math.abs(motionEvent.getY() - this.mDownY);
            if (Float.compare(abs, 0.0f) != 0) {
                abs = (float) Math.atan((double) (abs2 / abs));
                Object obj2;
                if (this.mIsRtl) {
                    obj2 = x < 0.0f ? 1 : null;
                } else if (x > 0.0f) {
                    obj2 = 1;//TODO: different from source
                } else {
                    obj2 = null;
                }
                if (!this.mIsPanelOpen || this.mIsPageMoving) {
                    obj = null;
                }
                if (obj != null && obj2 != null) {//TODO: different from source
                    return;
                }
                if ((obj != null && this.uoH.cnI()) || abs > 1.0471976f) {
                    return;
                }
                if (abs > 0.5235988f) {
                    super.determineScrollingStart(motionEvent, (((float) Math.sqrt((double) ((abs - 0.5235988f) / 0.5235988f))) * 4.0f) + 1.0f);
                } else {
                    super.determineScrollingStart(motionEvent, f);
                }
            }
        }
    }

    protected final boolean fitSystemWindows(Rect rect) {
        return !this.overlayController.unZ || super.fitSystemWindows(rect);
    }
}
