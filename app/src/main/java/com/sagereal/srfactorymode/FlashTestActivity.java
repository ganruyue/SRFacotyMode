package com.sagereal.srfactorymode;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;
import com.sagereal.srfactorymode.databinding.ActivityFlashTestBinding;
import com.sagereal.srfactorymode.databinding.ActivityVibrationTestBinding;

public class FlashTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityFlashTestBinding binding;
    private CameraManager cameraManager;
    private String[] cameraId;
    private int position = 8;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_test);
        setTitle(R.string.FlashlightTest);

        binding = ActivityFlashTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.pass.setOnClickListener(this);
        binding.fail.setOnClickListener(this);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList(); // 获取所有摄像头的ID
            if(cameraId.length == 0){
                Toast.makeText(this,getString(R.string.no_flash),Toast.LENGTH_LONG).show();
                return;
            }
            if(cameraId.length == 1){
                Toast.makeText(this,getString(R.string.one_flash),Toast.LENGTH_LONG).show();
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        try {
            // 打开所有闪光灯
            for (String flashId : cameraId){
                cameraManager.setTorchMode(flashId, true);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPause() {
        super.onPause();
        try {
            for (String flashId : cameraId){
                // 关闭闪光灯
                cameraManager.setTorchMode(flashId, false);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick (View v){
            if (v.getId() == R.id.pass) {
                SharePreferenceUtils.save(v.getContext(), position, 1);
                //创建一个新的Intent，指向SingleTestActivity。
                //这个Intent被设置为清除当前任务栈中该Activity之上的所有Activity（通过intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);实现）
                //这样用户就会直接看到SingleTestActivity的实例，而不是在其上堆叠新的实例。
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

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, FlashTestActivity.class));
    }
}