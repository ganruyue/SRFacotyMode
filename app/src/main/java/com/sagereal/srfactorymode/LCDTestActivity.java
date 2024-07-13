package com.sagereal.srfactorymode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;


import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;
import com.sagereal.srfactorymode.databinding.ActivityLcdtestBinding;

public class LCDTestActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityLcdtestBinding binding;
    private int count = 0;
    private int position = 4;
    int[] color = {R.color.red, R.color.green, R.color.gray, R.color.white};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLcdtestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(R.string.LCDTest);
        binding.btnStartLcd.setOnClickListener(this);
        binding.pass.setOnClickListener(this);
        binding.fail.setOnClickListener(this);
        binding.lcd.setOnClickListener(this);
        binding.LCDT.setVisibility(View.INVISIBLE);
        binding.pass.setVisibility(View.INVISIBLE);
        binding.fail.setVisibility(View.INVISIBLE);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_lcd) {
            binding.LCDT.setVisibility(View.VISIBLE);
            binding.btnStartLcd.setVisibility(View.GONE);
            binding.lcd.setBackgroundColor(getResources().getColor(color[count % 4]));
            count++;
        }
        else if (v.getId() == R.id.lcd && binding.btnStartLcd.getVisibility() == View.GONE && count > 0) {
            if (count == 3) {
                binding.pass.setVisibility(View.VISIBLE);
                binding.fail.setVisibility(View.VISIBLE);
            }
            binding.lcd.setBackgroundColor(getResources().getColor(color[count % 4]));
            count++;
            if (count > 3) {
                binding.lcd.setClickable(false);
            }
        }
        else if (v.getId() == R.id.pass) {
            SharePreferenceUtils.save(v.getContext(), position, 1);
            Intent intent = new Intent(getApplicationContext(), SingleTestActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if (v.getId() == R.id.fail) {
            SharePreferenceUtils.save(v.getContext(), position, 0);
            Intent intent = new Intent(getApplicationContext(), SingleTestActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, LCDTestActivity.class));
    }
}