package com.example.getsmsmessages;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity( tableName = "user_table")
public class User {


    @NonNull
    @ColumnInfo(name="phoneNumber")
    @PrimaryKey
    private String mPhoneNumber;


    @ColumnInfo(name="previousGames")
    private ArrayList<String> mPreviousGames;

    public User(@NonNull String phoneNumber, @NonNull ArrayList<String> previousGames) {
        this.mPhoneNumber = phoneNumber;
        this.mPreviousGames = previousGames;
    }

    public void setPreviousGames(ArrayList<String> previous) {
        this.mPreviousGames=previous;
    }



    public String getPhoneNumber() {
        return this.mPhoneNumber;
    }

    public ArrayList<String> getPreviousGames() {
        return this.mPreviousGames;
    }



}
