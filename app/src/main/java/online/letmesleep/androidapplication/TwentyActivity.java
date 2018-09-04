package online.letmesleep.androidapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



import java.util.ArrayList;

import online.letmesleep.androidapplication.alivoice.SpeechTranscriberUtil;

public class TwentyActivity extends AppCompatActivity implements SpeechTranscriberUtil.VoiceCallback{

    boolean isListening = false;
    SpeechTranscriberUtil stu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_twenty);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("实时语音转写");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        stu = new SpeechTranscriberUtil(this,this);


        findViewById(R.id.activity_twenty_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isListening){
                    ((Button)findViewById(R.id.activity_twenty_start)).setText("开  始");
                    isListening = false;
                    stu.stopTranscriber();
                }else{
                    ((Button)findViewById(R.id.activity_twenty_start)).setText("结  束");
                    isListening = true;
                    stu.startTranscriber();
                }
            }
        });

        initPermission();


    }

    /**
     * 权限申请
     */
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
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


    StringBuffer sb = new StringBuffer();
    @Override
    public void onTemp(final String temp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.activity_twenty_result)).setText(sb.toString()+temp);
            }
        });
    }

    @Override
    public void onAppend(String message) {
        sb.append(message);
    }

    @Override
    public void onError(final String err) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TwentyActivity.this, ""+err, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
