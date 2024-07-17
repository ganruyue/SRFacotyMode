package com.sagereal.srfactorymode.Utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.sagereal.srfactorymode.R;

public class SharePreferenceUtils {
     private static final String PREFS_NAME = "sr_factory_mode_shared_prefs"; // 定义 SharedPreferences 文件的名称
     private static final String PREFIX = "single_item_position_"; // 定义键值前缀

     public static void save(Context context,int position,int result){
         SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
         SharedPreferences.Editor editor = sharedPreferences.edit();
         //为不同的position值保存不同的result，不会相互覆盖。
         editor.putInt(PREFIX + position, result); // 将测试结果 result 存储到由前缀和位置组成的键值中
        //提交修改,apply()是异步的，不会阻塞主进程
         editor.apply();
    }

    public static int getResult(Context context, int position, int defaultValue) {
        // 获取SharedPreferences实例
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // 构造键名
        String key = PREFIX+context.getResources().getString(R.string.position) + position;
        // 从SharedPreferences中获取对应的值，如果没有找到则返回默认值（这里假设默认值为-1）
        return sharedPreferences.getInt(PREFIX + position, defaultValue);
    }
}
