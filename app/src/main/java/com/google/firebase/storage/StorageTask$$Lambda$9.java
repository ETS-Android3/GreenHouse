package com.google.firebase.storage;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.firebase:firebase-storage@@19.1.1 */
/* loaded from: classes.dex */
public final /* synthetic */ class StorageTask$$Lambda$9 implements OnCompleteListener {
    private final StorageTask arg$1;
    private final Continuation arg$2;
    private final TaskCompletionSource arg$3;

    private StorageTask$$Lambda$9(StorageTask storageTask, Continuation continuation, TaskCompletionSource taskCompletionSource) {
        this.arg$1 = storageTask;
        this.arg$2 = continuation;
        this.arg$3 = taskCompletionSource;
    }

    public static OnCompleteListener lambdaFactory$(StorageTask storageTask, Continuation continuation, TaskCompletionSource taskCompletionSource) {
        return new StorageTask$$Lambda$9(storageTask, continuation, taskCompletionSource);
    }

    @Override // com.google.android.gms.tasks.OnCompleteListener
    public void onComplete(Task task) {
        StorageTask.lambda$continueWithImpl$4(this.arg$1, this.arg$2, this.arg$3, task);
    }
}
