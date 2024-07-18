package com.sagereal.srfactorymode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sagereal.srfactorymode.databinding.SingletestBinding;
import com.sagereal.srfactorymode.Utils.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SingleTestActivity extends AppCompatActivity {

    private SingletestBinding binding;
    private SingleTestAdapter adapter;
    private List<String> dataList;
    private List<Integer> statusList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //保持屏幕常亮不休眠

        //binding绑定布局
        binding = SingletestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 获取RecyclerView并设置LayoutManager和Adapter

        // 设置LayoutManager
        binding.testRv.setLayoutManager(new LinearLayoutManager(this));

        setTitle(getString(R.string.SingleTest));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SingleTestAdapter singleTestItemAdapter = new SingleTestAdapter(init_d());
        // 创建并设置Adapter
        binding.testRv.setAdapter(new SingleTestAdapter(init_d()));
    }

    public List<String> init_d() {
        // 只在 dataList 为 null 时初始化它，以避免重复初始化
        if (dataList == null) {
            dataList = new ArrayList<>();
            dataList.add(getString(R.string.BatteryTest));
            dataList.add(getString(R.string.VibrationTest));
            dataList.add(getString(R.string.MicrophoneTest));
            dataList.add(getString(R.string.HeadphoneTest));
            dataList.add(getString(R.string.LCDTest));
            dataList.add(getString(R.string.SpeakerTest));
            dataList.add(getString(R.string.ReceiverTest));
            dataList.add(getString(R.string.CameraTest));
            dataList.add(getString(R.string.FlashlightTest));
            dataList.add(getString(R.string.KeyTest));
        }
        return dataList;
    }

   // Adapter类
    private class SingleTestAdapter extends RecyclerView.Adapter<SingleTestAdapter.ViewHolder> {
       // 存放数据的列表
       private List<String> mData;
       // 上下文对象，用于访问资源等
       private Context context;

       public SingleTestAdapter(List<String> data) {
        this.mData = data;
        this.context= context;
    }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 加载布局并创建ViewHolder
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
            return new ViewHolder(view);
        }

       @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String item = mData.get(position);
            holder.bind(item);
            //holder.textView.setText(item);
            //1红2绿
           //SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences(holder.itemView.getResources().getString(R.string.sr_factory_mode), Context.MODE_PRIVATE);
           int value = SharePreferenceUtils.getResult(SingleTestActivity.this, position, -1);
           if (value == 1) {
               holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.green));
           } else if (value == 0) {
               holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.red));
           }
        }


        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.test_rv); // 确保你有正确的ID
                itemView.setOnClickListener(this); // 为整个itemView设置点击监听器
            }
            public void bind(String item){
                textView.setText(item);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition(); // 获取当前ViewHolder的位置
                if (position != RecyclerView.NO_POSITION) { // 检查位置是否有效
                    switch (position) {
                        case 0:
                            BatteryTestActivity.openActivity(itemView.getContext());
                            break;
                        case 1:
                            VibrationTestActivity.openActivity(itemView.getContext());
                            break;
                        case 2:
                            MicrophoneTestActivity.openActivity(itemView.getContext());
                            break;
                        case 3:
                            HeadphoneTestActivity.openActivity(itemView.getContext());
                            break;
                        case 4:
                            LCDTestActivity.openActivity(itemView.getContext());
                            break;
                        case 5:
                            SpeakerTestActivity.openActivity(itemView.getContext());
                            break;
                        case 6:
                            ReceiverTestActivity.openActivity(itemView.getContext());
                            break;
                        case 7:
                            CameraTestActivity.openActivity(itemView.getContext());
                            break;
                        case 8:
                            FlashTestActivity.openActivity(itemView.getContext());
                            break;
                        case 9:
                            KeyTestActivity.openActivity(itemView.getContext());
                            break;
                    }
                }
            }
        }
    }
}
