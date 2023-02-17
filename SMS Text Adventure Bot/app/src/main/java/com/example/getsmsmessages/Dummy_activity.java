package com.example.getsmsmessages;

import android.app.Application;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

//This class is needed to "fool" the application to let objects run in the background
//because they need a reference to an active activity and they cant refer to the main one
//This is just a default/empty activity
public class Dummy_activity extends AppCompatActivity {

    private static Application mApplication;

    public static Application getAppApplication() {
        return mApplication;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        mApplication = getApplication();
    }

}