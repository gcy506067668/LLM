package online.letmesleep.androidapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import online.letmesleep.androidapplication.adapter.MainAdapter;
import online.letmesleep.androidapplication.forthactivity.CameraActivity;
import online.letmesleep.androidapplication.forthactivity.SMSSendActivity;
import online.letmesleep.androidapplication.forthactivity.VoiceActivity;
import online.letmesleep.androidapplication.forthactivity.WifiActivity;

public class ForteenActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener{

    @BindView(R.id.activity_main_recyclerview)
    RecyclerView recyclerView;
    MainAdapter adapter;
    List<String> datas;

    @Override
    public int setLayoutId() {
        return R.layout.activity_forth;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        datas = new ArrayList<>();
        adapter = new MainAdapter(datas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        datas.add("1. 图片拍照上传");
        datas.add("2. 视频摄像上传");
        datas.add("3. WIFI 判断");
        datas.add("4. 短信收发");
        datas.add("5. 语音播放");
        datas.add("6. 访问后台的接口（http  tcp/ip）");
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (position){
            case 0:
                startActivity(CameraActivity.class);
                break;
            case 1:
                startActivity(CameraActivity.class);
                break;
            case 2:
                startActivity(WifiActivity.class);
                break;
            case 3:
                startActivity(SMSSendActivity.class);
                break;
            case 4:
                startActivity(VoiceActivity.class);
                break;
            case 5:
                new AlertDialog.Builder(this)
                        .setTitle("网络请求")
                        .setMessage("http 协议get post download请求位于源码中/utils/DownloadUtil.java中,TCP/UDP请求示例位于/utils/TCPUDPDemo.java中，另外建议TCP/UDP配合开源的OKIO来使用，更加方便和安全。")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;

        }
    }
}
