package online.letmesleep.androidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toolbar;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {


    private Bundle getIntentBundle(){
        Intent intent = getIntent();
        if(intent == null)
            return null;
        else
            return intent.getExtras();
    }

    protected String getIntentString(String key){
        if(getIntentBundle()==null)
            return "";
        else
            return getIntentBundle().getString(key);
    }

    protected String getIntentString(String key,String defaultValue){
        if(getIntentBundle()==null)
            return defaultValue;
        else
            return getIntentBundle().getString(key);
    }

    protected long getIntentLong(String key){
        if(getIntentBundle()==null)
            return 0;
        else
            return getIntentBundle().getLong(key);
    }

    protected long getIntentLong(String key,long defaultValue){
        if(getIntentBundle()==null)
            return defaultValue;
        else
            return getIntentBundle().getLong(key);
    }

    protected long getIntentInt(String key){
        if(getIntentBundle()==null)
            return 0;
        else
            return getIntentBundle().getInt(key);
    }

    protected long getIntentInt(String key,long defaultValue){
        if(getIntentBundle()==null)
            return defaultValue;
        else
            return getIntentBundle().getInt(key);
    }

    protected void startActivity(Class<?> activity){
        super.startActivity(new Intent(this,activity));
    }

    protected void startActivityForResult(Class<?> activity,int requestCode){
        super.startActivityForResult(new Intent(this,activity),requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView();
        setContentView(setLayoutId());
        ButterKnife.bind(this);
        initView(savedInstanceState);
        initData();



    }

    public void beforeSetContentView() {
    }

    public abstract int setLayoutId();

    public abstract void initView(Bundle savedInstanceState);

    public abstract void initData();

    @UiThread
    protected void enableBackButton(){
        if(getSupportActionBar()==null)
            return;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @UiThread
    protected void setSupportActionBar(int resId){
        android.support.v7.widget.Toolbar toolbar = findViewById(resId);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
