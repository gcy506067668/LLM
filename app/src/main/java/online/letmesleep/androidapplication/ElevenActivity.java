package online.letmesleep.androidapplication;

import android.Manifest;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import online.letmesleep.androidapplication.utils.DownloadUtil;
import online.letmesleep.androidapplication.voice.IFLYSpeaker;
import online.letmesleep.androidapplication.voice.IFLYVoiceReader;
import online.letmesleep.androidapplication.webview.OpenOtherAppWebview;

public class ElevenActivity extends AppCompatActivity implements View.OnClickListener{

    Button createA;
    Button createB;
    Button destoryA;
    Button destoryB;

    LinearLayout groupA;
    LinearLayout groupB;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eleven);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("动态创建组件");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        createA = findViewById(R.id.eleven_activity_create_a);
        createB = findViewById(R.id.eleven_activity_create_b);
        destoryA = findViewById(R.id.eleven_activity_destory_a);
        destoryB = findViewById(R.id.eleven_activity_destory_b);
        groupA = findViewById(R.id.activity_eleven_group_a);
        groupB = findViewById(R.id.activity_eleven_group_b);

        createA.setOnClickListener(this);
        createB.setOnClickListener(this);
        destoryA.setOnClickListener(this);
        destoryB.setOnClickListener(this);



    }

    List<ViewGroup> parents = new ArrayList<>();
    List<View> views = new ArrayList<>();

    /****
     *
     *      根据父布局的tag 和组件tag集合批量修改组件内容
     *
     * @param tags    批量修改或者创建的组件 tag集
     * @param parent    组件的父容器的Tag
     */
    public void createViewByTags(List<String> tags,String parent){
        ViewGroup parentView = null;
        boolean isExist = false;
        for (ViewGroup view:parents) {
            if(parent.equals(view.getTag())){    //根据Tag从父布局列表中获取要创建布局的父布局
                parentView = view;
                isExist = true;
            }
        }
        if(!isExist){
            parentView = createParentViewByTag(parent);             //如果父布局不存在则创建父布局
        }

        for (int i = 0; i < tags.size(); i++) {
            for (int j = 0; j < views.size(); j++) {
                if(tags.get(i).equals(views.get(i).getTag())){     //此处表示当前tag组件存在   并且可以根据tag进行操作

                    View operatorView = views.get(i); // TODO: 2018/8/13     根据自己的需要写逻辑
                }else{                                              //此处表示前tag组件不存在 先创建 后操作
                    View view = new View(this);
                    view.setTag(tags.get(i));
                    ((LinearLayout)parentView).addView(view);
                    views.add(view);
                    // TODO: 2018/8/13   根据自己的需要写逻辑
                }
            }

        }
    }


    /****
     *              创建父布局容器
     *
     * @param tag   父布局tag
     * @return
     */
    public ViewGroup createParentViewByTag(String tag){
        ViewGroup parent = new LinearLayout(this);    //线性布局容器
        parent.setTag(tag);
        parents.add(parent);
        return parent;
    }





    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }






    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.eleven_activity_create_a:
                for (int i = 0; i < 5; i++) {
                    TextView textView = new TextView(this);
                    textView.setText("A列 "+i);
                    textView.setTag("a_"+i);
                    groupA.addView(textView);
                }

                break;
            case R.id.eleven_activity_create_b:
                for (int i = 0; i < 5; i++) {
                    TextView textView = new TextView(this);
                    textView.setText("B列 "+i);
                    textView.setTag("b_"+i);

                    groupB.addView(textView);
                }
                break;
            case R.id.eleven_activity_destory_a:

                List<View> viewsToRemove = new ArrayList<>();
                for (int i = 0; i < groupA.getChildCount(); i++) {
                    if(groupA.getChildAt(i) instanceof TextView){
                        String tag = ((String)(groupA.getChildAt(i).getTag()));
                        if(tag!=null){
                            if(tag.contains("a_"))
                                viewsToRemove.add(groupA.getChildAt(i));

                        }
                    }
                }

                for (int i = 0; i < viewsToRemove.size(); i++) {
                    groupA.removeView(viewsToRemove.get(i));
                }

                break;
            case R.id.eleven_activity_destory_b:
                List<View> bviewsToRemove = new ArrayList<>();
                for (int i = 0; i < groupB.getChildCount(); i++) {
                    if(groupB.getChildAt(i) instanceof TextView){
                        String tag = ((String)(groupB.getChildAt(i).getTag()));
                        if(tag!=null){
                            if(tag.contains("b_"))
                                bviewsToRemove.add(groupB.getChildAt(i));

                        }
                    }
                }

                for (int i = 0; i < bviewsToRemove.size(); i++) {
                    groupB.removeView(bviewsToRemove.get(i));
                }
                break;
        }
    }
}
