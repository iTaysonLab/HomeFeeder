package com.google.android.libraries.gsa.d.a;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.FrameLayout;
import java.io.PrintWriter;

public class OverlayController extends DialogOverlayController {

    public boolean mIsRtl;
    public long obZ = 0;
    public int mWindowShift;
    public String mPackageName;
    public SlidingPanelLayout slidingPanelLayout;
    public final t overlayControllerStateChanger = new OverlayControllerStateChanger(this);
    public FrameLayout container;
    public int unX = 0;
    public boolean mAcceptExternalMove = false;
    public boolean unZ = true;
    public com.google.android.libraries.i.d uoa;
    public PanelState panelState = PanelState.CLOSED;
    private int mActivityStateFlags = 0;

    protected OverlayController(Context context, int theme, int dialogTheme) {
        super(context, theme, dialogTheme);
    }

    final void b(int i, int i2, long j) {
        MotionEvent obtain = MotionEvent.obtain(this.obZ, j, i, this.mIsRtl ? (float) (-i2) : (float) i2, 0.0f, 0);
        obtain.setSource(4098);
        this.slidingPanelLayout.dispatchTouchEvent(obtain);
        obtain.recycle();
    }

    public void onOptionsUpdated(Bundle bundle) {

    }

    final com.google.android.libraries.i.d cnC() {
        BJ(0);
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Throwable ignored) {

        }
        this.windowView = null;
        cnB();
        onDestroy();
        return this.uoa;
    }

    final void BJ(int i) {
        int i2 = 1;
        int i3 = 0;
        if (this.mActivityStateFlags != i) {
            int i4;
            int i5;
            int i6;
            int i7 = (this.mActivityStateFlags & 1) != 0 ? 1 : 0;
            if ((this.mActivityStateFlags & 2) != 0) {
                i4 = 1;
            } else {
                i4 = 0;
            }
            if ((i & 1) != 0) {
                i5 = 1;
            } else {
                i5 = 0;
            }
            if ((i & 2) != 0) {
                i6 = 1;
            } else {
                i6 = 0;
            }
            if (i5 == 0 && i6 == 0) {
                i5 = 0;
            } else {
                i5 = 1;
            }
            if (i5 == 0) {
                i2 = 0;
            }
            if (i6 != 0) {
                i3 = 2;
            }
            this.mActivityStateFlags = i2 | i3;
            if (i7 == 0 && i5 != 0) {
                onStart();
            }
            if (i4 == 0 && i6 != 0) {
                onResume();
            }
            if (i4 != 0 && i6 == 0) {
                onPause();
            }
            if (i7 != 0 && i5 == 0) {
                onStop();
            }
        }
    }

    public void fI(int i) {
        int i2 = 1;
        int i3 = 0;
        if (cnD()) {
            int i4 = (i & 1) != 0 ? 1 : 0;
            if (this.panelState == PanelState.OPEN_AS_LAYER) {
                i2 = 0;
            }
            i4 &= i2;
            SlidingPanelLayout slidingPanelLayoutVar = this.slidingPanelLayout;
            if (i4 != 0) {
                i3 = 750;
            }
            slidingPanelLayoutVar.closePanel(i3);
            cnB();
        }
    }

    public final void BK(int i) {
        int i2 = 0;
        if (this.panelState == PanelState.CLOSED) {
            int i3 = (i & 1) != 0 ? 1 : 0;
            if ((i & 2) != 0) {
                this.slidingPanelLayout.uoH = new TransparentOverlayController(this);
                i3 = 0;
            }
            SlidingPanelLayout slidingPanelLayoutVar = this.slidingPanelLayout;
            if (i3 != 0) {
                i2 = 750;
            }
            slidingPanelLayoutVar.fv(i2);
        }
    }

    public void a(ByteBundleHolder byteBundleHolderVar) {
    }

    public void onBackPressed() {
        fI(1);
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println(str + "mWindowShift: " + this.mWindowShift);
        printWriter.println(str + "mAcceptExternalMove: " + this.mAcceptExternalMove);
        String valueOf = String.valueOf(this.panelState);
        printWriter.println(str + "mDrawerState: " + valueOf);
        printWriter.println(str + "mActivityStateFlags: " + this.mActivityStateFlags);
        valueOf = String.valueOf(this.slidingPanelLayout);
        printWriter.println(str + "mWrapperView: " + valueOf);
        SlidingPanelLayout slidingPanelLayoutVar = this.slidingPanelLayout;
        String concat = String.valueOf(str).concat("  ");
        printWriter.println(concat + "mPanelPositionRatio: " + slidingPanelLayoutVar.mPanelPositionRatio);
        printWriter.println(concat + "mDownX: " + slidingPanelLayoutVar.mDownX);
        printWriter.println(concat + "mDownY: " + slidingPanelLayoutVar.mDownY);
        printWriter.println(concat + "mActivePointerId: " + slidingPanelLayoutVar.mActivePointerId);
        printWriter.println(concat + "mTouchState: " + slidingPanelLayoutVar.mTouchState);
        printWriter.println(concat + "mIsPanelOpen: " + slidingPanelLayoutVar.mIsPanelOpen);
        printWriter.println(concat + "mIsPageMoving: " + slidingPanelLayoutVar.mIsPageMoving);
        printWriter.println(concat + "mSettling: " + slidingPanelLayoutVar.mSettling);
        printWriter.println(concat + "mForceDrag: " + slidingPanelLayoutVar.mForceDrag);
    }

    public void Hn() {
    }

    public void onCreate(Bundle bundle) {
    }

    private void onPause() {
    }

    private void onStop() {
    }

    private void onStart() {
    }

    private void onResume() {
    }

    protected Window getWindow() {
        return this.window;
    }

    public final void setTitle(CharSequence charSequence) {
        this.window.setTitle(charSequence);
    }

    public void onDestroy() {
    }

    public void bP(boolean z) {
    }

    public boolean Ho() {
        return false;
    }

    public void onScroll(float f) {
    }

    public Object getSystemService(String str) {
        if (!"window".equals(str) || this.windowManager == null) {
            return super.getSystemService(str);
        }
        return this.windowManager;
    }

    public final boolean cnD() {
        return this.panelState == PanelState.OPEN_AS_DRAWER || this.panelState == PanelState.OPEN_AS_LAYER;
    }

    public final void setVisible(boolean z) {
        if (z) {
            this.window.clearFlags(24);
        } else {
            this.window.addFlags(24);
        }
    }

    public void setState(PanelState panelStateVar) {
    }
}
