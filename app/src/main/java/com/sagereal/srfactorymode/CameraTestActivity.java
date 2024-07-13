package com.sagereal.srfactorymode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class CameraTestActivity extends AppCompatActivity {

    public static void openActivity(Context context, int position) {
        Intent intent = new Intent(context, CameraTestActivity.class);
        intent.getIntExtra(String.valueOf(position), 7);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        setTitle(R.string.CameraTest);
    }
}