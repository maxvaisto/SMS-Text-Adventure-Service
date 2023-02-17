package com.example.getsmsmessages;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

//UserDao is used to connect the SQL databases with the application repository
//This interface maps the repository commands to corresponding SQL commands
@Dao
public interface UserDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User... users);

    //USER PART
    @Query("SELECT DISTINCT phoneNumber FROM user_table")
    LiveData<List<String>> getAllPhoneNumbers();

    @Query("SELECT * FROM user_table WHERE phoneNumber = :userPhone")
    List<User> getPreviousGames(String userPhone);

    @Query("DELETE FROM user_table WHERE phoneNumber = :userPhone")
    void deleteUser(String userPhone);

    /*
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUsers(User ... users);
    */
    @Query("DELETE FROM user_table")
    void deleteAllUsers();

    /*
    //
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUserGames(GameSave... userGames);
    */
    @Query("DELETE FROM gameSaves_table WHERE phoneNumber = :userPhone AND game = :userGame")
    void deleteUserGame(String userPhone, String userGame);

    @Query("DELETE FROM gameSaves_table WHERE phoneNumber = :userPhone")
    void deleteAllGamesOfUser(String userPhone);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGame(GameSave... userGames);

    @Query("SELECT * FROM gameSaves_table WHERE " +
            "phoneNumber = :userPhone AND game = :userGame")
    List<GameSave> getProgress(String userPhone, String userGame);


    @Query("DELETE FROM gameSaves_table")
    void deleteAllUserGamesForAllUsers();
}
