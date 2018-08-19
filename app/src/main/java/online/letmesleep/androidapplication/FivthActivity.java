package online.letmesleep.androidapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;



import org.litepal.LitePal;
import org.litepal.util.Const;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import online.letmesleep.androidapplication.adapter.PhoneContactAdapter;
import online.letmesleep.androidapplication.bean.Contacts;
import online.letmesleep.androidapplication.phoneconference.Config;
import online.letmesleep.androidapplication.utils.DownloadUtil;
import online.letmesleep.androidapplication.utils.ProgressBarUtil;
import online.letmesleep.androidapplication.webview.OpenOtherAppWebview;

import static android.content.Intent.ACTION_MAIN;

public class FivthActivity extends AppCompatActivity {


    String downloadUrl = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
    String schemeUrl = "schemename://online.letmesleep.androidapplicationopena/path?id=1&name=test";


    TextInputLayout secondActivityUrl;
    LinearLayout loginLayout;
    AlertDialog dialog;
    PhoneContactAdapter adapter;
    RecyclerView recyclerView;
    List<Contacts> datas;
    Handler handler;
    //ServiceWaitThread mThread = new ServiceWaitThread();
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("语音电话会议");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        secondActivityUrl = findViewById(R.id.fivth_activity_account);
        dialog = ProgressBarUtil.getProgress(this);
        recyclerView = findViewById(R.id.fivth_activity_recyclerview);
        loginLayout = findViewById(R.id.fivth_activity_login_layout);
        //startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
        handler = new Handler();
        // 开启线程登录
        dialog.show();
        //mThread.start();
        findViewById(R.id.first_activity_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                name = secondActivityUrl.getEditText().getText().toString();
                //syncAccount(name, Config.password, Config.domain);
            }
        });

        initAdapter();

        initPermission();



    }

    private void initAdapter() {
        datas = new ArrayList<>();
        adapter = new PhoneContactAdapter(datas);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //callOutgoing(datas.get(position).getNumber(),datas.get(position).getName());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        DownloadUtil.get().HttpGet(Config.SERVER_URL, new DownloadUtil.MCallback() {
            @Override
            public void onSuccess(String resp) {
                datas.clear();
                datas.addAll(DownloadUtil.getContactsArrayFromJsonString(resp));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFaild(final String err) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FivthActivity.this, ""+err, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

//    private void syncAccount(String username, String password, String domain) {
//
//        LinphonePreferences mPrefs = LinphonePreferences.instance();
//        if (mPrefs.isFirstLaunch()) {
//            mPrefs.setAutomaticallyAcceptVideoRequests(true);
////            mPrefs.setInitiateVideoCall(true);
//            mPrefs.enableVideo(true);
//        }
//        int nbAccounts = mPrefs.getAccountCount();
//        if (nbAccounts > 0) {
//            String nbUsername = mPrefs.getAccountUsername(0);
//            if (nbUsername != null && !nbUsername.equals(username)) {
//                mPrefs.deleteAccount(0);
//                saveNewAccount(username, password, domain);
//            }
//        } else {
//            saveNewAccount(username, password, domain);
//            mPrefs.firstLaunchSuccessful();
//
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                loginLayout.setVisibility(View.INVISIBLE);
//                dialog.dismiss();
//            }
//        });
//    }
//
//    private void saveNewAccount(String username, String password, String domain) {
//        LinphonePreferences.AccountBuilder builder = new LinphonePreferences.AccountBuilder(LinphoneManager.getLc())
//                .setUsername(username)
//                .setDomain(domain)
//                .setPassword(password)
//                .setDisplayName(username)
//                .setTransport(LinphoneAddress.TransportType.LinphoneTransportTcp);
//
//        try {
//            builder.saveNewAccount();
//        } catch (LinphoneCoreException e) {
//
//        }
//    }
//
//    private class ServiceWaitThread extends Thread {
//        public void run() {
//            while (!LinphoneService.isReady()) {
//                try {
//                    sleep(30);
//                    Log.e("seriver is ","starting");
//                } catch (InterruptedException e) {
//                    throw new RuntimeException("waiting thread sleep() has been interrupted");
//                }
//            }
//
//            Log.e("seriver is ","start success");
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    dialog.dismiss();
//                }
//            });
//            mThread = null;
//        }
//    }
//
//    private void callOutgoing(String number,String name) {
//        try {
//
//            if (!LinphoneManager.getInstance().acceptCallIfIncomingPending()) {
//                String to = String.format("sip:%s@%s", number, Config.domain);
//                LinphoneManager.getInstance().newOutgoingCall(to, name);
//                startActivity(new Intent()
//                        .setClass(this, LinphoneActivity.class));
//            }
//        } catch (LinphoneCoreException e) {
//
//            LinphoneManager.getInstance().terminateCall();
//
//        }
//    }
//
//



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
