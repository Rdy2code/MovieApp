package com.example.android.movieappstage1;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.movieappstage1.databaseUtils.AppDatabase;

public class MovieViewModelFactory extends ViewModelProvider.NewInstanceFactory  {

    private AppDatabase mDb;
    private final int mMovieId;

    //Constructor
    public MovieViewModelFactory(AppDatabase database, int movieId) {
        mDb = database;
        mMovieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MovieItemViewModel (mDb, mMovieId);
    }
}
