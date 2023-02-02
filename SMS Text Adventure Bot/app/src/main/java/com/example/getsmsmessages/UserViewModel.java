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


/*
    UserViewModel is used to hold all of the user and game data that the application needs
    currently. (Things that wont be saved)
 */
public class UserViewModel extends AndroidViewModel {

    public UserRepository mUserRepository;

    //Unused
    private LiveData<List<String>> mAllPhoneNumbers;

    //App handle
    private static Application mApplication;
    private MutableLiveData<List<String>> mMutableMessage;

    //On creation create sms receiver (and register this), create new user user repo and
    //gather all phone numbers from it
    public UserViewModel(Application application) {
        super(application);
        mMutableMessage = new MutableLiveData<>();
        mApplication = application;
        SmsReceiver mReceiver = new SmsReceiver(mMutableMessage);

        application.registerReceiver(mReceiver,
                new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        //Create new repo
        mUserRepository = new UserRepository(application);

    }

    //Used to return the application handle
    public Application getApplication(){
        return mApplication;
    }

    /*
        Unimplemented functions
     */

    //Used to collect all of the phone numbers currently in the database
    LiveData<List<String>> getAllPhoneNumbers() { return mAllPhoneNumbers; }

    //Used to insert a user manually
    public void insertUser(String userPhone, ArrayList<String> userPreviousGames){
        mUserRepository.insertUser(userPhone,userPreviousGames);
    }

    //Used to get user previous game manually
    public ArrayList<String> getUserPreviousGame(String userPhone) {
        return mUserRepository.getUserPreviousGame(userPhone);
    }

}
