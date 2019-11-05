package ua.itaysonlab.overlaykit

import android.os.IInterface
import android.os.Parcel
import android.os.Parcelable

object ParcelMagic {
    fun a(parcel: Parcel): Boolean = parcel.readInt() == 1

    fun writeBoolean(parcel: Parcel, z: Boolean) {
        parcel.writeInt(if (z) 1 else 0)
    }

    fun createParcelable(parcel: Parcel, creator: Parcelable.Creator<*>): Parcelable? {
        if (parcel.readInt() == 0) return null
        return creator.createFromParcel(parcel) as Parcelable
    }

    fun writeToParcelWithParcelable(parcel: Parcel, parcelable: Parcelable?) {
        if (parcelable == null) {
            parcel.writeInt(0)
            return
        }
        parcel.writeInt(1)
        parcelable.writeToParcel(parcel, 0)
    }

    fun writeStrongBinder(parcel: Parcel, iInterface: IInterface?) {
        if (iInterface == null) {
            parcel.writeStrongBinder(null)
        } else {
            parcel.writeStrongBinder(iInterface.asBinder())
        }
    }
}