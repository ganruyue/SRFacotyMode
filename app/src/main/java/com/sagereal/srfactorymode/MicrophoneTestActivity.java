package com.sagereal.srfactorymode;

import static com.sagereal.srfactorymode.AudioRecordingService.ACTION_UPDATE_UI_END;
import static com.sagereal.srfactorymode.AudioRecordingService.ACTION_UPDATE_UI_START;
import static com.sagereal.srfactorymode.AudioRecordingService.Action_UPDATE_UI_FINISH;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;
import com.sagereal.srfactorymode.databinding.ActivityMicrophoneTestBinding;

public class MicrophoneTestActivity extends AppCompatActivity implements View.OnClickListener {

        private ActivityMicrophoneTestBinding binding;
        private int position = 2;

        private Handler handler = new Handler();
        private boolean isRecording = false;
        private long lastTime = 0;

        private AudioRecordingService.LocalBinder serviceBinder;
        private ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                serviceBinder = (AudioRecordingService.LocalBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                serviceBinder = null;
            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityMicrophoneTestBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setTitle(R.string.MicrophoneTest);
            binding.recodeBtn.setOnClickListener(this);
            binding.pass.setOnClickListener(this);
            binding.fail.setOnClickListener(this);

            // 注册BroadcastReceiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_UPDATE_UI_START);
            filter.addAction(ACTION_UPDATE_UI_END);
            filter.addAction(Action_UPDATE_UI_FINISH);
            LocalBroadcastManager.getInstance(this).registerReceiver(updateUIReceiver, filter);

            //如果没有权限再申请一次权限
            askForPermission();
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
                if (!checkPermissions()) {
                    showPermissionDialog();
                    return;
                } else {
                    Intent intent = new Intent(this, AudioRecordingService.class);
                    startService(intent);
                    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                }
            }
            if (v.getId() == R.id.pass) {
                //没测试或者测试未完成不能点击通过
                if (binding.recodeBtn.getText() != getString(R.string.retest)) {
                    Toast.makeText(v.getContext(), getString(R.string.miko_fail), Toast.LENGTH_SHORT).show();
                } else {
                    SharePreferenceUtils.save(v.getContext(), position, 1);
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

        public static void openActivity(Context context) {
            context.startActivity(new Intent(context, MicrophoneTestActivity.class));
        }

        //接受广播
    private BroadcastReceiver updateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_UPDATE_UI_START.equals(action)) {
                // 开始测试
                sendUIUpdateBroadcast_start();
            } if (ACTION_UPDATE_UI_END.equals(action)) {
                //录音结束开始播放
                sendUIUpdateBroadcast_end();
            } else if (Action_UPDATE_UI_FINISH.equals(action)){
                //测试完成-》重测
                sendUIUpdateBroadcast_finish();
            }
        }

        private void sendUIUpdateBroadcast_start() {
            binding.recodeBtn.setEnabled(false);   //开始录制后禁用按钮
            binding.recodeBtn.setText(R.string.testing); //测试中
            binding.Result.setText(R.string.recording);  //录音中,请说话...
        }

        private void sendUIUpdateBroadcast_end() {
            binding.Result.setText(R.string.record_finish);
        }

        private void sendUIUpdateBroadcast_finish(){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.Result.setText(R.string.record_finish_test);
                    binding.recodeBtn.setText(R.string.retest);
                    binding.recodeBtn.setEnabled(true);
                }
            }, 5000);
        }
    };

        @Override
        protected void onDestroy() {
            super.onDestroy();
            // 注销BroadcastReceiver
            LocalBroadcastManager.getInstance(this).unregisterReceiver(updateUIReceiver);
        }
    }

