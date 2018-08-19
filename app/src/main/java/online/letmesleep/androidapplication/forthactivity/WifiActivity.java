package online.letmesleep.androidapplication.forthactivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import online.letmesleep.androidapplication.R;
import online.letmesleep.androidapplication.utils.DownloadUtil;

public class WifiActivity extends AppCompatActivity {


    TextView wifiStatus;

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("启动APP");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wifiStatus = findViewById(R.id.wifi_statue);


        wifiStatus.setText(getWifiStatus());
        mHandler = new Handler();
        mHandler.postDelayed(runnable,1000);
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String nettype = "";
            int netcode = getNetType(WifiActivity.this);
            if(netcode==0){
                nettype = "移动网络";
            }else if(netcode==-1){
                nettype = "无网络";
            }else if(netcode==1){
                nettype = "WIFI网络";
            }
            wifiStatus.setText(nettype);
            mHandler.postDelayed(runnable,1000);
        }
    };






    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }



    /****
     *  获取手机WLAN状态
     * @return
     */
    private String getWifiStatus() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            int wifiState = wifiManager.getWifiState();
            switch(wifiState){
                case WifiManager.WIFI_STATE_DISABLED:
                    return "WLAN已关闭";
                case WifiManager.WIFI_STATE_ENABLED:
                    return "WLAN已打开";
                case WifiManager.WIFI_STATE_ENABLING:
                    return "WLAN打开中";
                case WifiManager.WIFI_STATE_DISABLING:
                    return "WLAN关闭中";
            }

        }
        return "为获取权限";
    }






    /***
     *          打开或者关闭wifi  true打开   false关闭
     *
     * @param state
     */
    public void setWifiEnable(boolean state){
        //首先，用Context通过getSystemService获取wifimanager
        WifiManager mWifiManager = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiManager.setWifiEnabled(state);
    }






    /**
     * 获取手机网络状态
     * -1  代表无网络
     * 0   代表蜂窝网络
     * 1    代表WiFi
     * */
    public int getNetType(Context context){

        int mState= -1;//代表无网络

        //获取安卓系统提供的服务，转换成链接管理类，这个类专门处理链接相关的东西
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo封装了网络链接信息
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo==null){
            return mState;//代表无网络
        }

        int type = activeNetworkInfo.getType();
        if (type==ConnectivityManager.TYPE_WIFI){
            mState=1;
        }else if (type==ConnectivityManager.TYPE_MOBILE){
            mState=0;
        }

        return mState;
    }

}
