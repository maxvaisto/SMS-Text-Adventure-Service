package com.example.getsmsmessages;

import static androidx.work.WorkInfo.State.SUCCEEDED;
import static com.example.getsmsmessages.Constants.KEY_MESSAGE_URI;
import static com.example.getsmsmessages.Constants.KEY_PURPOSE_URI;
import static com.example.getsmsmessages.Constants.KEY_RETURN_MESSAGE_URI;
import static com.example.getsmsmessages.Constants.KEY_SENDER_URI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/*
    THIS FUNCTION IS USED TO FOR THE UI AND APP PERMISSIONS
 */
public class MainActivity extends AppCompatActivity {

    private static Context context;

    public final int REQUEST_CODE = 1;
    final String[] PERMISSIONS = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
        };

    private UserViewModel mUserViewModel;
    private TextView mPhoneNumberText;
    private TextView mMessageText;
    private WorkManager mWorkManager;
    private Button mButton;
    private TextView mSentSMSMessage;
    private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Application application = getApplication();
        setContentView(R.layout.activity_main);
        mPhoneNumberText = (TextView)findViewById(R.id.phoneNumberText);
        mMessageText = (TextView)findViewById(R.id.SMSText);
        mSentSMSMessage = (TextView)findViewById(R.id.SentSMSMessage);
        mButton = findViewById(R.id.button);

        //GET PERMISSIONS FROM THE USER
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.SEND_SMS)  +
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECEIVE_SMS) +
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    MainActivity.this, new String[] {
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_SMS
                    },
                    REQUEST_CODE
            );

        }

        //THIS FUNCTION LISTENS THE INSERT COMMAND BUTTON
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        builder = new AlertDialog.Builder(this);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sender = mPhoneNumberText.getText().toString();
                String message = mMessageText.getText().toString();
                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to insert command "+ message + " from number " +
                                sender + "?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                insertCommand(v);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(), "Message not sent.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("AlertDialogExample");
                alert.show();

            }
        });
    }

    //Used to insert a command manually by the user
    private void insertCommand(View view) {
        String TAG = "MainProcess";
        String sender = mPhoneNumberText.getText().toString();
        String message = mMessageText.getText().toString();
        String purpose = "RETURN";
        if ((sender.substring(0, Math.min(sender.length(), 2)).equals("04") ||
                sender.substring(0, Math.min(sender.length(), 5)).equals("+3584")) &&
                message.substring(0, Math.min(sender.length(), 1)).equals(".")) {
            Data inputData = new Data.Builder()
                    .putString(KEY_SENDER_URI, sender)
                    .putString(KEY_MESSAGE_URI, message)
                    .putString(KEY_PURPOSE_URI, purpose)
                    .build();
            OneTimeWorkRequest parseRequest_Main =
                    new OneTimeWorkRequest.Builder(MessageParser.class)
                            .setInputData(inputData)
                            .build();
            mWorkManager = WorkManager.getInstance(context.getApplicationContext());
            mWorkManager.enqueue(parseRequest_Main);
            mWorkManager.getWorkInfoByIdLiveData(parseRequest_Main.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(@Nullable WorkInfo workInfo) {
                            if (workInfo != null) {
                                Log.d(TAG, "WorkInfo received: state: " + workInfo.getState());
                                if (workInfo.getState().equals(SUCCEEDED)){
                                    String message = workInfo.getOutputData().getString(KEY_RETURN_MESSAGE_URI);
                                    Log.d(TAG, "Message sent!");
                                    mSentSMSMessage.setText(message);
                                }

                            }
                        }
                    });

        } else {
            Toast.makeText(getApplicationContext(), "Invalid request",
                    Toast.LENGTH_LONG).show();
        }

    }

    //Get main windows context
    public static Context giveContext(){
        return context;
    }


    //Create the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //Used to show user short the application info text
    public void showInfo(MenuItem item) {
        Toast toast = Toast.makeText(this, R.string.toast_message,
                Toast.LENGTH_SHORT);
        toast.show();
    }
}