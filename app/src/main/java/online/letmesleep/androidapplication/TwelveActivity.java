package online.letmesleep.androidapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import java.io.File;
import java.util.List;

import online.letmesleep.androidapplication.utils.OpenFileByOtherApp;

public class TwelveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twelve);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("打开关联文件");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        findViewById(R.id.twelve_activity_choosefile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new LFilePicker()
                        .withActivity(TwelveActivity.this)
                        .withRequestCode(101)
                        .withTitle("选择文件")
                        .withMutilyMode(false)
                        .withIconStyle(Constant.ICON_STYLE_BLUE)
                        .start();

            }
        });


        findViewById(R.id.nine_activity_openurl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextView) findViewById(R.id.twelve_activity_url)).getText().toString().equals("")) {
                    return;
                }

                OpenFileByOtherApp.getInstance().openFile(TwelveActivity.this,new File(((TextView) findViewById(R.id.twelve_activity_url)).getText().toString()));
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                final List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);

                if (list.size() == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) findViewById(R.id.twelve_activity_url)).setText(list.get(0));
                        }
                    });
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
