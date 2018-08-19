package online.letmesleep.androidapplication.forthactivity;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import online.letmesleep.androidapplication.R;

public class SMSSendActivity extends AppCompatActivity {


    Button send;
    TextInputLayout contont;
    TextInputLayout contacts;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smssend);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("启动APP");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        send = findViewById(R.id.activity_sendsms_button);
        contont = findViewById(R.id.activity_sms_contont);
        contacts = findViewById(R.id.activity_sms_contacts);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = contont.getEditText().getText().toString();
                String contacts = SMSSendActivity.this.contacts.getEditText().getText().toString();
                if(!contacts.equals("")){
                    if(contacts.split(",").length==0)
                        SMSUtil.sendMessage(message,new String[]{contacts});
                    else
                        SMSUtil.sendMessage(message,contacts.split(","));

                }

            }
        });

        initPermission();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_DELIVER");
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        smsReceiver = new SmsReceiver();
        registerReceiver(smsReceiver,filter);
    }

    SmsReceiver smsReceiver;


    @Override
    protected void onDestroy() {
        unregisterReceiver(smsReceiver);
        super.onDestroy();
    }

    /**
     * 权限申请
     */
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS
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
