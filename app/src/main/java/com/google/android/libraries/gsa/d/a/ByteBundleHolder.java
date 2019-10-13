package com.google.android.libraries.gsa.d.a;

import android.os.Bundle;

final class ByteBundleHolder {

    private final Bundle extras;
    private final byte[] bytes;

    public ByteBundleHolder(byte[] bArr, Bundle bundle) {
        this.bytes = bArr;
        this.extras = bundle;
    }
}
