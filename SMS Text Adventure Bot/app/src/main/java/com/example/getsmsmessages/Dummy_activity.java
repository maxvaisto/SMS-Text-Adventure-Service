package com.example.getsmsmessages;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;

//This class is needed to "fool" the application to let objects run in the background
//because they need a reference to an active activity and they cant refer to the main one
//This is just a default/empty activity
public class Dummy_activity extends AppCompatActivity {

    private static Application mApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        mApplication = getApplication();
    }
    public static Application getAppApplication(){
        return mApplication;
    }

}