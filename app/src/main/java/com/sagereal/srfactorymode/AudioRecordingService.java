package com.sagereal.srfactorymode;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;


//Service类用于录音,可以在后台进行

public class AudioRecordingService extends Service {
    //广播的Action，用于标识广播
    // 定义不同的Action
    public static final String ACTION_UPDATE_UI_START = "com.sagereal.srfactorymode.UPDATE_UI_START";
    public static final String ACTION_UPDATE_UI_END = "com.sagereal.srfactorymode.UPDATE_UI_END";
    public static final String Action_UPDATE_UI_FINISH = "com.sagereal.srfactorymode.UPDATE_UI_START";

    //定义一个名为 TAG 的字符串常量，其值为 "AudioRecordingService"。
   //记录日志时，可以使用这个 TAG 来标识这些日志消息是来自音频录制服务的。
    private static final String TAG = "AudioRecordingService";

    /*MediaRecorder用于录音，MediaPlayer用于播放录音，Handler来管理时间延迟*/
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;   //文件存放位置
    private Handler handler = new Handler();
    private boolean isRecording = false;
    private boolean already_r = false;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        AudioRecordingService getService() {
            return AudioRecordingService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY; // 如果服务被系统杀死，尝试重新创建
    }

    //广播

    private void sendUIUpdateBroadcast_start() {
        Intent intent = new Intent(ACTION_UPDATE_UI_START);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }  // 点击开始按钮后更新UI（录音中）

    private void sendUIUpdateBroadcast_end() {
        Intent intent = new Intent(ACTION_UPDATE_UI_END);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    } // 录音完成播放录音

    private void sendUIUpdateBroadcast_finish() {
        Intent intent = new Intent(Action_UPDATE_UI_FINISH);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    } // 录音播放都结束，retest

    //开始录音
    private void startRecording() {
        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/recorded_audio.mp4";
        //创建一个MediaRecorder实例，并设置其音频源、输出格式、输出文件和音频编码器。这些配置决定了录音的源格式和存储位置。
        if (mediaRecorder == null){
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
            //发送广播更新UI
            sendUIUpdateBroadcast_start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //使用handler.postDelayed()安排一个延迟任务，该任务在5秒后执行。
        //  当延迟时间到达时，将调用stopRecording()和startPlaying()方法。
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRecording();
                // 等待MediaRecorder完全停止后再开始播放（可选，取决于你的stopRecording实现）
                // 这里可能需要一个回调或检查机制来确保MediaRecorder已经停止
                startPlaying();
            }
        }, 5000); // 5秒后自动播放录音
        stopPlaying();
    }

    //停止录音
    void stopRecording() {
        if (mediaRecorder != null) {
            try {
                isRecording = false;
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                already_r = true;
                //发送广播更新UI
                sendUIUpdateBroadcast_finish();
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
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mediaRecorder = null;
            sendUIUpdateBroadcast_end();
        }
    }

    //停止播放
    private void stopPlaying(){
        if (mediaPlayer!=null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @Override
    public void onDestroy() {
        stopRecording();
        stopPlaying();
        super.onDestroy();
    }
}