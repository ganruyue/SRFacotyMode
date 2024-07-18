package com.sagereal.srfactorymode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;
import com.sagereal.srfactorymode.Utils.ToastUtils;
import com.sagereal.srfactorymode.databinding.ActivityKeyTestBinding;
import com.sagereal.srfactorymode.databinding.ActivityMicrophoneTestBinding;
import com.sagereal.srfactorymode.databinding.ActivityReceiverTestBinding;

public class KeyTestActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityKeyTestBinding binding;
    private int position = 9;
    private BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        //电源键只可以检测黑屏
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                binding.powerVolume.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_test);
        setTitle(R.string.KeyTest);

        binding = ActivityKeyTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.downVolume.setOnClickListener(this);
        binding.upVolume.setOnClickListener(this);
        binding.powerVolume.setOnClickListener(this);
        binding.pass.setOnClickListener(this);
        binding.fail.setOnClickListener(this);

    }

    //音量键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                binding.upVolume.setVisibility(View.INVISIBLE);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                binding.downVolume.setVisibility(View.INVISIBLE);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }
   //按电源键又打开后恢复
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(powerReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销BroadcastReceiver
        unregisterReceiver(powerReceiver);
    }

    private boolean all_done(){
        if(binding.upVolume.getVisibility()== View.INVISIBLE&&
                binding.downVolume.getVisibility()== View.INVISIBLE &&
                binding.powerVolume.getVisibility()== View.INVISIBLE
        ){return true;}
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pass) {
            //没点完不能通过
            if(!all_done()){
                ToastUtils.showToast(v.getContext(),getString(R.string.key_tip), Toast.LENGTH_SHORT);
                return;}
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

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, KeyTestActivity.class));
    }
}