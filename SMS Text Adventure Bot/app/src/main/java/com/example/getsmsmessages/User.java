package com.example.getsmsmessages;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

//User SQL database contain the information of every user who has sent a message (ever)
//this contains their phone number and their previously played games
@Entity(tableName = "user_table")
public class User {


    @NonNull
    @ColumnInfo(name = "phoneNumber")
    @PrimaryKey
    private String mPhoneNumber;


    @ColumnInfo(name = "previousGames")
    private ArrayList<String> mPreviousGames;

    public User(@NonNull String phoneNumber, @NonNull ArrayList<String> previousGames) {
        this.mPhoneNumber = phoneNumber;
        this.mPreviousGames = previousGames;
    }

    //Used to receive the user phone number
    public String getPhoneNumber() {
        return this.mPhoneNumber;
    }

    //Used to receive all of the games the user is currently playing (not at start)
    public ArrayList<String> getPreviousGames() {
        return this.mPreviousGames;
    }

    //Update the user visited game list
    public void setPreviousGames(ArrayList<String> previous) {
        this.mPreviousGames = previous;
    }


}
