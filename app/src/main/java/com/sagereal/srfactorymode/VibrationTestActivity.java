package com.sagereal.srfactorymode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;
import com.sagereal.srfactorymode.databinding.ActivityVibrationTestBinding;

public class VibrationTestActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityVibrationTestBinding binding;
    private boolean is_vibration = false;
    private int position = 1;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibration_test);
        setTitle(R.string.VibrationTest);

        binding = ActivityVibrationTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 通过Vibrator类来实现震动功能
        binding.pass.setOnClickListener(this);
        binding.fail.setOnClickListener(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pass) {
            if(!vibrator.hasVibrator()){
                Toast.makeText(v.getContext(),getString(R.string.noVibration),Toast.LENGTH_SHORT).show();
                return;}
            else {
                SharePreferenceUtils.save(v.getContext(), position, 1);
                //创建一个新的Intent，指向SingleTestActivity。
                //这个Intent被设置为清除当前任务栈中该Activity之上的所有Activity（通过intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);实现）
                //这样用户就会直接看到SingleTestActivity的实例，而不是在其上堆叠新的实例。
                Intent intent = new Intent(getApplicationContext(), SingleTestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
        if (v.getId() == R.id.fail) {
            SharePreferenceUtils.save(v.getContext(), position, 0);
            Intent intent = new Intent(getApplicationContext(), SingleTestActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // 如果在振动，取消振动
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.cancel();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //检查设备是否支持振动
        // 检查设备是否支持振动
        if (!vibrator.hasVibrator()) {
            // 设备不支持振动，弹出提示消息
            Toast.makeText(this, R.string.noVibration, Toast.LENGTH_LONG).show();
        } else {
            // 设备支持振动，进行振动操作
            long[] pattern = {1000, 1000}; //震动1000ms，停止1000ms
            vibrator.vibrate(pattern, 0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        vibrator.cancel();
    }

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, VibrationTestActivity.class));
    }
}