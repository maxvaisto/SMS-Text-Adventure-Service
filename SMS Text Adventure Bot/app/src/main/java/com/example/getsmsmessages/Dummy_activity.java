package com.example.getsmsmessages;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;

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