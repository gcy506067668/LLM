package online.letmesleep.androidapplication;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import online.letmesleep.androidapplication.QRCode.QRCodeScannerActivity;
import online.letmesleep.androidapplication.adapter.MainAdapter;

import online.letmesleep.androidapplication.facedetect.FaceDetectActivity;

public class MainActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener{

    @BindView(R.id.activity_main_recyclerview)
    RecyclerView recyclerView;
    MainAdapter adapter;
    List<String> datas;

    @Override
    public int setLayoutId() {
        return R.layout.activity_main;
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
        datas.add("1. 启动另一个APP,如果不存在，下载安装后启动");
        datas.add("2. 在app中用webview 组件加载页面，嵌入页面的脚本可以启动特定的APP");
        datas.add("3. 页面（表单）间的流畅（炫酷）切换（页面回退时可选择释放或保持,能相应左右滑屏和上下滑屏动作");
        datas.add("4. 语音对讲机---未实现");
        datas.add("5. 语音（电话）会议");
        datas.add("6. 视频直播发送端");
        datas.add("7. 视频直播浏览端");
        datas.add("8. 文字聊天---未实现");
        datas.add("9. 浏览器的调用");
        datas.add("10. 开发一个手机浏览器---未实现");
        datas.add("11. 动态创建组件");
        datas.add("12. 打开关联文件");
        datas.add("13. 语音驱动接口");
        datas.add("14. 相关接口");
        datas.add("15. 标准表单");
        datas.add("16. 二维码");
        datas.add("17. 百度人脸识别");
        datas.add("18. 保持屏幕常亮");
        datas.add("19. 在app中用webview 组件加载页面，嵌入页面的脚本可以调用app的原生函数");
        datas.add("20. 实时语音转写");
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (position){
            case 0:
                startActivity(FirstActivity.class);
                break;
            case 1:
                startActivity(SecondActivity.class);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                startActivity(FivthActivity.class);
                break;
            case 5:
                startActivity(SixthActivity.class);
                break;
            case 6:
                startActivity(SeventhActivity.class);
                break;
            case 7:
                break;
            case 8:
                startActivity(NineActivity.class);
                break;
            case 9:
                break;
            case 10:
                startActivity(ElevenActivity.class);
                break;
            case 11:
                startActivity(TwelveActivity.class);
                break;
            case 12:
                startActivity(ThirteenActivity.class);
                break;
            case 13:
                startActivity(ForteenActivity.class);
                break;
            case 14:
                startActivity(FivteenActivity.class);
                break;
            case 15:
                startActivity(QRCodeScannerActivity.class);   //二维码
                break;
            case 16:
                startActivity(FaceDetectActivity.class);
                break;
            case 17:
                startActivity(EighteenActivity.class);
                break;
            case 18:
                startActivity(NineteenActivity.class);
                break;
            case 19:
                startActivity(TwentyActivity.class);
                break;
        }
    }
}
