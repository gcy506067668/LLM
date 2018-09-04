package online.letmesleep.androidapplication;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.litepal.LitePal;

import online.letmesleep.androidapplication.forthactivity.SoundPoolUtil;

public class MApplication extends Application {

    private static MApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        SpeechUtility.createUtility(this, SpeechConstant.APPID+"="+ APPConfig.IFLY_APPID);
        SoundPoolUtil.initSoundPool(this);
        LitePal.initialize(this);
    }

    public static MApplication getInstance(){
        return mApplication;
    }
}
