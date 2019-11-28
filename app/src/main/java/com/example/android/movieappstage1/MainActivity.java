package com.example.android.movieappstage1;

//TODO: Save scroll state

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieappstage1.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.ListItemClickListener {

    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private TextView mErrorMessageTextView;
    public ProgressBar mProgressBar;
    public List<MovieObject> mMovieObjects;
    private int selectedItem = 1;
    private final static String USER_SELECTION = "selection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageTextView = findViewById(R.id.error_message);

        mProgressBar = findViewById(R.id.progress_bar);

        mRecyclerView = findViewById(R.id.movie_poster_iv);

        //TODO:Improve layout for landscape orientation

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MovieAdapter(this, mMovieObjects, this);

        mRecyclerView.setAdapter(mAdapter);

        //Launch the app with the most popular movies displayed. If user selects a category from the
        //dropdown menu, preserve that setting on device rotation by calling getUserSelection with
        //the menuItem ID
        if (savedInstanceState != null) {
            selectedItem = savedInstanceState.getInt(USER_SELECTION);
            getUserSelection (selectedItem);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
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

    //Show the list of movies corresponding to the user's selection
    private void fetchMovieData(String userSelection) {
        URL movieRequestUrl = NetworkUtils.buildMovieUrl(userSelection);

        mProgressBar.setVisibility(View.VISIBLE);

        //Encapsulate the data from the network call inside a ViewModel, eliminating the need
        //for a Loader, and preserving data during configuration change
        LoadMovieViewModel loadMovieViewModel = ViewModelProviders.of(this)
                .get(LoadMovieViewModel.class);
        loadMovieViewModel.getMovies(movieRequestUrl).observe(this, new Observer<List<MovieObject>>() {
            @Override
            public void onChanged(@Nullable List<MovieObject> movieObjects) {
                if (movieObjects != null) {
                    mMovieObjects = movieObjects;
                    mAdapter.setMovieData(mMovieObjects);
                    showMovieData();
                } else {
                    showErrorMessage();
                }
            }
        });

        mProgressBar.setVisibility(View.INVISIBLE);
    }

    //Clicking on a movie poster image opens up the DetailActivity screen, which shows
    //information about the moview
    @Override
    public void onItemClick(int clickedItemIndex) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("MovieObject", mMovieObjects.get(clickedItemIndex));
        startActivity(intent);
    }

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
                mMovieObjects = movies;
                mAdapter.setMovieData(mMovieObjects);
            }
        });
    }
}