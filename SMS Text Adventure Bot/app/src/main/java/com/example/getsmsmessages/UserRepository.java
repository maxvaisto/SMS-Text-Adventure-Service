package com.example.getsmsmessages;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private UserDao mUserDao;

    //IS THIS NEEDED?
    private LiveData<List<String>> mAllPhoneNumbers;


    UserRepository(Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);
        mUserDao = db.userDao();
        mAllPhoneNumbers = mUserDao.getAllPhoneNumbers();
    }



    private class GameSaveT{
        String mPhoneNumber;
        String mGame;
        ArrayList<String> mGameProgress;

        private GameSaveT(@NonNull String userPhoneNumber, @NonNull String userGame, ArrayList<String> userGameProgress) {
            this.mPhoneNumber = userPhoneNumber;
            this.mGame = userGame;
            this.mGameProgress = userGameProgress;
        }
    }

    /*
       REPOSITORY COMMANDS BELOW
     */

    LiveData<List<String>> getAllPhoneNumbers() {
        return mAllPhoneNumbers;
    }

    public void insertUser(String userPhone, ArrayList<String> userPreviousGame){
        User userInfo = new User(userPhone,userPreviousGame);
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.insertUser(userInfo);
        });

    }

    public ArrayList<String> getUserPreviousGame(String userPhone){
        List<User> userList = mUserDao.getPreviousGames(userPhone);
        if (!userList.isEmpty()) {
            if (!userList.get(0).getPreviousGames().isEmpty()){
                return userList.get(0).getPreviousGames();
            }

        }
        return new ArrayList<>();

    }

    public void insertUserGame(String userPhone, String game, ArrayList<String> userGameProgress){
        GameSave userGameSave = new GameSave(userPhone,game,userGameProgress);
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.insertGame(userGameSave);
        });

    }

    public ArrayList<String> getUserGameProgress(String userPhone, String game){
        List<String> userGameProgress;
        List<GameSave> userGameSave = mUserDao.getProgress(userPhone,game);
        if (!userGameSave.isEmpty()) {
            if (!userGameSave.get(0).getGameProgress().isEmpty()){
                return userGameSave.get(0).getGameProgress();
            }else {
                Log.d("UserRepository", "Empty gameProgress for user " + userPhone
                + " in game " + game);
            }

        } else {
            Log.d("UserRepository", "Empty GameSave for user " + userPhone
                    + " in game " + game);
        }
        return new ArrayList<>();
    }

    public void deleteUserGameSave(String userPhone, String game){
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.deleteUserGame(userPhone,game);
        });

    }

    public void deleteUser(String userPhone){
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.deleteUser(userPhone);
        });

    }

    public void deleteAllUsers(){
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.deleteAllUsers();
        });

    }

    public void deleteAllGamesOfAllUsers(){
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.deleteAllUserGamesForAllUsers();
        });

    }

    public void deleteAllGamesOfUser(String userPhone){
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.deleteAllGamesOfUser(userPhone);
        });

    }


    public void deleteUserGame(String userPhone, String game){
        MyRoomDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.deleteUserGame(userPhone,game);
        });

    }
    /*
       REPOSITORY COMMANDS ABOVE
     */


}
