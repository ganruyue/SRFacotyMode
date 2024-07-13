package com.sagereal.srfactorymode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class KeyTestActivity extends AppCompatActivity {

    public static void openActivity(Context context, int position) {
        Intent intent = new Intent(context, KeyTestActivity.class);
        intent.getIntExtra(String.valueOf(position), 9);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_test);
        setTitle(R.string.KeyTest);
    }
}