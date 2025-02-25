package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.PendingResultUtil;

/* compiled from: com.google.android.gms:play-services-base@@17.1.0 */
/* loaded from: classes.dex */
final class zaj implements PendingResultUtil.ResultConverter<R, T> {
    private final /* synthetic */ Response zapg;

    /* JADX INFO: Access modifiers changed from: package-private */
    public zaj(Response response) {
        this.zapg = response;
    }

    @Override // com.google.android.gms.common.internal.PendingResultUtil.ResultConverter
    public final /* synthetic */ Object convert(Result result) {
        this.zapg.setResult(result);
        return this.zapg;
    }
}
