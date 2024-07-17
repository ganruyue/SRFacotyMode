package com.sagereal.srfactorymode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;
import com.sagereal.srfactorymode.Utils.ToastUtils;
import com.sagereal.srfactorymode.databinding.ActivityReceiverTestBinding;


import java.io.IOException;

public class ReceiverTestActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityReceiverTestBinding binding;
    private int position = 6;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private BroadcastReceiver headphonesReceiver;
    // 耳机插拔状态，有耳机不播放
    private boolean plugHeadphones = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_test);
        setTitle(R.string.ReceiverTest);

        binding = ActivityReceiverTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.ReceiverTip.setOnClickListener(this);
        binding.pass.setOnClickListener(this);
        binding.fail.setOnClickListener(this);
        //注册耳机插拔状态变化的广播接收器
        registerHeadphonesReceiver();
        // 获取AudioManager实例
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 设置音量控制流
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean has_receiver(){
        // 检查设备是否支持听筒
        if (!supportReceiver()) {
            ToastUtils.showToast(this, getString(R.string.receiver_no), Toast.LENGTH_SHORT);
            return false;
        }
        else return true;
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
        // 设置音频路由为听筒
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setSpeakerphoneOn(false);

            // 创建MediaPlayer并设置要播放的音乐文件
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build());

            try {
                // 设置MediaPlayer文件
                mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.music));
                // 设置准备监听器，当MediaPlayer准备好时开始播放
                mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                // 异步准备MediaPlayer，这样不会阻塞主线程
                mediaPlayer.prepareAsync();
                // 设置播放完成监听器，当MediaPlayer播放完成时恢复音频路由为默认值
                mediaPlayer.setOnCompletionListener(mp -> {
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                    audioManager.setSpeakerphoneOn(true);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick (View v){
        if (v.getId() == R.id.pass) {
            if (!has_receiver()){
                //不支持听筒，不能点击通过
                ToastUtils.showToast(this,getString(R.string.receiver_no),Toast.LENGTH_SHORT);
                return;
            }
            if(plugHeadphones)
            {
                ToastUtils.showToast(this,getString(R.string.headphone_in),Toast.LENGTH_SHORT);
                return;
            }
            SharePreferenceUtils.save(v.getContext(), position, 1);
            Intent intent = new Intent(getApplicationContext(), SingleTestActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if (v.getId() == R.id.fail) {
            SharePreferenceUtils.save(v.getContext(), position, 0);
            Intent intent = new Intent(getApplicationContext(), SingleTestActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void releaseMediaPlayer(){
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, ReceiverTestActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean supportReceiver() {
        if (audioManager != null) {
            // 获取所有输出音频设备的信息
            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo deviceInfo : devices) {
                // 检查设备类型是否为内置听筒
                if (deviceInfo.getType() == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE) {
                    return true;
                }
            }
        }
        return false;
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