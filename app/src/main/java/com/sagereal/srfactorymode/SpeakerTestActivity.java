package com.sagereal.srfactorymode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;
import com.sagereal.srfactorymode.Utils.ToastUtils;
import com.sagereal.srfactorymode.databinding.ActivityMicrophoneTestBinding;
import com.sagereal.srfactorymode.databinding.ActivitySpeakerTestBinding;

public class SpeakerTestActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySpeakerTestBinding binding;
    private int position = 5;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private BroadcastReceiver headphonesReceiver;
    // 耳机插拔状态，有耳机不播放
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
         //注册耳机插拔状态变化的广播接收器
        registerHeadphonesReceiver();
        //初始化音量为一半
        init_volume();
    }

    private void init_volume(){
        // 获取AudioManager实例
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volume = maxVolume / 2;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    //广播接收器
    private void registerHeadphonesReceiver() {
        headphonesReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("state")) {
                    checkHeadphones();
                }
            }
        };
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(headphonesReceiver, filter);
    }

    //检查耳机状态
    private void checkHeadphones() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 判断是否插入耳机并进行提示
        if (audioManager.isWiredHeadsetOn()) {
            ToastUtils.showToast(this, getString(R.string.headphone_in), Toast.LENGTH_SHORT);
            plugHeadphones = true;
            // 若插入了耳机则暂停播放
            onPause();
        } else if (!audioManager.isWiredHeadsetOn() && plugHeadphones) {
            ToastUtils.showToast(this, getString(R.string.headphone_out), Toast.LENGTH_SHORT);
            plugHeadphones = false;
            onRestart();
        }
    }

    private void playMusic(){
        //有耳机不播放
        if(plugHeadphones){
            return;
        }
        // 创建MediaPlayer并设置要播放的音乐文件
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        if (mediaPlayer != null) {
            // 设置音频流的类型为音乐流。这是在Android中用于区分不同音频用途的标识，比如音乐、电话铃声、系统通知等。
            // 使用STREAM_MUSIC表示这个音频流是用于播放音乐的，这会影响音量控制和音频焦点等行为。
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过AudioAttributes.Builder构建一个AudioAttributes对象
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA) // 设置音频的用途为媒体
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) // 设置音频的内容类型为音乐
                    .build()); // 构建AudioAttributes对象
            mediaPlayer.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick (View v){
        if (v.getId() == R.id.pass) {
            if(plugHeadphones)
            {
                ToastUtils.showToast(this,getString(R.string.headphone_in),Toast.LENGTH_SHORT);
                return;
            }
            SharePreferenceUtils.save(v.getContext(), position, 1);
            // 跳转至单项测试列表页面
            finish();
        }
        if (v.getId() == R.id.fail) {
            SharePreferenceUtils.save(v.getContext(), position, 0);
            // 跳转至单项测试列表页面
            finish();
        }
    }

    private void releaseMediaPlayer(){
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, SpeakerTestActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            releaseMediaPlayer();
        }
        playMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        playMusic();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        releaseMediaPlayer();
        unregisterReceiver(headphonesReceiver);
    }
}