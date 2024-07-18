package com.sagereal.srfactorymode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;


import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;
import com.sagereal.srfactorymode.databinding.ActivityLcdtestBinding;

public class LCDTestActivity extends AppCompatActivity implements View.OnClickListener{

    //要改成全屏测试
    private ActivityLcdtestBinding binding;
    private int count = 0;
    private int position = 4;
    int[] color = {R.color.red, R.color.green, R.color.gray, R.color.white};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLcdtestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.LCDTest);

        binding.btnStartLcd.setOnClickListener(this);
        binding.pass.setOnClickListener(this);
        binding.fail.setOnClickListener(this);
        binding.lcd.setOnClickListener(this);
        binding.LCDT.setVisibility(View.INVISIBLE);
        binding.pass.setVisibility(View.INVISIBLE);
        binding.fail.setVisibility(View.INVISIBLE);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_lcd) {
            binding.LCDT.setVisibility(View.VISIBLE);
            binding.btnStartLcd.setVisibility(View.GONE);
            binding.lcd.setBackgroundColor(getResources().getColor(color[count % 4]));
            //移除整个状态栏，包括时间、电量等信息
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
            hideSysUI();
            count++;
        }
        else if (v.getId() == R.id.lcd && binding.btnStartLcd.getVisibility() == View.GONE && count > 0) {
            if (count == 3) {
                binding.pass.setVisibility(View.VISIBLE);
                binding.fail.setVisibility(View.VISIBLE);
            }
            //View类的一个方法，用于设置视图的背景颜色
            //count % 4是一个取模运算，用于确保count的值始终在0到3之间循环
            binding.lcd.setBackgroundColor(getResources().getColor(color[count % 4]));
            count++;
            if (count > 3) {
                binding.lcd.setClickable(false);
            }
        }
        else if (v.getId() == R.id.pass) {
            SharePreferenceUtils.save(v.getContext(), position, 1);
            // 跳转至单项测试列表页面
            finish();
        }
        else if (v.getId() == R.id.fail) {
            SharePreferenceUtils.save(v.getContext(), position, 0);
            // 跳转至单项测试列表页面
            finish();
        }
    }

    private void hideSysUI() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getActionBar();
            if (null!=actionBar) {
                actionBar.hide();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSysUI();
        }
    }

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, LCDTestActivity.class));
    }
}