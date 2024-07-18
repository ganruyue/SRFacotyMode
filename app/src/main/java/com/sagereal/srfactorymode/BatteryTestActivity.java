package com.sagereal.srfactorymode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.sagereal.srfactorymode.Utils.ToastUtils;
import com.sagereal.srfactorymode.databinding.ActivityBatteryTestBinding;
import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;


public class BatteryTestActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityBatteryTestBinding binding;
    private boolean isCharging = false;
    private int position = 0;
    // 假设的wasCharging变量，用于跟踪上次的充电状态
    private boolean wasCharging = false;
    private int charge_change = 0;
    //需要修改，未充电充电各一次才可以点击通过,有状态改变数字加1


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_test);
        setTitle(R.string.BatteryTest);

        binding = ActivityBatteryTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // 注册BroadcastReceiver
        IntentFilter filter = new IntentFilter(); //IntentFilter用于指定BroadcastReceiver应该接收哪些类型的广播
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);  //向过滤器添加了一个action 任何注册了此action的BroadcastReceiver都会接收到这个广播
        registerReceiver(batteryReceiver, filter);

        //按钮绑定
        binding.pass.setOnClickListener((View.OnClickListener)this);
        binding.fail.setOnClickListener((View.OnClickListener)this);
    }



        private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            //充电状态
            // 充电状态改变时调用
            if (isCharging != wasCharging) {
                charge_change++;
            }
            wasCharging = isCharging;

    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            //电量
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int)(((float)level/scale)*100);

            //电压
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            //温度
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            // 温度是以十分之一摄氏度为单位的，需要将其转换为摄氏度
            float temperatureInCelsius = temperature / 10.0f;

            // 更新UI
            runOnUiThread(() -> {
                binding.chargeStatus.setText(getString(R.string.ChargeStatus) + " " +
                        (isCharging ? getString(R.string.is_charging) : getString(R.string.Uncharged)));
                binding.baPercentage.setText(getString(R.string.BatteryPercentage) + " "+batteryPct+"%");
                binding.baVoltage.setText(getString(R.string.BatteryVoltage)+" "+voltage+" mV");
                binding.baTemperature.setText(getString(R.string.BatteryTemperature)+" "+temperatureInCelsius+" ℃");
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销BroadcastReceiver
        unregisterReceiver(batteryReceiver);
    }

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, BatteryTestActivity.class));
    }

    //通过失败
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pass) {
//            if(!isCharging){
//                ToastUtils.showToast(v.getContext(),getString(R.string.battery_tip),Toast.LENGTH_SHORT);
//                return;}
            if(charge_change == 0){
                ToastUtils.showToast(v.getContext(),getString(R.string.battery_tip1),Toast.LENGTH_SHORT);
            }
            else{
                SharePreferenceUtils.save(v.getContext(), position, 1);
                // 跳转至单项测试列表页面
                finish();
                }
            }
        if (v.getId() == R.id.fail){
            SharePreferenceUtils.save(v.getContext(),position,0);
            // 跳转至单项测试列表页面
            finish();
        }
    }
}