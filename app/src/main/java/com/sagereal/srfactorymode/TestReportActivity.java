package com.sagereal.srfactorymode;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sagereal.srfactorymode.databinding.TestreportBinding;
import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;

public class TestReportActivity extends AppCompatActivity implements View.OnClickListener {
    private TestreportBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testreport);
        setTitle(R.string.test_report_tittle);

        binding = TestreportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getData();
    }

    private void getData(){
        String[] singleTestName = {getString(R.string.BatteryTest), getString(R.string.VibrationTest), getString(R.string.MicrophoneTest),
                getString(R.string.HeadphoneTest), getString(R.string.LCDTest), getString(R.string.SpeakerTest), getString(R.string.ReceiverTest),
                getString(R.string.CameraTest), getString(R.string.FlashlightTest), getString(R.string.KeyTest)};

        for (int i = 0; i < EnumSingleTest.SINGLE_TEST_NUM.getValue(); i++) {
            int value = SharePreferenceUtil.getData(this, i, EnumSingleTest.UNTESTED.getValue());

            if (value == EnumSingleTest.TESTED_PASS.getValue()) {
                mBinding.tvPass.append(singleTestName[i] + "\n\n");
            } else if (value == EnumSingleTest.TESTED_FAIL.getValue()) {
                mBinding.tvFail.append(singleTestName[i] + "\n\n");
            } else if (value == EnumSingleTest.UNTESTED.getValue()) {
                mBinding.tvUntested.append(singleTestName[i] + "\n\n");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ib_back){
            finish();
        }
    }
}
