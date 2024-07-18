package com.sagereal.srfactorymode;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;
import com.sagereal.srfactorymode.Utils.ToastUtils;
import com.sagereal.srfactorymode.databinding.ActivityHeadphoneTestBinding;
import com.sagereal.srfactorymode.databinding.ActivityMicrophoneTestBinding;

import java.io.IOException;

public class HeadphoneTestActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityHeadphoneTestBinding binding;
    private int position = 3;

    /*MediaRecorder用于录音，MediaPlayer用于播放录音，Handler来管理时间延迟*/
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;   //文件存放位置
    private Handler handler = new Handler();

    private boolean isRecording = false;
    private boolean isPlaying = false;

    // 耳机插拔状态，有耳机不测试
    private boolean plugHeadphones = false;
    private BroadcastReceiver headphonesReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headphone_test);
        setTitle(R.string.HeadphoneTest);

        binding = ActivityHeadphoneTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.recodeBtn.setOnClickListener(this);
        binding.pass.setOnClickListener(this);
        binding.fail.setOnClickListener(this);

        //如果没有权限再申请一次权限
        askForPermission();
        // 注册耳机插拔状态变化的广播接收器
        registerHeadphonesReceiver();
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

    private void checkHeadphones() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 判断是否插入耳机
        if (audioManager.isWiredHeadsetOn()) {
            // 耳机已插入
            ToastUtils.showToast(this, getString(R.string.headphone_in), Toast.LENGTH_SHORT);
            plugHeadphones = true; // 标记耳机已插入
        } else {
            // 耳机未插入或已拔出
            ToastUtils.showToast(this, getString(R.string.headphone_out), Toast.LENGTH_SHORT);
            plugHeadphones = false; // 标记耳机未插入
            // 若拔下了耳机并在测试中刷新该页面
            if (!binding.recodeBtn.isEnabled()) {
                recreate();
            }
        }
    }

    private boolean checkPermissions() {
        // 检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            return false; // 权限尚未授权，返回false
        }
        return true; // 权限已经授权，返回true
    }

    private void askForPermission() {
        if (!checkPermissions()) {
            // 请求录音权限
            showPermissionDialog();
        } else return;
    }

    private void showPermissionDialog() {
        //使用AlertDialog.Builder类来创建一个新的对话框构建器对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.permission_tittle));
        builder.setMessage(getString(R.string.permission_message));
        //点击按钮，触发OnClickListener，转到应用的设置页面
        builder.setPositiveButton(getString(R.string.GoSet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        //点击按钮，关闭对话框
        builder.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false); // 禁止点击对话框外部取消
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.recode_btn && !isRecording) {
            if(!plugHeadphones)
            {   //耳机未插入状态不可以测试
                ToastUtils.showToast(this,getString(R.string.headphone_out),Toast.LENGTH_SHORT);
                return;
            }
            if (!checkPermissions()) {
                showPermissionDialog();
            } else {
                startRecording();
            }
            return;
        }
        if (v.getId() == R.id.pass) {
            //没测试或者测试未完成不能点击通过
            if (binding.recodeBtn.getText() != getString(R.string.retest)) {
                ToastUtils.showToast(v.getContext(), getString(R.string.test_fail), Toast.LENGTH_SHORT);
            } else {
                SharePreferenceUtils.save(v.getContext(), position, 1);
                // 跳转至单项测试列表页面
                finish();
            }
        }
        if (v.getId() == R.id.fail) {
            SharePreferenceUtils.save(v.getContext(), position, 0);
            // 跳转至单项测试列表页面
            finish();
        }
    }

    //开始录音
    private void startRecording() {
        binding.Result.setText(R.string.recording);
        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/recorded_audio.mp4";
        //创建一个MediaRecorder实例，并设置其音频源、输出格式、输出文件和音频编码器。这些配置决定了录音的源格式和存储位置。
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }
        //调用prepare()方法准备MediaRecorder进行录制。这是一个同步调用，可能会抛出IOException。
        //如果准备成功，调用start()方法开始录制。
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            binding.recodeBtn.setEnabled(false);   //开始录制后禁用按钮
            binding.recodeBtn.setText(R.string.testing); //测试中
            binding.Result.setVisibility(View.VISIBLE);
            binding.Result.setText(R.string.recording);  //录音中,请说话...
        } catch (IOException e) {
            e.printStackTrace();
        }
        //使用handler.postDelayed()安排一个延迟任务，该任务在5秒后执行。
        //  当延迟时间到达时，将调用stopRecording()和startPlaying()方法。
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRecording();
            }
        }, 5000); // 5秒后自动播放录音
    }

    //停止录音
    void stopRecording() {
        binding.Result.setText(R.string.record_finish);
        if (mediaRecorder != null) {
            try {
                isRecording = false;
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                startPlaying();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    //开始播放
    private void startPlaying() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        try {
            isPlaying = true;
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            // 播放完成
            mediaPlayer.setOnCompletionListener(mp -> {
                stopPlaying();
                binding.Result.setText(R.string.record_finish_test);
                binding.recodeBtn.setText(R.string.retest);
                binding.recodeBtn.setEnabled(true);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //停止播放
    private void stopPlaying() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                isPlaying= false ;
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRecording();
        stopPlaying();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        binding.recodeBtn.setText(R.string.retest);
        binding.recodeBtn.setEnabled(true);
        binding.Result.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headphonesReceiver);
    }

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, HeadphoneTestActivity.class));
    }
}