package online.letmesleep.androidapplication.facedetect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.baidu.aip.face.AipFace;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import online.letmesleep.androidapplication.APPConfig;
import online.letmesleep.androidapplication.facedetect.facebean.FaceBean;

/***
 * @author Letmesleep
 * create time 2018/8/20
 * email 506067668@qq.com
 */
public class FaceRecognizeOnline {


    private static AipFace client;
    private static FaceRecognizeOnline facedetect;

    /***
     *          解析json获取识别结果中的result数据 如果识别失败返回null
     * @param res           识别结果
     * @param callback      识别失败回调
     * @return
     */
    private JSONObject getResult(JSONObject res, FaceCallback callback) {
        try {
            if (0 == res.getInt("error_code")) {
                return res.getJSONObject("result");
            } else {
                if (callback != null)
                    callback.onError(res.getString("error_msg"));
            }
        } catch (JSONException e) {
            if (callback != null)
                callback.onError(e.toString());
        }
        return null;
    }

    private FaceRecognizeOnline() {
        client = new AipFace(APPConfig.APP_ID, APPConfig.API_KEY, APPConfig.SECRET_KEY);
        facedetect = this;
    }

    public static FaceRecognizeOnline getInstance() {
        if (facedetect == null) {
            new FaceRecognizeOnline();
        }
        return facedetect;
    }

    /***
     *          检测人脸
     * @param imagePath 图片地址
     * @param callback  回调接口
     */
    public void detectFace(final String imagePath, final FaceCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> options = new HashMap<String, String>();
                options.put("max_face_num", "5");
                options.put("face_type", "LIVE");
                options.put("face_field", "quality");
                JSONObject res = client.detect(getImageBase64StrFromImagePath(imagePath), "BASE64", options);
                Log.e("letmesleep", "" + res.toString());
                res = getResult(res, callback);

                try {
                    if (res != null) {
                        if (callback != null) {
                            callback.onSuccess(res.toString());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    /***
     *          检测图片中有几张人脸
     * @param imagePath         图片路径
     * @param callback          接口回调
     * @return 人脸数
     */
    private int detectFaceNum(String imagePath, FaceCallback callback) {

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("max_face_num", "5");
        options.put("face_type", "LIVE");
        options.put("face_field", "quality");
        JSONObject res = client.detect(getImageBase64StrFromImagePath(imagePath), "BASE64", options);

        res = getResult(res, callback);

        if (res != null) {
            try {
                return res.getInt("face_num");
            } catch (JSONException e) {
                callback.onError(e.toString());
                return 0;
            }
        }
        return 0;
    }


    /***
     *          人脸库中添加人脸信息
     * @param imagePath         图片路径
     * @param groupId           组号
     * @param userId            人脸识别号
     * @param callback           回调
     */
    public void addFace(final String imagePath, final String groupId, final String userId, final FaceCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkImageQuality(imagePath)) {  //人脸图片合格
                    HashMap<String, String> options = new HashMap<String, String>();
                    options.put("user_info", userId);
                    options.put("quality_control", "NORMAL");
                    options.put("liveness_control", "LOW");

                    String imageType = "BASE64";

                    // 人脸注册
                    JSONObject res = client.addUser(getImageBase64StrFromImagePath(imagePath), imageType, groupId, userId, options);
                    try {
                        if (0 == res.getInt("error_code")) {
                            if (callback != null)
                                callback.onSuccess(res.getJSONObject("result").getString("face_token"));
                        } else {
                            if (callback != null)
                                callback.onError(res.toString());
                        }

                    } catch (JSONException e) {
                        if (callback != null)
                            callback.onError(e.toString());

                    }
                } else {
                    if (callback != null)
                        callback.onError("人脸图片质量不足！");
                }
            }
        }).start();

    }

    /***
     *            识别一张图中多个人脸
     * @param imagePath         图片路径
     * @param groupId           组号   多个组号用英文逗号隔开
     * @param callback          回调
     */
    public void mutiSearchFace(final String imagePath, final String groupId, final FaceCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sb = new StringBuffer();
                List<String> datas = cropMutiFaceToBase64EncodeStr(imagePath,callback);
                if(datas.size()==0){
                    return;
                }
                sb.append("图片中有： ");
                for (int i = 0; i < datas.size(); i++) {
                    HashMap<String, String> options = new HashMap<String, String>();
                    options.put("quality_control", "NORMAL");
                    options.put("liveness_control", "LOW");
                    options.put("max_user_num", "3");

                    String imageType = "BASE64";
                    String groupIdList = groupId;    //"a,b,c,d"  max 20
                    // 人脸搜索
                    JSONObject res = client.search(datas.get(i), imageType, groupIdList, options);
                    try {
                        if (0 == res.getInt("error_code")) {
                            org.json.JSONArray ja = res.getJSONObject("result").getJSONArray("user_list");
                            if (ja.length() != 0) {
                                Log.e("letmesleep",ja.getJSONObject(0).getDouble("score")+"  "+res.toString());
                                if(ja.getJSONObject(0).getDouble("score")>80){
                                    JSONObject jo = ja.getJSONObject(0);
                                    String groupid = jo.getString("group_id");
                                    String userid = jo.getString("user_id");
                                    List<FaceBean> fbs = LitePal.where("user_id=?", userid).find(FaceBean.class);
                                    if (fbs.size() != 0) {
                                        sb.append(" "+fbs.get(0).getUsername());
                                    }
                                }
                            }else{
                                if (callback != null)
                                    callback.onError(res.toString());
                            }

                            Thread.sleep(800);
                        }
                    } catch (Exception e) {
                        if (callback != null)
                            callback.onError(e.toString());
                    }

                }
                if(callback!=null)
                    callback.onSuccess(sb.toString());
            }
        }).start();
    }

    /****
     *              单张人脸识别
     * @param imagePath         图片地址
     * @param groupId           组号
     * @param callback          回调
     */
    public void singleFaceSearch(final String imagePath, final String groupId, final FaceCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> options = new HashMap<String, String>();
                options.put("quality_control", "NORMAL");
                options.put("liveness_control", "LOW");
                options.put("max_user_num", "3");
                String image = getImageBase64StrFromImagePath(imagePath);
                String imageType = "BASE64";
                String groupIdList = groupId;    //"a,b,c,d"  max 20
                // 人脸搜索
                JSONObject res = client.search(image, imageType, groupIdList, options);
                try {
                    if (0 == res.getInt("error_code")) {

                        org.json.JSONArray ja = res.getJSONObject("result").getJSONArray("user_list");
                        if (ja.length() != 0) {
                            JSONObject jo = ja.getJSONObject(0);
                            String groupid = jo.getString("group_id");
                            String userid = jo.getString("user_id");
                            List<FaceBean> fbs = LitePal.where("user_id=?", userid).find(FaceBean.class);
                            if (fbs.size() != 0) {
                                if (callback != null) {
                                    callback.onSuccess(fbs.get(0).getUsername());
                                    return;
                                }
                            }

                        }

                        if (callback != null)
                            callback.onError(res.toString());
                    }
                } catch (Exception e) {
                    if (callback != null)
                        callback.onError(e.toString());
                }
            }
        }).start();
    }


    /****
     *          删除人脸信息
     * @param groupId       组号
     * @param userId        用户id
     * @param faceToken     人脸信息识别号  添加人脸的时API给的信息
     * @param callback      回调
     */
    public void deleteFace(final String groupId, final String userId, final String faceToken, final FaceCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 传入可选参数调用接口
                HashMap<String, String> options = new HashMap<String, String>();

                JSONObject res = client.faceDelete(userId, groupId, faceToken, options);
                try {
                    if (0 == res.getInt("error_code")) {
                        if (callback != null) {
                            callback.onSuccess("success");
                        }
                    } else {
                        if (callback != null) {
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
     * @param imagePath         图片地址
     * @return true or  false
     */
    public boolean checkImageQuality(String imagePath) {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("max_face_num", "5");
        options.put("face_type", "LIVE");
        options.put("face_field", "quality");
        JSONObject res = client.detect(getImageBase64StrFromImagePath(imagePath), "BASE64", options);
        try {
            if (0 == res.getInt("error_code")) {
                org.json.JSONArray jsa = res.getJSONObject("result").getJSONArray("face_list");
                if (jsa.length() != 1)
                    return false;
                for (int i = 0; i < jsa.length(); i++) {
                    JSONObject qualityJo = jsa.getJSONObject(i).getJSONObject("quality");
                    JSONObject angleJo = jsa.getJSONObject(i).getJSONObject("angle");
                    double yaw = angleJo.getDouble("yaw");
                    double pitch = angleJo.getDouble("pitch");
                    double roll = angleJo.getDouble("roll");
                    if (yaw < 0)
                        yaw = -yaw;
                    if (pitch < 0)
                        pitch = -pitch;
                    if (yaw < 0)
                        roll = -roll;
                    JSONObject occlusionJo = qualityJo.getJSONObject("occlusion");

                    double blur = qualityJo.getDouble("blur");
                    double illumination = qualityJo.getDouble("illumination");
                    //int completeness = qualityJo.getInt("completeness");

                    if (blur < 0.7 && illumination > 40 && yaw < 20 && pitch < 20 && roll < 20) {
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


    /***
     * 根据图片地址转换为base64编码字符串
     * @param imgFile           图片地址
     * @return base64编码图片
     */
    public String getImageBase64StrFromImagePath(String imgFile) {
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

        // base64
        String encodeString = new String(Base64.encode(data, Base64.DEFAULT));
        return encodeString;
    }



    /***
     * 将bitmap转变为Base64编码的字符串
     * @param bitmap        bitmap
     * @return              base64 encode String
     */
    private String getImageBase64StrFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream baos = null;
        String reslut = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                /**
                 * 压缩只对保存有效果bitmap还是原来的大小
                 */
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] byteArray = baos.toByteArray();
                reslut = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return reslut;
    }

    public interface FaceCallback {
        void onSuccess(String message);

        void onError(String err);
    }

    /****
     * 一张图中包含多个人脸的识别
     * 1.检测人脸数
     * 2.判断人脸是否符合识别要求
     * 3.根据人脸数进行切割图片
     * 4.base64编码
     *
     * @param imagePath     图片地址
     * @param callback         回掉
     * @return          一张图中所有符合识别条件的人脸图片的base64编码集合
     */
    public List<String> cropMutiFaceToBase64EncodeStr(String imagePath,FaceCallback callback) {
        List<String> datas = new ArrayList<>();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        try {
            /***
             *      检测人脸数目
             *      检查人脸清晰度、旋转角度、遮盖判断是否符合识别条件
             *      确定人脸在图像中的位置
             */
            List<RectLocation> locations = new ArrayList<>();
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("max_face_num", "5");           //最多识别人脸数
            options.put("face_type", "LIVE");
            options.put("face_field", "quality");
            JSONObject res = client.detect(getImageBase64StrFromImagePath(imagePath), "BASE64", options);
            if (0 == res.getInt("error_code")) {
                org.json.JSONArray jsa = res.getJSONObject("result").getJSONArray("face_list");
                for (int i = 0; i < jsa.length(); i++) {
                    JSONObject qualityJo = jsa.getJSONObject(i).getJSONObject("quality");
                    JSONObject angleJo = jsa.getJSONObject(i).getJSONObject("angle");
                    JSONObject locationJo = jsa.getJSONObject(i).getJSONObject("location");
                    double yaw = angleJo.getDouble("yaw");
                    double pitch = angleJo.getDouble("pitch");
                    double roll = angleJo.getDouble("roll");
                    if (yaw < 0)
                        yaw = -yaw;
                    if (pitch < 0)
                        pitch = -pitch;
                    if (yaw < 0)
                        roll = -roll;
                    JSONObject occlusionJo = qualityJo.getJSONObject("occlusion");

                    double blur = qualityJo.getDouble("blur");
                    double illumination = qualityJo.getDouble("illumination");
                    //int completeness = qualityJo.getInt("completeness");

                    if (blur < 0.7 && illumination > 40 && yaw < 20 && pitch < 20 && roll < 20) {
                        RectLocation rectLocation = new RectLocation();
                        rectLocation.left = (int)locationJo.getDouble("left");
                        rectLocation.top = (int)locationJo.getDouble("top");
                        rectLocation.width = (int)locationJo.getDouble("width");
                        rectLocation.height = (int)locationJo.getDouble("height");
                        locations.add(rectLocation);
                    }
                }

                /****
                 *      按人脸位置切割图片分组
                 *      转换成字节数组
                 *      base64编码
                 */

                for (RectLocation rectLocation:locations) {
                    Bitmap bitmapItem = Bitmap.createBitmap(bitmap,rectLocation.left,rectLocation.top,rectLocation.width,rectLocation.height);
                    datas.add(getImageBase64StrFromBitmap(bitmapItem));
                    gcBitmap(bitmapItem);
                }
                gcBitmap(bitmap);

            }else{
                if(callback!=null)
                    callback.onError(res.getString("error_msg"));
            }
        } catch (Exception e) {
            if(callback!=null)
                callback.onError(e.toString());
        }
        return datas;
    }


    /****
     *      回收bitmap
     * @param bitmap    bitmap
     */
    public static void gcBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle(); // 回收图片所占的内存
            bitmap = null;
            System.gc(); // 提醒系统及时回收
        }
    }

    private class RectLocation {
        int left;
        int top;
        int width;
        int height;
    }
}
