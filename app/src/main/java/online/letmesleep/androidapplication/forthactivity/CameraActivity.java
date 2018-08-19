package online.letmesleep.androidapplication.forthactivity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.security.KeyStore;
import java.util.ArrayList;

import online.letmesleep.androidapplication.R;
import online.letmesleep.androidapplication.utils.DownloadUtil;
import online.letmesleep.androidapplication.utils.OpenFileByOtherApp;

public class CameraActivity extends AppCompatActivity implements CameraKitEventListener {

    CameraView cameraView;
    Button capVideo;
    Button capImage;
    PhotoView pic;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("图片与视频");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initPermission();

        cameraView = findViewById(R.id.camera);
        cameraView.addCameraKitListener(this);
        capVideo = findViewById(R.id.camera_activity_cap_video);
        capImage = findViewById(R.id.camera_activity_cap_image);
        pic = findViewById(R.id.camera_activity_cap_photoview);

        capImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        capVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    cameraView.captureVideo();
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    cameraView.stopVideo();
                }
                return true;
            }
        });

        pic.setZoomable(true);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pic.setVisibility(View.INVISIBLE);
                capImage.setVisibility(View.VISIBLE);
                capVideo.setVisibility(View.VISIBLE);
            }
        });

    }



    /**
     * 权限申请
     */
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.CAMERA,
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
    public void onEvent(CameraKitEvent cameraKitEvent) {

    }

    @Override
    public void onError(CameraKitError cameraKitError) {

    }

    @Override
    public void onImage(final CameraKitImage cameraKitImage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pic.setImageBitmap(cameraKitImage.getBitmap());
                pic.setVisibility(View.VISIBLE);
                capImage.setVisibility(View.INVISIBLE);
                capVideo.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onVideo(final CameraKitVideo cameraKitVideo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraActivity.this, "视频文件存放路径："+cameraKitVideo.getVideoFile().getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(pic.getVisibility()==View.VISIBLE){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pic.setVisibility(View.INVISIBLE);
                    capImage.setVisibility(View.VISIBLE);
                    capVideo.setVisibility(View.VISIBLE);
                }
            });
            return;
        }
        super.onBackPressed();
    }
}
