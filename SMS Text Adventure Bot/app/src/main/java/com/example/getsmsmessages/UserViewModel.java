package com.example.getsmsmessages;


import android.app.Application;
import android.content.IntentFilter;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends AndroidViewModel {

    public UserRepository mUserRepository;

    private LiveData<List<String>> mAllPhoneNumbers;

    private static Application mApplication;
    private MutableLiveData<List<String>> mMutableMessage;

    public UserViewModel(Application application) {
        super(application);
        Log.d("Crash testing","10");
        mMutableMessage = new MutableLiveData<>();
        mApplication = application;
        SmsReceiver mReceiver = new SmsReceiver(mMutableMessage);

        application.registerReceiver(mReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        mUserRepository = new UserRepository(application);
        mAllPhoneNumbers = mUserRepository.getAllPhoneNumbers();


    }
    LiveData<List<String>> getAllPhoneNumbers() { return mAllPhoneNumbers; }
    public void insertUser(String userPhone, ArrayList<String> userPreviousGames){
        mUserRepository.insertUser(userPhone,userPreviousGames);
    }
    public Application getApplication(){
        return mApplication;
    }

    public ArrayList<String> getUserPreviousGame(String userPhone) {
        return mUserRepository.getUserPreviousGame(userPhone);
    }

}
