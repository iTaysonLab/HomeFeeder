package com.google.android.a;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public class LauncherOverlayBinder extends Binder implements IInterface {
    public IBinder asBinder() {
        return this;
    }

    protected final boolean a(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {//Todo: throws is new
        if (i > 16777215) {
            return super.onTransact(i, parcel, parcel2, i2);
        }
        parcel.enforceInterface(getInterfaceDescriptor());
        return false;
    }
}
