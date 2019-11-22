package com.example.android.movieappstage1.databaseUtils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.example.android.movieappstage1.MovieObject;

@Database(entities = {MovieObject.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static String DATABASE_NAME = "moviesdatabase";
    private static AppDatabase sInstance;

    //Singleton constructor restricts instantiation of database to one object.
    //Returns an instance of an AppDatabase.
    public static AppDatabase getInstance (Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    //Abstract method provides access to MovieDao and its associated SQL methods
    public abstract MovieDao movieDao ();
}
