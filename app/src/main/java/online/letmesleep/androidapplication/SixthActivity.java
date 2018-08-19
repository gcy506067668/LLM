package online.letmesleep.androidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import online.letmesleep.androidapplication.rtmp.RtmpSenderActivity;

public class SixthActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sixth);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("直播推流端");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.sixth_activity_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rtmpAddress = ((TextInputLayout)findViewById(R.id.sixth_activity_tympaddress)).getEditText().getText().toString();
                Intent intent = new Intent(SixthActivity.this, RtmpSenderActivity.class);
                intent.putExtra("address",rtmpAddress);
                startActivity(intent);
            }
        });



    }





    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }



}
