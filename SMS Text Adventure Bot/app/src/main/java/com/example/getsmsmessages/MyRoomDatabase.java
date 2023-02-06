package com.example.getsmsmessages;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/*
    MyRoomDatabase is used to contain the DAO and the SQL library

 */

@Database(entities = {GameSave.class,User.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class MyRoomDatabase extends RoomDatabase {


    public abstract UserDao userDao();

    public static MyRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    private static final String TAG = "MyRoomDatabase";
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //Used to get initialize the database if such is not found
    public static MyRoomDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (MyRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MyRoomDatabase.class, "userPlay_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //Neutered member of the class that can be used to repopulate the database after a reset
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                /*
                UserDao dao = INSTANCE.userDao();
                dao.deleteAllUserGamesForAllUsers();
                dao.deleteAllUsers();
                Log.d(TAG,"ALL DELETED");
                */
            });
        }
    };
}
