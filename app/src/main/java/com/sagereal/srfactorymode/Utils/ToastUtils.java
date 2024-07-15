package com.sagereal.srfactorymode.Utils;

import android.content.Context;
import android.widget.Toast;

//为了不一直显示
public class ToastUtils {
    private static Toast lastToast;
    public static void showToast(Context context, String message, int duration) {
        // 如果上一个Toast存在，则取消掉
        if (lastToast != null) {
            lastToast.cancel();
        }
        lastToast = Toast.makeText(context, message, duration);
        lastToast.show();
    }
}