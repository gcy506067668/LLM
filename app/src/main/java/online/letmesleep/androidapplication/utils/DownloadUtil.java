package online.letmesleep.androidapplication.utils;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import online.letmesleep.androidapplication.bean.Contacts;

public class DownloadUtil {

    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * @param url 下载连接
     * @param saveDir 储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public void download(final String url, final String saveDir, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, getNameFromUrl(url));
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess(file);
                } catch (Exception e) {
                    Log.e("error",e.toString());
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException
     * 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    @NonNull
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(File file);

        /**
         * @param progress
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }


    /*****
     *              从服务器获取联系人的json字符串
     * @param json
     * @return
     */
    public static List<Contacts> getContactsArrayFromJsonString(String json){
        List<Contacts> datas = new ArrayList<>();
        try{
            JSONArray array = com.alibaba.fastjson.JSONObject.parseArray(json);
            if(array==null)
                return datas;
            for (int i = 0; i < array.size(); i++) {
                datas.add(array.getJSONObject(i).toJavaObject(Contacts.class));
            }}catch (Exception e){
        }
        return datas;
    }

    /*****
     *      HTTP get请求
     * @param url        地址
     * @param callback   回调
     */
    public void HttpGet(final String url, final MCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(okHttpClient==null)
                    return;
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
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

    /***
     *  HTTP get请求  无回调
     * @param url   地址
     */
    public void HttpGet(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                    //return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    public interface MCallback{
        void onSuccess(String result);
        void onFaild(String err);
    }


    /****
     *          HTTP post请求
     * @param url    地址
     * @param json      参数
     * @param callback      回调
     */
    public static void httpPost(final String url, final String json, final MCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = get().post(url,json);
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
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

}

