package com.sagereal.srfactorymode;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sagereal.srfactorymode.databinding.SingletestBinding;
import com.sagereal.srfactorymode.databinding.TestreportBinding;
import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestReportActivity extends AppCompatActivity{
    private final int[] mResourcesId = {
            R.string.BatteryTest,
            R.string.VibrationTest,
            R.string.MicrophoneTest,
            R.string.HeadphoneTest,
            R.string.LCDTest,
            R.string.SpeakerTest,
            R.string.ReceiverTest,
            R.string.CameraTest,
            R.string.KeyTest,
            R.string.FlashlightTest,
    };

    private final List<Map<String, String>> mListItem0 = new ArrayList<>();

    private final List<Map<String, String>> mListItem1 = new ArrayList<>();

    private final List<Map<String, String>> mListItem2 = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testreport);
        setTitle(R.string.TestReport);
        ListView PassList = findViewById(R.id.pass_list);
        ListView FailList = findViewById(R.id.fail_list);
        ListView UntestedList = findViewById(R.id.untested_list);
        initData();
        /**
         * 为PassList这个ListView设置适配器，适配器使用this作为上下文，
         * mListItem0作为数据源，R.layout.list_item作为布局文件
         * new String[]{getString(R.string.name)}指定数据源中的哪个字段（
         * 这里是资源字符串R.string.name对应的值）用于显示**/
        PassList.setAdapter(new SimpleAdapter(this, mListItem0, R.layout.recycler_item,
                new String[]{getString(R.string.name)},
                new int[]{R.id.test_rv}));

        FailList.setAdapter(new SimpleAdapter(this, mListItem1, R.layout.recycler_item,
                new String[]{getString(R.string.name)},
                new int[]{R.id.test_rv}));

        UntestedList.setAdapter(new SimpleAdapter(this, mListItem2, R.layout.recycler_item,
                new String[]{getString(R.string.name)},
                new int[]{R.id.test_rv}));
    }

    //初始化数据
    private void initData() {

        for (int i = 0; i < mResourcesId.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put(getString(R.string.name), getString(mResourcesId[i]));
            int state = SharePreferenceUtils.getResult(this, i, -1);
            switch (state) {
                case 1:
                    mListItem0.add(map);
                    break;
                case 0:
                    mListItem1.add(map);
                    break;
                case -1:
                    mListItem2.add(map);
                    break;
            }
        }
    }
}
