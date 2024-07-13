package com.sagereal.srfactorymode;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;


import com.sagereal.srfactorymode.databinding.HomepageBinding;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private HomepageBinding binding;
    //请求权限
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private final String[] mPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE
    };

    //activity可见前的一些初始化操作
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        binding = HomepageBinding.inflate(getLayoutInflater());
        //将通过View Binding加载的布局根视图设置为Activity的内容视图,Activity就会显示homepage.xml布局文件中定义的内容。
        setContentView(binding.getRoot());
        init();
        //检查权限是否授权
        if (!checkPermissions()) {
            //请求权限
            ActivityCompat.requestPermissions(this, mPermissions, PERMISSIONS_REQUEST_CODE);
        }
        binding.photographMainBtn.setOnClickListener((View.OnClickListener) this);
        binding.callMainBtn.setOnClickListener((View.OnClickListener) this);
        binding.SingleTestMainBtn.setOnClickListener((View.OnClickListener) this);
        binding.TestReportMainBtn.setOnClickListener((View.OnClickListener) this);
    }


    public void init(){
        binding.deviceName.setText(getString(R.string.HomePageDeviceName)+""+ Build.DEVICE);
        binding.deviceType.setText(getString(R.string.HomePageDeviceModel) + " " + Build.MODEL);
        binding.systemVersion.setText(getString(R.string.HomePageSystemVersion) + " " + Build.DISPLAY);
        binding.androidVersion.setText(getString(R.string.HomePageAndroidVersion) + " " + Build.VERSION.RELEASE);
        getRam();
        getRom();
        binding.batterySize.setText(getString(R.string.HomePageBatteryCapacity) + " " + getBatteryCapacity() + " " + getString(R.string.mAh));
        binding.screenSize.setText(getString(R.string.HomePageScreenSize)+" "+getScreen()+" "+getString(R.string.inch));
        getScreenSolution();
    }


        // 检查是否有权限
        private boolean checkPermissions() {
            // 检查所有权限是否都已经被授权
            //每次循环迭代时，permission 会被设置为 mPermissions 数组中的下一个元素。
            for (String permission : mPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false; // 权限尚未授权，返回false
                }
            }
            return true; // 权限已经授权，返回true
        }


    // 处理权限请求的结果
    /*@NonNull 是一个注解（annotation），它用于指示一个字段、方法参数、方法返回值或方法本身不应该为null。
    如果检测到这样的值可能为null（例如，如果你在一个应该返回非空值的方法中返回了null），它会发出警告或错误*/
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == PERMISSIONS_REQUEST_CODE) {
                // 检查每一项权限是否都已授权
                boolean allPermissionsGranted = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                if (allPermissionsGranted) {
                    // 所有权限都已授权，继续你的操作
                } else {
                    //showPermissionDialog();
                }
            }
        }


        //权限申请对话框
  private void showPermissionDialog(){
            //使用AlertDialog.Builder类来创建一个新的对话框构建器对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.permission_tittle));
        builder.setMessage(getString(R.string.permission_message));
        //点击按钮，触发OnClickListener，转到应用的设置页面
        builder.setPositiveButton(getString(R.string.GoSet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",getPackageName(),null);
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


    public void getRam() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        binding.ram.setText(getString(R.string.HomePageRAM) + " " + (int) Math.ceil(memoryInfo.totalMem / 1024 / 1024 / (float) 1024) + " " + getString(R.string.G));
    }

   //设备的内部存储空间大小
    public void getRom() {
            //ContextCompat.getExternalFilesDirs() 方法来获取应用的外部文件目录
        File externalStorageDirectory = ContextCompat.getExternalFilesDirs(this, null)[0];
        long totalRom = externalStorageDirectory.getTotalSpace();
        long GB = 1024 * 1024 * 1024;  //定义了一个GB的单位（1GB等于102410241024字节）
        //一个可能的ROM大小数组
        final long[] deviceRomMemoryMap = {2 * GB, 4 * GB, 8 * GB, 16 * GB, 32 * GB, 64 * GB, 128 * GB, 256 * GB, 512 * GB, 1024 * GB, 2048 * GB};
        int[] displayRomSize = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048};
        //找到一个容量大于或等于 totalRom 的值。然后使用相应的 displayRomSize 值来设置显示的ROM大小。
        int i;
        for (i = 0; i < deviceRomMemoryMap.length; i++) {
            if (totalRom <= deviceRomMemoryMap[i]) {
                break;
            }
        }
        //使用 binding.rom.setText() 方法（这通常是在Data Binding中使用的）来更新UI上的ROM显示。
        binding.rom.setText(getString(R.string.HomePageROM) + " " + displayRomSize[i] + " " + getString(R.string.G));
    }


    public double getBatteryCapacity() {
        double batteryCapacity = 0;
        Class<?> powerProfileClass = null;

        try {
            // 获取PowerProfile类的Class对象
            powerProfileClass = Class.forName("com.android.internal.os.PowerProfile");

            // 假设这个方法在包含Context的某个类中调用，比如Activity或Service
            Context context = getApplicationContext(); // 或者直接this，如果当前类就是Context的子类

            // 获取PowerProfile的构造函数并实例化
            Constructor<?> constructor = powerProfileClass.getConstructor(Context.class);
            Object powerProfile = constructor.newInstance(context);

            // 获取getBatteryCapacity方法并调用
            Method getBatteryCapacityMethod = powerProfileClass.getMethod("getBatteryCapacity");
            batteryCapacity = (double) getBatteryCapacityMethod.invoke(powerProfile);
        } catch (ClassNotFoundException e) {
            // 处理类未找到的异常
            Log.e("BatterySize", "Class com.android.internal.os.PowerProfile not found", e);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            // 处理其他反射相关的异常
            Log.e("BatterySize", "Error accessing PowerProfile.getBatteryCapacity", e);
        }

        return batteryCapacity;
    }


    public double getScreen(){
            // 获取DisplayMetrics实例
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // 获取屏幕宽度和高度（像素）
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        //屏幕密度
        float xdpi = displayMetrics.xdpi;
        float ydpi = displayMetrics.ydpi;
        //物理宽度（英寸） = 像素宽度 / DPI
        //物理高度（英寸） = 像素高度 / DPI

        double screenSize = Math.sqrt(Math.pow(screenWidth / xdpi, 2) + Math.pow(screenHeight / ydpi, 2));
        return Math.round(screenSize * 100) / 100.0;
    }


    public void getScreenSolution() {
        // 获取WindowManager服务
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();

            // 使用DisplayMetrics来获取屏幕尺寸
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getRealMetrics(displayMetrics);

            int x = displayMetrics.widthPixels;
            int y = displayMetrics.heightPixels;

            binding.screenResolution.setText(getString(R.string.HomePageScreenResolution) + " " + x + "x" + y + " " +getString(R.string.pixel));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.photograph_main_btn) {
            photo();
        }
        if (v.getId() == R.id.call_main_btn) {
            call();
        }
        if (v.getId() == R.id.SingleTest_main_btn) {
            Intent intent = new Intent(v.getContext(), SingleTestActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.TestReport_main_btn) {
            Intent intent = new Intent(v.getContext(), TestReportActivity.class);
            startActivity(intent);
        }
    }

    private void photo(){
            //判断是否有权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限对话框
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
            showPermissionDialog();
        } else {
            // 已经权限，跳转到相机
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(cameraIntent);
        }
    }

    private void call() {
        //判断是否有权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限对话框
            showPermissionDialog();
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CODE);
        } else {
            // 已经权限，跳转到拨号
            Intent call112Intent = new Intent(Intent.ACTION_CALL);
            Uri uri = Uri.parse(getString(R.string.call_112));
            call112Intent.setData(uri);
            startActivity(call112Intent);
        }
    }
}