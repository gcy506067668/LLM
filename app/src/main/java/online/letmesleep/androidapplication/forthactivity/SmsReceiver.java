package online.letmesleep.androidapplication.forthactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "letmesleep";

    public static SmsReceiver smsReceiver;

    public interface Callback{
        void receiver(String content,String sender);
    }
    List<Callback> listeners;
    public void addListener(Callback callback){
        listeners.add(callback);
    }

    public static SmsReceiver getInstance(){
        return smsReceiver;
    }

    public SmsReceiver() {
        Log.e(TAG, "SmsReceiver!");
        listeners = new ArrayList<>();
        smsReceiver = this;
    }
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.e(TAG, "onReceive!");
        Cursor cursor = null;
        try {
            if (SMS_RECEIVED.equals(intent.getAction())) {
                Log.e(TAG, "sms received!");
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    final SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    if (messages.length > 0) {
                        String content = messages[0].getMessageBody();
                        String sender = messages[0].getOriginatingAddress();
                        long msgDate = messages[0].getTimestampMillis();
                        String smsToast = "New SMS received from : "
                                + sender + "\n'"
                                + content + "'";
                        Toast.makeText(context, smsToast, Toast.LENGTH_LONG)
                                .show();
                        Log.e(TAG, "message from: " + sender + ", message body: " + content
                                + ", message date: " + msgDate);
                        //自己的逻辑
                    }
                }
//                cursor = context.getContentResolver().query(Uri.parse("content://sms"), new String[] { "_id", "address", "read", "body", "date" }, "read = ? ", new String[] { "0" }, "date desc");
//                if (null == cursor){
//                    return;
//                }
//                Log.i(TAG,"m cursor count is "+cursor.getCount());
//                Log.i(TAG,"m first is "+cursor.moveToFirst());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception : " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }
}
