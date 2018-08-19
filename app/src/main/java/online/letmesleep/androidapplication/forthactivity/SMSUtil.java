package online.letmesleep.androidapplication.forthactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class SMSUtil {
    public static void sendMessage(String message,List<String> receiver){
        for (int i = 0; i <receiver.size() ; i++) {
            SmsManager sms = SmsManager.getDefault();
            if(sms==null)
                return;
            List<String> texts = sms.divideMessage(message);
            for (String text : texts) {
                sms.sendTextMessage(receiver.get(i),  null,  message,  null, null);
            }
        }


    }

    public static void sendMessage(String message,String[] receiver){
        for (int i = 0; i <receiver.length ; i++) {
            SmsManager sms = SmsManager.getDefault();
            if(sms==null)
                return;
            List<String> texts = sms.divideMessage(message);
            for (String text : texts) {
                sms.sendTextMessage(receiver[i],  null,  message,  null, null);
            }
        }


    }

}
