package com.example.getsmsmessages;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import java.util.ArrayList;


@Entity(primaryKeys = {"phoneNumber","game"}, tableName = "gameSaves_table")
public class GameSave {


    @NonNull
    @ColumnInfo(name="phoneNumber")
    private String mPhoneNumber;

    @NonNull
    @ColumnInfo(name="game")
    private String mGame;

    @ColumnInfo(name="gameProgress")
    private ArrayList<String> mGameProgress;

    public GameSave(@NonNull String mPhoneNumber, @NonNull String mGame, ArrayList<String> mGameProgress) {
        this.mPhoneNumber = mPhoneNumber;
        this.mGame = mGame;
        this.mGameProgress = mGameProgress;
    }


    public void setGameProgress(ArrayList<String> userGameProgress) {
        this.mGameProgress=userGameProgress;
    }

    public void setPhoneNumber(String userPhoneNumber){
        this.mPhoneNumber = userPhoneNumber;
    }

    public void setGame(String userGame){
        this.mGame = userGame;
    }
    public String getPhoneNumber() {
        return this.mPhoneNumber;
    }

    public String getGame() {
        return this.mGame;
    }

    public ArrayList<String> getGameProgress() {
        return this.mGameProgress;
    }
}
