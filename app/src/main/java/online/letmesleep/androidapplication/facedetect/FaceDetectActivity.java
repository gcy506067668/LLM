package online.letmesleep.androidapplication.facedetect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import online.letmesleep.androidapplication.R;
import online.letmesleep.androidapplication.facedetect.facebean.FaceBean;
import online.letmesleep.androidapplication.utils.ImageUtil;
import online.letmesleep.androidapplication.utils.ProgressBarUtil;

public class FaceDetectActivity extends AppCompatActivity implements View.OnClickListener{


    LinearLayout addLayer;
    LinearLayout recLayer;
    LinearLayout delLayer;
    String imagePicPath = null;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facedetect);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("人脸识别");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = ProgressBarUtil.getProgress(this);

        findViewById(R.id.face_recognize_addface).setOnClickListener(this);
        findViewById(R.id.face_recognize_addrecognize).setOnClickListener(this);
        findViewById(R.id.face_recognize_deleteface).setOnClickListener(this);
        findViewById(R.id.face_recognize_addface_addbutton).setOnClickListener(this);
        findViewById(R.id.face_recognize_deleteface_deletebutton).setOnClickListener(this);
        findViewById(R.id.face_recognize_choosepic).setOnClickListener(this);
        findViewById(R.id.face_recognize_chooserecognizepic).setOnClickListener(this);

        addLayer = findViewById(R.id.face_recognize_addface_layer);
        recLayer = findViewById(R.id.face_recognize_recognize_layer);
        delLayer = findViewById(R.id.face_recognize_deleteface_layer);
        initPermission();
    }

    String action = null;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.face_recognize_addface:      //注册
                addLayer.setVisibility(View.VISIBLE);
                recLayer.setVisibility(View.INVISIBLE);
                delLayer.setVisibility(View.INVISIBLE);
                break;
            case R.id.face_recognize_addrecognize:
                addLayer.setVisibility(View.INVISIBLE);
                recLayer.setVisibility(View.VISIBLE);
                delLayer.setVisibility(View.INVISIBLE);
                break;
            case R.id.face_recognize_deleteface:
                addLayer.setVisibility(View.INVISIBLE);
                recLayer.setVisibility(View.INVISIBLE);
                delLayer.setVisibility(View.VISIBLE);
                break;
            case R.id.face_recognize_addface_addbutton:
                if(imagePicPath==null){
                    Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                String username = ((TextInputLayout)findViewById(R.id.face_recognize_addface_name)).getEditText().getText().toString();
                final FaceBean faceBean = new FaceBean();
                faceBean.setUsername(username);
                faceBean.setGroup_id("letmesleep1");
                int count = LitePal.count(FaceBean.class);
                faceBean.setUser_id("user"+count);
                dialog.show();

                FaceRecognizeOnline.getInstance().addFace(imagePicPath, faceBean.getGroup_id(), faceBean.getUser_id(), new FaceRecognizeOnline.FaceCallback() {
                    @Override
                    public void onSuccess(String message) {
                        faceBean.setFace_token(message);
                        faceBean.save();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(FaceDetectActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(final String err) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(FaceDetectActivity.this, "注册失败:"+err, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


                break;

            case R.id.face_recognize_deleteface_deletebutton:
                String deleteusername = ((TextInputLayout)findViewById(R.id.face_recognize_delete_username)).getEditText().getText().toString();
                List <FaceBean> fbs = LitePal.where("username=?",deleteusername).find(FaceBean.class);
                if(fbs.size()!=0){
                    FaceBean fb = fbs.get(0);
                    dialog.show();
                    FaceRecognizeOnline.getInstance().deleteFace(fb.getGroup_id(), fb.getUser_id(), fb.getFace_token(), new FaceRecognizeOnline.FaceCallback() {
                        @Override
                        public void onSuccess(final String message) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(FaceDetectActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(String err) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(FaceDetectActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    fb.delete();
                }
                break;
            case R.id.face_recognize_choosepic:
                action = "addface";
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(FaceDetectActivity.this);
                break;
            case R.id.face_recognize_chooserecognizepic:
                action = "recognize";
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(FaceDetectActivity.this);
                break;
        }
    }


    /**
     * 权限申请
     */
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imagePicPath = ImageUtil.getImageAbsolutePath(this, resultUri);
                Glide.with(this)
                        .load(imagePicPath)
                        .into((ImageView) findViewById(R.id.face_recognize_imageview));

                if("recognize".equals(action)){
                    dialog.show();
                    FaceRecognizeOnline.getInstance().mutiSearchFace(imagePicPath, "letmesleep1", new FaceRecognizeOnline.FaceCallback() {
                        @Override
                        public void onSuccess(final String message) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    ((TextView)findViewById(R.id.face_recognize_result)).setText(message);
                                }
                            });
                        }

                        @Override
                        public void onError(final String err) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(FaceDetectActivity.this, ""+err, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
