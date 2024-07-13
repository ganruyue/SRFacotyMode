package com.sagereal.srfactorymode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SpeakerTestActivity extends AppCompatActivity {

    public static void openActivity(Context context, int position) {
        Intent intent = new Intent(context, SpeakerTestActivity.class);
        intent.getIntExtra(String.valueOf(position),5);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_test);
        setTitle(R.string.SpeakerTest);
    }
}