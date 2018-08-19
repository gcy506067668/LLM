package online.letmesleep.androidapplication.facedetect;

import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.baidu.aip.face.AipFace;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import online.letmesleep.androidapplication.facedetect.facebean.FaceBean;

public class FaceRecognizeOnline {



    private static AipFace client ;
    private static FaceRecognizeOnline facedetect ;
    private FaceRecognizeOnline(){
        client = new AipFace(Config.APP_ID, Config.API_KEY, Config.SECRET_KEY);
        facedetect = this;
    }

    public static FaceRecognizeOnline getInstance(){
        if(facedetect==null){
            new FaceRecognizeOnline();
        }
        return facedetect;
    }

    public void detectFace(final String imagePath, final FaceDetectCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.setConnectionTimeoutInMillis(2000);
                client.setSocketTimeoutInMillis(60000);
                HashMap<String, String> options = new HashMap<String, String>();
                options.put("max_face_num", "5");
                options.put("face_type", "LIVE");
                options.put("face_field", "quality");
                JSONObject res = client.detect(getImageStr(imagePath), "BASE64", options);
                try {
                    if(callback!=null) {
                        callback.detectSuccess(res.getJSONObject("result").getInt("face_num"));
                        callback.allResult(res.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }



    public void addFace(final String imagePath,final String groupId,final String userId,final FaceCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(checkImageQuality(imagePath)){  //人脸图片合格
                    HashMap<String, String> options = new HashMap<String, String>();
                    options.put("quality_control", "NORMAL");
                    options.put("liveness_control", "LOW");

                    String imageType = "BASE64";


                    // 人脸注册
                    JSONObject res = client.addUser(getImageStr(imagePath), imageType, groupId, userId, options);
                    try {
                        if(0==res.getInt("error_code")){

                            if(callback!=null)
                                callback.onSuccess(res.getJSONObject("result").getString("face_token"));
                        }else{
                            if(callback!=null)
                                callback.onError(res.toString());
                        }

                    } catch (JSONException e) {
                        if(callback!=null)
                            callback.onError(e.toString());

                    }
                }else{
                    if(callback!=null)
                        callback.onError("人脸图片质量不足！");
                }
            }
        }).start();

    }


    public void searchFace(final String imagePath,final String groupId,final FaceCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> options = new HashMap<String, String>();
                options.put("quality_control", "NORMAL");
                options.put("liveness_control", "LOW");
                options.put("max_user_num", "3");

                String image = getImageStr(imagePath);
                String imageType = "BASE64";
                String groupIdList = groupId;    //"a,b,c,d"  max 20

                // 人脸搜索
                JSONObject res = client.search(image, imageType, groupIdList, options);
                try {
                    if(0==res.getInt("error_code")){
                        Log.e("letmesleep",""+res.toString());
                        org.json.JSONArray ja = res.getJSONObject("result").getJSONArray("user_list");
                        if(ja.length()!=0){
                            JSONObject jo= ja.getJSONObject(0);
                            String groupid = jo.getString("group_id");
                            String userid = jo.getString("user_id");
                            List<FaceBean> fbs = LitePal.where("user_id=?",userid).find(FaceBean.class);
                            if(fbs.size()!=0){
                                if(callback!=null) {
                                    callback.onSuccess(fbs.get(0).getUsername());
                                    return;
                                }
                            }

                        }

                        if(callback!=null)
                            callback.onError(res.toString());
                    }
                } catch (Exception e) {
                    if(callback!=null)
                        callback.onError(e.toString());
                }
            }
        }).start();
    }



    public void deleteFace(final String groupId, final String userId, final String faceToken, final FaceCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 传入可选参数调用接口
                HashMap<String, String> options = new HashMap<String, String>();

                JSONObject res = client.faceDelete(userId, groupId, faceToken, options);
                try {
                    if(0==res.getInt("error_code")){
                        if(callback!=null){
                            callback.onSuccess("success");
                        }
                    }else{
                        if(callback!=null) {
                            callback.onError("error");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /***
     *          检测人脸是否符合添加条件
     * @param imagePath
     * @return
     */
    public boolean checkImageQuality(String imagePath) {
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("max_face_num", "5");
        options.put("face_type", "LIVE");
        options.put("face_field", "quality");
        JSONObject res = client.detect(getImageStr(imagePath), "BASE64", options);
        try {
            if(0==res.getInt("error_code")){
                org.json.JSONArray jsa = res.getJSONObject("result").getJSONArray("face_list");
                if(jsa.length()!=1)
                    return false;
                for (int i = 0; i < jsa.length(); i++) {
                    JSONObject qualityJo = jsa.getJSONObject(i).getJSONObject("quality");
                    JSONObject angleJo = jsa.getJSONObject(i).getJSONObject("angle");
                    double yaw = angleJo.getDouble("yaw");
                    double pitch = angleJo.getDouble("pitch");
                    double roll = angleJo.getDouble("roll");
                    if(yaw<0)
                        yaw = -yaw;
                    if(pitch<0)
                        pitch = -pitch;
                    if(yaw<0)
                        roll = -roll;
                    JSONObject occlusionJo = qualityJo.getJSONObject("occlusion");

                    double blur = qualityJo.getDouble("blur");
                    double illumination = qualityJo.getDouble("illumination");
                    //int completeness = qualityJo.getInt("completeness");

                    if(blur<0.7&&illumination>40&&yaw<20&&pitch<20&&roll<20){
                        return true;
                    }
                    return false;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * @Description: 根据图片地址转换为base64编码字符串
     * @Author:
     * @CreateTime:
     * @return
     */
    public static String getImageStr(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 加密
        String encodeString = new String(Base64.encode(data,Base64.DEFAULT));
        return encodeString;
    }

    public interface FaceDetectCallback {
        void detectSuccess(int faceCount);
        void allResult(String result);
    }

    public interface FaceCallback {
        void onSuccess(String message);
        void onError(String err);
    }
}
