package com.example.android.movieappstage1;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.android.movieappstage1.utilities.JsonUtils;
import com.example.android.movieappstage1.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class LoadMovieViewModel extends AndroidViewModel {

    private final MutableLiveData<List<MovieObject>> movieObjects =
            new MutableLiveData<List<MovieObject>>();

    public LoadMovieViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<MovieObject>> getMovies(URL url) {
        loadData(url);
        return movieObjects;
    }

    @SuppressLint("StaticFieldLeak")
    private void loadData(URL url) {
        new AsyncTask<URL, Void, String>() {

            @Override
            protected String doInBackground(URL... urls) {
                if (urls.length == 0) {
                    return null;
                }

                URL movieRequestUrl = urls[0];
                String movieJson = "";

                try {
                    movieJson = NetworkUtils.getURLResponse(movieRequestUrl);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return movieJson;
            }

            @Override
            protected void onPostExecute(String movieJson) {
                if (movieJson != null && !movieJson.equals("")) {
                    movieObjects.setValue(JsonUtils.extractFeatureFromJson(movieJson));
                } else {
                    movieObjects.setValue(null);
                }
            }
        }.execute(url);
    }
}
