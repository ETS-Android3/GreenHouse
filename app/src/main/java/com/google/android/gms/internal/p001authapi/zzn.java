package com.google.android.gms.internal.p001authapi;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

/* compiled from: com.google.android.gms:play-services-auth@@18.0.0 */
/* renamed from: com.google.android.gms.internal.auth-api.zzn  reason: invalid package */
/* loaded from: classes.dex */
final class zzn extends zzo<Status> {
    private final /* synthetic */ Credential zzao;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zzn(zzj zzjVar, GoogleApiClient googleApiClient, Credential credential) {
        super(googleApiClient);
        this.zzao = credential;
    }

    @Override // com.google.android.gms.internal.p001authapi.zzo
    protected final void zzc(Context context, zzx zzxVar) throws RemoteException {
        zzxVar.zzc(new zzp(this), new zzt(this.zzao));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.api.internal.BasePendingResult
    public final /* synthetic */ Result createFailedResult(Status status) {
        return status;
    }
}
