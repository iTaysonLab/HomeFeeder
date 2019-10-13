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

/*
Method generation error in method: com.google.android.a.a.a(int, android.os.Parcel):android.os.Parcel
jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0017: INVOKE  (wrap: android.os.Parcel
  ?: MERGE  (r5_1 android.os.Parcel) = (r5_0 'parcel' android.os.Parcel), (r1_0 'obtain' android.os.Parcel)) android.os.Parcel.recycle():void type: VIRTUAL in method: com.google.android.a.a.a(int, android.os.Parcel):android.os.Parcel
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:203)
	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:100)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:50)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:297)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:328)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:265)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:228)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:118)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:83)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:19)
	at jadx.core.ProcessClass.process(ProcessClass.java:43)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: ?: MERGE  (r5_1 android.os.Parcel) = (r5_0 'parcel' android.os.Parcel), (r1_0 'obtain' android.os.Parcel) in method: com.google.android.a.a.a(int, android.os.Parcel):android.os.Parcel
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:101)
	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:84)
	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:625)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:343)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
	... 20 more
Caused by: jadx.core.utils.exceptions.CodegenException: MERGE can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:530)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:514)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:211)
	... 25 more

*/

    //Todo: different from source
    public final void b(int i, Parcel parcel) {
        Parcel obtain = Parcel.obtain();
        try {
            this.binder.transact(i, parcel, obtain, 0);
            obtain.readException();
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            parcel.recycle();
            obtain.recycle();
        }
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
