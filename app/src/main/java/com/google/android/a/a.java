package com.google.android.a;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public class a implements IInterface {
    private final String bHh;
    private final IBinder binder;

    protected a(IBinder iBinder, String str) {
        this.binder = iBinder;
        this.bHh = str;
    }

    public IBinder asBinder() {
        return this.binder;
    }

    protected final Parcel pg() {
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken(this.bHh);
        return obtain;
    }

    //Todo: different from source
    public final Parcel a(int i, Parcel parcel) {
        IBinder iBinder;
        Parcel obtain = Parcel.obtain();
        try {
            iBinder = this.binder;
            iBinder.transact(i, parcel, obtain, 0);
            obtain.readException();
        } catch (RuntimeException | RemoteException e) {
            parcel = null;
            //throw e;
        } finally {
            obtain.recycle();
        }
        return parcel;
    }

    //Todo: different from source
    protected final void c(int i, Parcel parcel) {
        try {
            this.binder.transact(i, parcel, null, 1);
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            parcel.recycle();
        }
    }
}
