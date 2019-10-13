package com.google.android.libraries.gsa.d.a;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.Window.Callback;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;

import java.util.HashSet;

class DialogOverlayController extends ContextThemeWrapper implements Callback, DialogListeners {

    public WindowManager windowManager;
    public final Window window;
    private final HashSet<DialogInterface> dialogs = new HashSet<>();
    public View windowView;

    DialogOverlayController(Context context, int theme, int dialogTheme) {
        super(context, theme);
        Dialog dialog = new Dialog(context, dialogTheme);
        this.window = dialog.getWindow();
        this.window.setCallback(this);
        Window window = this.window;
        if (VERSION.SDK_INT >= 21) {
            //window.setStatusBarColor(0);
            //window.setNavigationBarColor(0);
            window.addFlags(Integer.MIN_VALUE);
        } else if (VERSION.SDK_INT >= 19) {
            window.addFlags(201326592);
        }
    }

    void onBackPressed() {
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() != 4 || keyEvent.getAction() != 1 || keyEvent.isCanceled()) {
            return this.window.superDispatchKeyEvent(keyEvent);
        }
        onBackPressed();
        return true;
    }

    public boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
        return this.window.superDispatchKeyShortcutEvent(keyEvent);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return this.window.superDispatchTouchEvent(motionEvent);
    }

    public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
        return this.window.superDispatchTrackballEvent(motionEvent);
    }

    public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
        return this.window.superDispatchGenericMotionEvent(motionEvent);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return false;
    }

    public View onCreatePanelView(int i) {
        return null;
    }

    public boolean onCreatePanelMenu(int i, Menu menu) {
        return false;
    }

    public boolean onPreparePanel(int i, View view, Menu menu) {
        return true;
    }

    public boolean onMenuOpened(int i, Menu menu) {
        return true;
    }

    public boolean onMenuItemSelected(int i, MenuItem menuItem) {
        return false;
    }

    public void onWindowAttributesChanged(LayoutParams layoutParams) {
        if (this.windowView != null) {
            this.windowManager.updateViewLayout(this.windowView, layoutParams);
        }
    }

    public void onContentChanged() {
    }

    public void onWindowFocusChanged(boolean z) {
    }

    public void onAttachedToWindow() {
    }

    public void onDetachedFromWindow() {
    }

    public void onPanelClosed(int i, Menu menu) {
    }

    public boolean onSearchRequested() {
        return false;
    }

    public boolean onSearchRequested(SearchEvent searchEvent) {
        return false;
    }

    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return null;
    }

    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int i) {
        return null;
    }

    public void onActionModeStarted(ActionMode actionMode) {
    }

    public void onActionModeFinished(ActionMode actionMode) {
    }

    public void startActivity(Intent intent) {
        super.startActivity(intent.addFlags(268435456));
    }

    public void startActivity(Intent intent, Bundle bundle) {
        super.startActivity(intent.addFlags(268435456), bundle);
    }

    public void onShow(DialogInterface dialogInterface) {
        this.dialogs.add(dialogInterface);
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.dialogs.remove(dialogInterface);
    }

    final void cnB() {
        if (!this.dialogs.isEmpty()) {
            Dialog[] dialogArr = this.dialogs.toArray(new Dialog[this.dialogs.size()]);
            this.dialogs.clear();
            for (Dialog dismiss : dialogArr) {
                dismiss.dismiss();
            }
        }
    }
}
