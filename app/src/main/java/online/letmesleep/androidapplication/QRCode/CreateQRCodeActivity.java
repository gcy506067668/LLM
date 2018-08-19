package online.letmesleep.androidapplication.QRCode;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import online.letmesleep.androidapplication.R;


public class CreateQRCodeActivity extends AppCompatActivity {

    @BindView(R.id.create_qrcode_edit_application_name)
    EditText createQrcodeEditApplicationName;
    @BindView(R.id.create_qrcode_imageview)
    ImageView createQrcodeImageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.activity_create_qrcode);
        ButterKnife.bind(this);
        //getSupportActionBar().hide();
    }

    @OnClick(R.id.create_qrcode_edit_application_create_button)
    public void onViewClicked() {

        createQrcodeImageview.setImageBitmap(CodeUtils.createImage(createQrcodeEditApplicationName.getText().toString(),
                400,
                400,
                BitmapFactory.decodeResource(getResources(),  R.drawable.ic_launcher_background)));
    }
}
