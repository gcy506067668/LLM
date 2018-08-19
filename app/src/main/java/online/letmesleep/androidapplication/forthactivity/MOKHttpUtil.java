package online.letmesleep.androidapplication.forthactivity;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Letmesleep on 2017/8/29.
 */

public class MOKHttpUtil {

    private static MOKHttpUtil mokHttpUtil;
    OkHttpClient client;


    private MOKHttpUtil() {
        mokHttpUtil = this;
        client = new OkHttpClient();
    }

    public static MOKHttpUtil getInstance() {
        if (mokHttpUtil == null)
            new MOKHttpUtil();
        return mokHttpUtil;
    }

    public interface Callback {
        void onSuccess(String resp);

        void onFaild(String err);
    }

    public void HttpGet(final String url, final Callback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(client==null)
                    return;
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    //return response.body().string();
                    if(callback!=null){
                        callback.onSuccess(response.body().string());
                    }
                } catch (IOException e) {
                    if(callback!=null){
                        callback.onFaild(e.toString());
                    }
                }
            }
        }).start();
    }

    public void HttpGet(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    //return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }




    public static void httpPost(final String url, final String json, final Callback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = getInstance().post(url,json);
                    if(callback!=null)
                        callback.onSuccess(result);
                } catch (IOException e) {
                    if(callback!=null)
                        callback.onFaild(e.toString());
                }
            }
        }).start();
    }


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
