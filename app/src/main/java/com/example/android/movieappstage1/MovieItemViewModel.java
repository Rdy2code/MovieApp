package com.example.android.movieappstage1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.android.movieappstage1.databaseUtils.AppDatabase;

public class MovieItemViewModel extends ViewModel {

    private LiveData<MovieObject> movie;


    public MovieItemViewModel(AppDatabase database, int movieId) {
        movie = database.movieDao().loadMovieById(movieId);
    }

    //Getter method
    public LiveData<MovieObject> getMovie() {
        return  movie;
    }
}
