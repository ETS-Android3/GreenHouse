package androidx.core.app;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;

/* loaded from: classes.dex */
public final class AppOpsManagerCompat {
    public static final int MODE_ALLOWED = 0;
    public static final int MODE_DEFAULT = 3;
    public static final int MODE_ERRORED = 2;
    public static final int MODE_IGNORED = 1;

    private AppOpsManagerCompat() {
    }

    public static String permissionToOp(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            return AppOpsManager.permissionToOp(permission);
        }
        return null;
    }

    public static int noteOp(Context context, String op, int uid, String packageName) {
        if (Build.VERSION.SDK_INT < 19) {
            return 1;
        }
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService("appops");
        return appOpsManager.noteOp(op, uid, packageName);
    }

    public static int noteOpNoThrow(Context context, String op, int uid, String packageName) {
        if (Build.VERSION.SDK_INT < 19) {
            return 1;
        }
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService("appops");
        return appOpsManager.noteOpNoThrow(op, uid, packageName);
    }

    public static int noteProxyOp(Context context, String op, String proxiedPackageName) {
        if (Build.VERSION.SDK_INT < 23) {
            return 1;
        }
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        return appOpsManager.noteProxyOp(op, proxiedPackageName);
    }

    public static int noteProxyOpNoThrow(Context context, String op, String proxiedPackageName) {
        if (Build.VERSION.SDK_INT < 23) {
            return 1;
        }
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        return appOpsManager.noteProxyOpNoThrow(op, proxiedPackageName);
    }
}
