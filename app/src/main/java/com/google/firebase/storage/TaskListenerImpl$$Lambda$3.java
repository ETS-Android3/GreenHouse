package com.google.firebase.storage;

import com.google.firebase.storage.StorageTask;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.firebase:firebase-storage@@19.1.1 */
/* loaded from: classes.dex */
public final /* synthetic */ class TaskListenerImpl$$Lambda$3 implements Runnable {
    private final TaskListenerImpl arg$1;
    private final Object arg$2;
    private final StorageTask.ProvideError arg$3;

    private TaskListenerImpl$$Lambda$3(TaskListenerImpl taskListenerImpl, Object obj, StorageTask.ProvideError provideError) {
        this.arg$1 = taskListenerImpl;
        this.arg$2 = obj;
        this.arg$3 = provideError;
    }

    public static Runnable lambdaFactory$(TaskListenerImpl taskListenerImpl, Object obj, StorageTask.ProvideError provideError) {
        return new TaskListenerImpl$$Lambda$3(taskListenerImpl, obj, provideError);
    }

    @Override // java.lang.Runnable
    public void run() {
        this.arg$1.onRaise.raise(this.arg$2, this.arg$3);
    }
}
