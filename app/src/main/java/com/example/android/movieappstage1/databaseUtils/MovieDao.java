package com.example.android.movieappstage1.databaseUtils;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.android.movieappstage1.MovieObject;

import java.util.List;

//Define MovieObject database methods. Update method is not needed. Entity(table) defined in
//MovieObject.java
@Dao
public interface MovieDao {

    @Query ("SELECT * FROM movie")
    LiveData<List<MovieObject>> loadAllMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie (MovieObject movie);

    @Delete
    void deleteMovie (MovieObject movie);

    //Query database to retrieve the information for a single movie
    @Query("SELECT * FROM movie WHERE mMovieId = :movieId")
    LiveData<MovieObject> loadMovieById (int movieId);
}
