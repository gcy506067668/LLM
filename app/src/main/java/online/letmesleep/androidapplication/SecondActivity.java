package online.letmesleep.androidapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.letmesleep.androidapplication.utils.DownloadUtil;
import online.letmesleep.androidapplication.webview.OpenOtherAppWebview;

/*******
 *
 *  更具webview指定URL打开某个特定的APP，被打开的APP应具有特殊接口
 *  请参考伴随程序AndroidApplicationOpenA源码
 *
 */
public class SecondActivity extends AppCompatActivity {


    String downloadUrl = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
    String schemeUrl = "schemename://online.letmesleep.androidapplicationopena/path?id=1&name=test";


    TextInputLayout secondActivityUrl;
    OpenOtherAppWebview myWebview;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Webview启动APP");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        secondActivityUrl = findViewById(R.id.second_activity_url);
        myWebview = findViewById(R.id.my_webview);
        textView = findViewById(R.id.second_activity_tips);
        textView.setText("HTML打开APP示例代码：  \n<html lang=\"zh-CN\">\n" +
                "<a href = \"schemename://online.letmesleep.androidapplicationopena/path?id=1&name=test\">打开测试APP</a>\n" +
                "</html>");

        findViewById(R.id.first_activity_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setVisibility(View.GONE);
                myWebview.loadUrl(secondActivityUrl.getEditText().getText().toString());

            }
        });
        initPermission();

    }



    /**
     * 权限申请
     */
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }





}
