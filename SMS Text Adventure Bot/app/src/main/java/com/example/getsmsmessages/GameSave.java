package com.example.getsmsmessages;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import java.util.ArrayList;

//This SQL database stores for users their current (also means games that they've quit for now)
//paths that they have chosen in the adventures
//Here the primary key is the combination of the user phone number and game name.
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

    //This is used to set the users new position in the game
    //This could also be done by having separate functions for
    //(1) Reset game state
    //(2) Add a new page to the list (go forward)
    //(3) Remove the last page from the list (go back)
    public void setGameProgress(ArrayList<String> userGameProgress) {
        this.mGameProgress=userGameProgress;
    }

    //Not implemented -- Can be used to import the users stories to a new number
    public void setPhoneNumber(String userPhoneNumber){
        this.mPhoneNumber = userPhoneNumber;
    }

    //Used to set the user game progress
    public void setGame(String userGame){
        this.mGame = userGame;
    }


    //Used retrieve the user phone number
    //This is needed to find the user's game from the database
    public String getPhoneNumber() {
        return this.mPhoneNumber;
    }

    //This is used to get the game name
    //Also needed for selecting the right database entry
    public String getGame() {
        return this.mGame;
    }

    //This is used to retrieve the users current game progress
    public ArrayList<String> getGameProgress() {
        return this.mGameProgress;
    }
}
