package online.letmesleep.androidapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import online.letmesleep.androidapplication.utils.DownloadUtil;

/****
 * @author Letmesleep
 * 通过APP包名来判断该APP是否已经安装，如果安装则启动APP 否则根据下载地址自动下载并安装APP
 */
public class FirstActivity extends AppCompatActivity {


    TextInputLayout til;
    TextView downloadTip;
    ProgressBar downpercent;
    LinearLayout downloadLayout;

    /***           下载地址          此处以腾讯QQ为例    ***/
    String downloadUrl = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("启动APP");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        til = findViewById(R.id.first_activity_packagename);
        downloadTip = findViewById(R.id.first_activity_text_download);
        downpercent = findViewById(R.id.first_activity_progressbar);
        downloadLayout = findViewById(R.id.first_activity_download_layout);


        findViewById(R.id.first_activity_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = getPackageManager();
                if (checkPackInfo(til.getEditText().getText().toString())) {
                    Intent intent = packageManager.getLaunchIntentForPackage(til.getEditText().getText().toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(FirstActivity.this, "应用未安装,下载中...", Toast.LENGTH_SHORT).show();
                    downloadLayout.setVisibility(View.VISIBLE);
                    downLoadApk();
                }
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
                                downloadTip.setText("下载完成，正在安装...");
                            }
                        });
                        installApk(file);
                        }

                    @Override
                    public void onDownloading(final int progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downpercent.setProgress(progress);
                                downloadTip.setText("下载中..."+progress+"%");
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
    private void installApk(File file){
        Intent intent=new Intent("android.intent.action.VIEW");
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {

            Uri contentUri = FileProvider.getUriForFile(this,"online.letmesleep.androidapplication.fileProvider",file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
        }else{
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
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
