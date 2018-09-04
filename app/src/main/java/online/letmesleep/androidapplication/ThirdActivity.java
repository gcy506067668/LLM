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

import online.letmesleep.androidapplication.utils.DownloadUtil;
import online.letmesleep.androidapplication.webview.OpenOtherAppWebview;

// TODO: 2018/8/22  浏览器多窗口模式未完成
public class ThirdActivity extends AppCompatActivity {


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

    /***
     *  下载apk文件
     */
    private void downLoadApk() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadUtil.get().download(downloadUrl, "download", new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                        installApk(file);
                    }

                    @Override
                    public void onDownloading(final int progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }

                    @Override
                    public void onDownloadFailed() {

                    }
                });
            }
        }).start();
    }


    /****
     *   安装APK文件
     * @param file   apk文件存放路径
     */
    private void installApk(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Uri contentUri = FileProvider.getUriForFile(this, "online.letmesleep.androidapplication.fileProvider", file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        intent.addCategory("android.intent.category.DEFAULT");
        startActivity(intent);
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


    /**
     * 检查包是否存在
     *
     * @param packname
     * @return
     */
    private boolean checkPackInfo(String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }



}
