package online.letmesleep.androidapplication.alivoice;


import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import java.util.LinkedHashMap;
import java.util.Map;

import online.letmesleep.androidapplication.forthactivity.SoundPoolUtil;

public class SpeechTranscriberUtil implements EventListener{

    private VoiceCallback callback;
    private EventManager asr;

    public SpeechTranscriberUtil(Context context, VoiceCallback callback){

        asr = EventManagerFactory.create(context, "asr");
        asr.registerListener(this);
        this.callback = callback;

    }



    /***
     *      开始实时翻译
     *
     */
    public void startTranscriber(){
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START; // 替换成测试的event

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        // params.put(SpeechConstant.NLU, "enable");
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音
        // params.put(SpeechConstant.IN_FILE, "res:///com/baidu/android/voicedemo/16k_test.pcm");
        //params.put(SpeechConstant.VAD, SpeechConstant.VAD_ENDPOINT_TIMEOUT);
        // params.put(SpeechConstant.PROP ,20000);
        // params.put(SpeechConstant.PID, 1537); // 中文输入法模型，有逗号
        // 请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);


        String json = null; // 可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
    }




    /**
     *      结束实时翻译
     */
    public void stopTranscriber(){
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }

    String result = null;
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {         //引擎启动
            SoundPoolUtil.play(1);
        }
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH)) {       //长语句识别结束
            SoundPoolUtil.play(4);
        }

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {       //识别结束 可能包含错误
            if(callback!=null)
                callback.onAppend(result);
        }

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {

            try {

                result = new JSONObject(params).getString("results_recognition");
                result = result.replace("\"","");

                if(callback!=null)
                    callback.onTemp(result);
            } catch (JSONException e) {
                if(callback!=null)
                    callback.onError(e.toString());
            }

        }


    }



    public interface VoiceCallback{
        void onTemp(String temp);
        void onAppend(String message);
        void onError(String err);

    }




}
