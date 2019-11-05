package ua.itaysonlab.overlaykit

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel

class LauncherOverlayBinder: Binder(), IInterface {
    override fun asBinder(): IBinder = this

    fun a(i: Int, parcel: Parcel, parcel2: Parcel, i2: Int): Boolean {
        if (i > 16777215) {
            return super.onTransact(i, parcel, parcel2, i2)
        }
        parcel.enforceInterface(interfaceDescriptor)
        return false
    }
}