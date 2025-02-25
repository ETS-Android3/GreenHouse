package com.google.android.gms.common.internal;

/* compiled from: com.google.android.gms:play-services-basement@@17.1.1 */
/* loaded from: classes.dex */
public final class zzh {
    private final String packageName;
    private final int zzek = 129;
    private final boolean zzel;
    private final String zzet;
    private final boolean zzeu;

    public zzh(String str, String str2, boolean z, int i, boolean z2) {
        this.packageName = str;
        this.zzet = str2;
        this.zzeu = z;
        this.zzel = z2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String zzt() {
        return this.zzet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String getPackageName() {
        return this.packageName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int zzq() {
        return this.zzek;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean getUseDynamicLookup() {
        return this.zzel;
    }
}
