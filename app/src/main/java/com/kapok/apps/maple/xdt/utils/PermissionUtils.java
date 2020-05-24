package com.kapok.apps.maple.xdt.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:动态权限处理类
 */
public class PermissionUtils {
    private static Map<String, CheckResultListener> checkResultListenerMap = new HashMap<>();

    public interface CheckResultListener {
        /**
         * 检查成功
         */
        void checkSuccess();

        /**
         * 检查失败
         *
         * @param permissions 缺少的权限
         */
        void checkFailure(String[] permissions);
    }

    /**
     * 动态权限检查
     */
    public static void checkPermission(Context context, String[] permissions, CheckResultListener checkResultListener) {
        int requestCode = (int) (Math.random() * 100);
        List<String> needRequest = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    needRequest.add(permission);
                }
            }
        }
        if (context instanceof Activity && needRequest.size() > 0) {
            ActivityCompat.requestPermissions((Activity) context, needRequest.toArray(new String[needRequest.size()]), requestCode);
            if (checkResultListener != null)
                checkResultListenerMap.put(String.valueOf(requestCode), checkResultListener);
        } else if (checkResultListener != null) {
            checkResultListener.checkSuccess();
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CheckResultListener listener = checkResultListenerMap.remove(String.valueOf(requestCode));
        List<String> failPermissions = new ArrayList<>();
        if (listener != null) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    failPermissions.add(permissions[i]);
                }
            }
            if (failPermissions.size() == 0)
                listener.checkSuccess();
            else
                listener.checkFailure(failPermissions.toArray(new String[failPermissions.size()]));
        }
    }
}
