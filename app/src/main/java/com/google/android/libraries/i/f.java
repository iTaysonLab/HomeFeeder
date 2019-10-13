package com.google.android.libraries.i;

import android.os.IBinder;
import android.os.Parcel;
import com.google.android.a.a;

public final class f extends a implements d {

    f(IBinder iBinder) {
        super(iBinder, "com.google.android.libraries.launcherclient.ILauncherOverlayCallback");
    }

    public final void aK(float f) {
        Parcel pg = pg();
        pg.writeFloat(f);
        c(1, pg);
    }

    public final void BI(int i) {
        Parcel pg = pg();
        pg.writeInt(i);
        c(2, pg);
    }
}
