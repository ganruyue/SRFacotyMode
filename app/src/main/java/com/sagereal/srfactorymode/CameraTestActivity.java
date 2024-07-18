package com.sagereal.srfactorymode;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CaptureRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.hardware.Camera;
import android.os.Handler;
import android.provider.Settings;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;
import com.sagereal.srfactorymode.Utils.ToastUtils;
import com.sagereal.srfactorymode.databinding.ActivityCameraTestBinding;
import com.sagereal.srfactorymode.databinding.ActivityMicrophoneTestBinding;

import java.io.IOException;

public class CameraTestActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {
    private ActivityCameraTestBinding binding;
    private int position = 7;
    private boolean already_click = false;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private int mCameraStatus = Camera.CameraInfo.CAMERA_FACING_BACK; // 默认后置摄像头
    private long mLastClickTime = 0; // 记录上次点击时间戳
    private static final long CLICK_INTERVAL = 2000; // 限制的点击间隔，单位毫秒

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        setTitle(getString(R.string.CameraTest));
        askForPermission();
        binding = ActivityCameraTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mSurfaceView = binding.cameraView;
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        setOnClickListeners(binding.switchBtn, binding.pass, binding.fail);
    }

    //切换相机
    private void switch_Camera() {
        // 切换相机状态
        mCameraStatus = (mCameraStatus == Camera.CameraInfo.CAMERA_FACING_BACK) ?
                Camera.CameraInfo.CAMERA_FACING_FRONT :
                Camera.CameraInfo.CAMERA_FACING_BACK;
        binding.switchBtn.setText((mCameraStatus == Camera.CameraInfo.CAMERA_FACING_BACK) ? R.string.front_camera : R.string.rear_camera);
        //先释放再打开
        releaseCamera();
        openCamera();
    }

    //打开摄像头
    private void openCamera() {
        try {
            mCamera = Camera.open(mCameraStatus);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
//            finalPreviewRequestBuilder1.set(CaptureRequest.CONTROL_AF_MODE,
//                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            /**暂时还不会写对焦**/

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //释放资源
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private boolean checkPermissions() {
        // 检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false; // 权限尚未授权，返回false
        }
        return true; // 权限已经授权，返回true
    }

    private void askForPermission() {
        if (!checkPermissions()) {
            // 请求相机权限
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

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        releaseCamera();
    }

     //设置点击事件监听器
    private void setOnClickListeners(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }
        public static void openActivity(Context context) {
        context.startActivity(new Intent(context, CameraTestActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v.getId()  == R.id.switch_btn) {
            // 限制时间内连续点击
            if (System.currentTimeMillis() - mLastClickTime < CLICK_INTERVAL) {
                // 提示用户不能连续点击
                ToastUtils.showToast(this, getString(R.string.no_click), Toast.LENGTH_SHORT);
                return;
            }
            // 更新上次点击时间
            mLastClickTime = System.currentTimeMillis();
            // 反转相机
            switch_Camera();
            already_click = true;
        } else if (v.getId() == R.id.pass) {
            // 还未反转过相机
            if (!already_click) {
                ToastUtils.showToast(this, getString(R.string.camera_tip), Toast.LENGTH_SHORT);
            } else {
                SharePreferenceUtils.save(v.getContext(), position, 1);
                // 跳转至单项测试列表页面
                finish();
            }
        } if (v.getId() == R.id.fail) {
            SharePreferenceUtils.save(v.getContext(), position, 0);
            // 跳转至单项测试列表页面
            finish();
        }
    }
}