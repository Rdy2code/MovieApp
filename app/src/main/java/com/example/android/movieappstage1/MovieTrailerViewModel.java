package com.example.android.movieappstage1;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.android.movieappstage1.utilities.JsonUtils;
import com.example.android.movieappstage1.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MovieTrailerViewModel extends AndroidViewModel {

    private final MutableLiveData<Uri> mYoutubeKey =
            new MutableLiveData<Uri>();

    private final MutableLiveData<List<MovieObject>> mMovieReviewsObjectList =
            new MutableLiveData<List<MovieObject>>();

    public MovieTrailerViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Uri> getMovieTrailer (URL url) {
        loadData(url);
        return mYoutubeKey;
    }

    public LiveData<List<MovieObject>> getMovieReview(Integer movieIdNumber) {
        loadMovieReviews(movieIdNumber);
        return mMovieReviewsObjectList;
    }

    @SuppressLint("StaticFieldLeak")
    private void loadData(URL url) {
        new AsyncTask<URL, Void, String>() {

            @Override
            protected String doInBackground(URL... Urls) {
                if (Urls.length == 0) {
                    return null;
                }

                URL videoUrl = Urls[0];
                String videoJson = "";

                try {
                    videoJson = NetworkUtils.getURLResponse(videoUrl);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return videoJson;
            }

            @Override
            protected void onPostExecute(String json) {
                if (json != null && !json.equals("")) {
                    String youtubeKey = JsonUtils.extractYouTubeId(json);
                    Uri builtUri = Uri.parse(NetworkUtils.YOUTUBE_API_BASE_URL).buildUpon()
                            .appendEncodedPath(String.valueOf(youtubeKey))
                            .build();
                    mYoutubeKey.setValue(builtUri);
                }
            }
        }.execute(url);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadMovieReviews (Integer movieNumber) {
        new AsyncTask<Integer, Void, String>() {

            @Override
            protected String doInBackground(Integer... movieIds) {

                //Build the URL that will fetch the reviews JSON
                String reviewsJson = "";
                URL reviewsUrl = NetworkUtils.buildReviewsUrl(movieIds[0]);

                try {
                    reviewsJson = NetworkUtils.getURLResponse(reviewsUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return reviewsJson;
            }

            @Override
            protected void onPostExecute(String reviewsJson) {

                if (reviewsJson != null && !reviewsJson.equals("")){
                    //Initialize the List<MovieObject> to the Movie Object holding the author and content fields
                    mMovieReviewsObjectList.setValue(JsonUtils.extractMovieReviews(reviewsJson));
                }
            }
        }.execute(movieNumber);
    }
}
