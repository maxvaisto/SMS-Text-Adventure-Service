package com.example.getsmsmessages;

import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
    SendSMS class is used to send the compiled messages to the user
    There are currently two alternatives for sending a SMS message
    of which the first one is not used

    The functions contain debugging Toast messages that are currently unused
 */
public class SendSMS {

    private static String TAG="SMS_OUT";

    //Can be used to send a single message
    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            PendingIntent sentPI;
            String SENT = "SMS_SENT";
            sentPI = PendingIntent.getBroadcast(MainActivity.giveContext(),
                    0,new Intent(SENT), 0);
            smsManager.sendTextMessage(phoneNo, null, msg, sentPI, null);
            Log.d("SMS_CHECK","MESSAGE: " + msg + " SENT!");
            //Toast.makeText(getApplicationContext(), "Message Sent",
            //        Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.e("SMS_CHECK","MESSAGE NOT SENT!");
            //Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
            //       Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public void sendLongSMS(String phoneNo, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phoneNo,null, parts, null, null);
            Log.d(TAG,"ALL SENT!") ;

            //Toast.makeText(getApplicationContext(), "Message Sent",
            //        Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.e(TAG,"MESSAGE NOT SENT!");
            //Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
            //       Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

}
