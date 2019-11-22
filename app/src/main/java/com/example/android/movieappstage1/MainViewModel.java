package com.example.android.movieappstage1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.android.movieappstage1.databaseUtils.AppDatabase;

import java.util.List;

/** ViewModel class to cache the list of LiveData<MovieObjects> */
public class MainViewModel extends AndroidViewModel {

    private LiveData<List<MovieObject>> movies;

    //Constructor
    public MainViewModel (Application application) {
        super(application);
        //Initialize the member variable
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        movies = database.movieDao().loadAllMovies();
    }

    //Getter methods
    public LiveData<List<MovieObject>> getMovies() {
        return movies;
    }
}
