package com.sagereal.srfactorymode.Utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.sagereal.srfactorymode.R;


public class SharePreferenceUtils {

     public static void save(Context context,int position,int result){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.sr_factory_mode), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //为不同的position值保存不同的result，不会相互覆盖。
        editor.putInt(context.getResources().getString(R.string.position)+position, result);
        //提交修改
        editor.apply();
    }
}
