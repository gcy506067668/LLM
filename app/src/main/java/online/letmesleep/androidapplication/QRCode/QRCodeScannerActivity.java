package online.letmesleep.androidapplication.QRCode;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.letmesleep.androidapplication.R;
import online.letmesleep.androidapplication.utils.ImageUtil;



public class QRCodeScannerActivity extends AppCompatActivity {


    private boolean light = false;
    private int REQUEST_IMAGE = 109;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.activity_qrcode_scanner);
        ButterKnife.bind(this);
        /**
         * 执行扫面Fragment的初始化操作
         */
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);

        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();

        initPermission();
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

    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, final String result) {
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   Toast.makeText(QRCodeScannerActivity.this, ""+result, Toast.LENGTH_SHORT).show();
               }
           });
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            QRCodeScannerActivity.this.setResult(RESULT_OK, resultIntent);
            QRCodeScannerActivity.this.finish();
        }
    };

    @OnClick({R.id.add_application_by_text, R.id.button_exit, R.id.button_album, R.id.button_openlight, R.id.button_create_qrcode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_application_by_text:
                startActivity(new Intent(this,CreateQRCodeActivity.class));
                break;
            case R.id.button_exit:
                finish();
                break;
            case R.id.button_album:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
                break;
            case R.id.button_openlight:
                if (light) {
                    light = false;
                    CodeUtils.isLightEnable(false);
                } else {
                    light = true;
                    CodeUtils.isLightEnable(true);
                }
                break;
            case R.id.button_create_qrcode:
                startActivity(new Intent(this,CreateQRCodeActivity.class));
                break;

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                final Uri uri = data.getData();
                //ContentResolver cr = getContentResolver();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(QRCodeScannerActivity.this,uri), new CodeUtils.AnalyzeCallback() {
                                @Override
                                public void onAnalyzeSuccess(Bitmap mBitmap, final String result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(QRCodeScannerActivity.this, ""+result, Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }

                                @Override
                                public void onAnalyzeFailed() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(QRCodeScannerActivity.this, "解析错误", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }
}
