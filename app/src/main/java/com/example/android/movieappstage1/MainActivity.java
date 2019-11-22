package com.example.android.movieappstage1;

//TODO: Save scroll state

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieappstage1.utilities.JsonUtils;
import com.example.android.movieappstage1.utilities.NetworkUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<String> {

    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private TextView mErrorMessageTextView;
    public static ProgressBar mProgressBar;
    public List<MovieObject> movieObjects;
    private int selectedItem = -1;
    private final static String USER_SELECTION = "selection";
    private GridLayoutManager layoutManager;
    private static final int MOVIE_SEARCH_LOADER = 22;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageTextView = (TextView) findViewById(R.id.error_message);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mRecyclerView = (RecyclerView) findViewById(R.id.movie_poster_iv);

        //TODO:Improve layout for landscape orientation

        layoutManager = new GridLayoutManager(this, 3);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MovieAdapter(this, movieObjects, this);

        mRecyclerView.setAdapter(mAdapter);

        //Launch the app with the most popular movies displayed. If user selects a category from the
        //dropdown menu, preserve that setting on device rotation by calling getUserSelection with
        //the menuItem ID
        if (savedInstanceState != null) {
            selectedItem = savedInstanceState.getInt(USER_SELECTION);
            getUserSelection (selectedItem);
        } else {
            fetchMovieData(NetworkUtils.MOST_POPULAR);
            setTitle(R.string.popular);
        }
    }

    private void showMovieData() {
        //Remove Error Message TextView
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        //Show the RecyclerView for movie data display
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        //Remove Error Message TextView
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        //Show the RecyclerView for movie data display
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void fetchMovieData(String userSelection) {
        URL movieRequestUrl = NetworkUtils.buildMovieUrl(userSelection);

        //Create Bundle to hold request URL for delivery to the AsyncTaskLoader
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, movieRequestUrl.toString());

        LoaderManager loaderManager = LoaderManager.getInstance(this);
        Loader<String> loader = loaderManager.getLoader(MOVIE_SEARCH_LOADER);
        if (loader == null) {
            loaderManager.initLoader(MOVIE_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_SEARCH_LOADER, queryBundle, this);
        }


        //new FetchMovieTask(this).execute(movieRequestUrl);
    }

    //Clicking on a movie poster image opens up the DetailActivity screen, which shows
    //information about the moview
    @Override
    public void onItemClick(int clickedItemIndex) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("MovieObject", movieObjects.get(clickedItemIndex));
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {

            String movieJsonResponse;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (bundle == null) {
                    return;
                }

                //To avoid duplicate loads, cache and deliver the result
                if (movieJsonResponse != null) {
                    deliverResult(movieJsonResponse);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(@Nullable String data) {
                movieJsonResponse = data;
                super.deliverResult(data);
            }

            @Nullable
            @Override
            public String loadInBackground() {
                String queryUrlString = bundle.getString(SEARCH_QUERY_URL_EXTRA);
                if (queryUrlString == null || TextUtils.isEmpty(queryUrlString)) {
                    return null;
                }
                try {
                    URL url = new URL(queryUrlString);
                    return NetworkUtils.getURLResponse(url);

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String movieJson) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (movieJson != null && !movieJson.equals("")) {
            showMovieData();
            movieObjects = JsonUtils.extractFeatureFromJson(movieJson);
            mAdapter.setMovieData(movieObjects);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    //Run the network request to the Movie Db server on a background thread using AsyncTask
    //To prevent memory leaks, make the inner AsyncTask class static.
//    private static class FetchMovieTask extends AsyncTask<URL, Void, String> {
//
//        //Use a weak reference to the Activity to gain access to the member variables and methods
//        private WeakReference<MainActivity> activityWeakReference;
//
//        //Retain only a weak reference to the Activity
//        FetchMovieTask(MainActivity context) {
//            activityWeakReference = new WeakReference<>(context);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mProgressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected String doInBackground(URL... Urls) {
//            if (Urls.length == 0) {
//                return null;
//            }
//
//            URL movieRequestUrl = Urls[0];
//            String movieJson = "";
//
//            try {
//                movieJson = NetworkUtils.getURLResponse(movieRequestUrl);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return movieJson;
//        }
//
//        @Override
//        protected void onPostExecute(String movieJson) {
//
//            MainActivity activity = activityWeakReference.get();
//            if (activity == null || activity.isFinishing()) {
//                return;
//            }
//
//            mProgressBar.setVisibility(View.INVISIBLE);
//            if (movieJson != null && !movieJson.equals("")) {
//                activity.showMovieData();
//                activity.movieObjects = JsonUtils.extractFeatureFromJson(movieJson);
//                activity.mAdapter.setMovieData(activity.movieObjects);
//            } else {
//                activity.showErrorMessage();
//            }
//        }
//    }

    //Create menu with options for selecting lists of movies
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    //MainActivity overflow menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_by_popularity:
                selectedItem = 1;
                Toast.makeText(this, "Most popular selected", Toast.LENGTH_SHORT).show();
                setTitle(R.string.popular);
                fetchMovieData(NetworkUtils.MOST_POPULAR);
                break;

            case R.id.sort_by_rating:
                selectedItem = 2;
                Toast.makeText(this, "Top rated selected", Toast.LENGTH_SHORT).show();
                setTitle(R.string.rating);
                fetchMovieData(NetworkUtils.TOP_RATED);
                break;

            case R.id.favorites:
                selectedItem = 3;
                Toast.makeText(this, "Favorites selected", Toast.LENGTH_SHORT).show();
                setTitle(getString(R.string.favorites));
                //Read list of Favorites from database
                queryDatabase();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(USER_SELECTION, selectedItem);
    }

    //Helper method that that maintains the user-selected movie list on device rotation.
    private void getUserSelection (int userSelection) {

        switch (userSelection) {
            case 1:
                setTitle(R.string.popular);
                fetchMovieData(NetworkUtils.MOST_POPULAR);
                break;

            case 2:
                setTitle(R.string.rating);
                fetchMovieData(NetworkUtils.TOP_RATED);
                break;

            case 3:
                setTitle(getString(R.string.favorites));
                queryDatabase();
                break;
        }
    }

    //Retrieve the list of Favorite movies from the database and populate the UI
    private void queryDatabase () {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieObject>>() {
            @Override
            public void onChanged(@Nullable List<MovieObject> movies) {
                movieObjects = movies;
                mAdapter.setMovieData(movieObjects);
            }
        });
    }
}