package com.google.android.gms.internal.p001authapi;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.GmsClient;

/* compiled from: com.google.android.gms:play-services-auth@@18.0.0 */
/* renamed from: com.google.android.gms.internal.auth-api.zzq  reason: invalid package */
/* loaded from: classes.dex */
public final class zzq extends GmsClient<zzx> {
    private final Auth.AuthCredentialsOptions zzar;

    public zzq(Context context, Looper looper, ClientSettings clientSettings, Auth.AuthCredentialsOptions authCredentialsOptions, GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, 68, clientSettings, connectionCallbacks, onConnectionFailedListener);
        this.zzar = new Auth.AuthCredentialsOptions.Builder(authCredentialsOptions == null ? Auth.AuthCredentialsOptions.zzk : authCredentialsOptions).zzc(zzal.zzr()).zzd();
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient
    protected final String getStartServiceAction() {
        return "com.google.android.gms.auth.api.credentials.service.START";
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient
    protected final String getServiceDescriptor() {
        return "com.google.android.gms.auth.api.credentials.internal.ICredentialsService";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gms.common.internal.BaseGmsClient
    public final Bundle getGetServiceRequestExtraArgs() {
        return this.zzar.toBundle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Auth.AuthCredentialsOptions zze() {
        return this.zzar;
    }

    @Override // com.google.android.gms.common.internal.GmsClient, com.google.android.gms.common.internal.BaseGmsClient, com.google.android.gms.common.api.Api.Client
    public final int getMinApkVersion() {
        return 12800000;
    }

    @Override // com.google.android.gms.common.internal.BaseGmsClient
    protected final /* synthetic */ IInterface createServiceInterface(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.auth.api.credentials.internal.ICredentialsService");
        if (queryLocalInterface instanceof zzx) {
            return (zzx) queryLocalInterface;
        }
        return new zzw(iBinder);
    }
}
