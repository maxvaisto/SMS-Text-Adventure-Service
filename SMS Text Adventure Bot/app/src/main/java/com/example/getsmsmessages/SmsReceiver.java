package com.example.getsmsmessages;


import static com.example.getsmsmessages.Constants.KEY_MESSAGE_URI;
import static com.example.getsmsmessages.Constants.KEY_SENDER_URI;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.List;
//SmsReceiver is used to receive and check all of the user messages to see
// if they contain the components to be interpreted as a potential message
public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private final MutableLiveData<List<String>> mData;
    public LiveData<List<String>> getData() {
        return mData;
    }

    public SmsReceiver (MutableLiveData<List<String>> mMutableMessage) {
        mData = mMutableMessage;
    }

    //We have our own chain of action that will take place after a message is received
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();

        //Check if the message is not empty
        if (intentAction != null) {
            Log.d("action_name",intentAction );

            //Create default toastMessage which informs of an error
            String toastMessage = context.getString(R.string.unknown_intent);

            //Check the type of received message which should be SMS_RECEIVED
            if (SMS_RECEIVED.equals(intentAction)) {
                toastMessage = "SMS Received";
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    // get sms objects
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus.length == 0) {
                        Log.d("SMS_Received", "EMPTY MESSAGE");
                        return;

                    }

                    // large message might be broken into many
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        sb.append(messages[i].getMessageBody());
                    }
                    String sender = messages[0].getOriginatingAddress();
                    String message = sb.toString();

                    //Check that the message comes from a valid country and from a toll-free
                    //number.
                    //This needs to be changed manually if the program should read messages from
                    //regions outside of Finland and from number that does not start with 04 or
                    // +3584 and that the message start with a period ('.') -sign
                    Log.d("SMS_CHECK", (sender.substring(0, Math.min(sender.length(), 2))));

                    if ((sender.substring(0, Math.min(sender.length(), 2)).equals("04") ||
                            sender.substring(0, Math.min(sender.length(), 5)).equals("+3584")) &&
                            message.substring(0, Math.min(sender.length(), 1)).equals(".")) {


                        Log.d("SMS_Received", "FROM: " + '"' + sender + '"'
                                + " MESSAGE: " + '"' + message + '"');


                        Data outputData = new Data.Builder()
                                .putString(KEY_SENDER_URI, sender)
                                .putString(KEY_MESSAGE_URI, message)
                                .build();

                        OneTimeWorkRequest parseRequest =
                                new OneTimeWorkRequest.Builder(MessageParser.class)
                                        .setInputData(outputData)
                                        .build();

                        WorkManager mWorkManager = WorkManager.getInstance(context.getApplicationContext());
                        mWorkManager.enqueue(parseRequest);


                    }

                    Log.d("SMS_CHECK", "BACK");

                }
            }

            // Display the toast.
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();

        }
    }

}