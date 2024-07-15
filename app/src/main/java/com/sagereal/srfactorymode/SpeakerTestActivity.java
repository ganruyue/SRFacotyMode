package com.sagereal.srfactorymode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sagereal.srfactorymode.databinding.ActivityMicrophoneTestBinding;
import com.sagereal.srfactorymode.databinding.ActivitySpeakerTestBinding;

public class SpeakerTestActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySpeakerTestBinding binding;
    private int position = 5;

    private BroadcastReceiver headphonesReceiver;
    // 耳机插拔状态，有耳机不测试
    private boolean plugHeadphones = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_test);
        setTitle(R.string.SpeakerTest);

        binding = ActivitySpeakerTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.SpeakerTip.setOnClickListener(this);
        binding.pass.setOnClickListener(this);
        binding.fail.setOnClickListener(this);
        
        // 注册耳机插拔状态变化的广播接收器
        registerHeadphonesReceiver();
    }


    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, SpeakerTestActivity.class));
    }
}