package com.example.getsmsmessages;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;


/*
    The user repository connects the User game information containing room database
    to the ViewModels with the creation of simple API commands
*/
public class UserRepository {


    private UserDao mUserDao;

    private LiveData<List<String>> mAllPhoneNumbers;

    //First when the user repository is created connect it to the database DAO
    UserRepository(Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);
        mUserDao = db.userDao();
        mAllPhoneNumbers = mUserDao.getAllPhoneNumbers();
    }


    /*
       REPOSITORY COMMANDS BELOW
     */

    LiveData<List<String>> getAllPhoneNumbers() {
        return mAllPhoneNumbers;
    }

    //Add a new user
    public void insertUser(String userPhone, ArrayList<String> userPreviousGame) {
        User userInfo = new User(userPhone, userPreviousGame);
        MyRoomDatabase.databaseWriteExecutor.execute(() -> mUserDao.insertUser(userInfo));

    }

    //Ge the previous game file
    public ArrayList<String> getUserPreviousGame(String userPhone) {
        //Fetch user games from the DAO
        List<User> userList = mUserDao.getPreviousGames(userPhone);
        if (!userList.isEmpty()) {
            if (!userList.get(0).getPreviousGames().isEmpty()) {
                return userList.get(0).getPreviousGames();
            }

        }
        return new ArrayList<>();

    }

    //Insert the new game state
    public void insertUserGame(String userPhone, String game, ArrayList<String> userGameProgress) {
        GameSave userGameSave = new GameSave(userPhone, game, userGameProgress);
        MyRoomDatabase.databaseWriteExecutor.execute(() -> mUserDao.insertGame(userGameSave));

    }

    public ArrayList<String> getUserGameProgress(String userPhone, String game) {
        //Fetch user game progress from the DAO
        List<GameSave> userGameSave = mUserDao.getProgress(userPhone, game);
        if (!userGameSave.isEmpty()) {
            if (!userGameSave.get(0).getGameProgress().isEmpty()) {
                return userGameSave.get(0).getGameProgress();
            } else {
                Log.d("UserRepository", "Empty gameProgress for user " + userPhone
                        + " in game " + game);
            }

        } else {
            Log.d("UserRepository", "Empty GameSave for user " + userPhone
                    + " in game " + game);
        }
        return new ArrayList<>();
    }

    public void deleteUser(String userPhone) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> mUserDao.deleteUser(userPhone));

    }

    /*
        BELOW UNIMPLEMENTED BUT USEFUL COMMANDS
     */
    public void deleteUserGameSave(String userPhone, String game) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> mUserDao.deleteUserGame(userPhone, game));

    }


    public void deleteAllUsers() {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> mUserDao.deleteAllUsers());

    }

    public void deleteAllGamesOfAllUsers() {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> mUserDao.deleteAllUserGamesForAllUsers());

    }

    public void deleteAllGamesOfUser(String userPhone) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> mUserDao.deleteAllGamesOfUser(userPhone));

    }


    public void deleteUserGame(String userPhone, String game) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> mUserDao.deleteUserGame(userPhone, game));

    }
    /*
       REPOSITORY COMMANDS ABOVE
     */


}
