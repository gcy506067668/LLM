package online.letmesleep.androidapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import online.letmesleep.androidapplication.voice.IFLYSpeaker;
import online.letmesleep.androidapplication.voice.IFLYVoiceReader;

public class ThirteenActivity extends AppCompatActivity implements IFLYVoiceReader.ReaderAnalListener {

    TextInputLayout textToVoice;
    TextInputLayout voiceToText;
    Button speak;
    Button listener;
    IFLYVoiceReader voiceReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirdteen);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("语音接口");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textToVoice = findViewById(R.id.eleven_activity_text_to_voice);
        voiceToText = findViewById(R.id.eleven_activity_voice_to_text);
        speak = findViewById(R.id.eleven_activity_speak);
        listener = findViewById(R.id.eleven_activity_totext_button);


        /**********init voice reader**********/
        voiceReader = new IFLYVoiceReader(this,this);


        /*****语音合成****/
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contont = textToVoice.getEditText().getText().toString();
                IFLYSpeaker.Speak(ThirteenActivity.this, contont, IFLYSpeaker.Speaker.EnglishOrChineseGirl);
            }
        });
        initPermission();


        /*****语音识别****/
        listener.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        voiceReader.beginListenering();
                        break;
                    case MotionEvent.ACTION_UP:
                        voiceReader.endListenering();
                        break;
                }
                return true;
            }
        });

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


    @Override
    public void onSuccess(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                voiceToText.getEditText().setText(result);
            }
        });
    }

    @Override
    public void onFalie(final String err) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ThirteenActivity.this, err, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
