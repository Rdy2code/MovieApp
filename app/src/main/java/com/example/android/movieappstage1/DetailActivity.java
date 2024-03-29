package com.example.android.movieappstage1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movieappstage1.databaseUtils.AppDatabase;
import com.example.android.movieappstage1.utilities.JsonUtils;
import com.example.android.movieappstage1.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    //Declare variables
    private ImageView mPoster;
    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private TextView mOverview;
    private int mMovieId;
    private String mYoutubeKey;
    private List<MovieObject> mMovieReviewsObjectList;
    private MovieReviewAdapter mReviewsAdapter;
    private RecyclerView mRecyclerView;
    private MovieObject mMovieObject;
    private AppDatabase mDb;

    //Current state of the Favorite Button
    private int mCurrentState;
    //Button Favorite mode
    private final int FAVORITE = 0;
    //Button Unfavorite mode
    private final int UNFAVORITE = 1;

    //Variable for storing a reference to the Favorite Button
    private Button mFavoriteButton;

    // Extra for the Movie ID to be received in the intent
    public static final String STATUS = "favoriteStatus";

    //LiveData instance variable
    MovieTrailerViewModel movieTrailerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movieTrailerViewModel = ViewModelProviders.of(this)
                .get(MovieTrailerViewModel.class);

        mPoster = (ImageView) findViewById(R.id.detail_image_view);
        mTitle = (TextView) findViewById(R.id.movie_title);
        mReleaseDate = (TextView) findViewById(R.id.release_date);
        mVoteAverage = (TextView) findViewById(R.id.vote_average);
        mOverview = (TextView) findViewById(R.id.overview);
        mRecyclerView = (RecyclerView) findViewById(R.id.movieReviewRecyclerView);
        mFavoriteButton = (Button) findViewById(R.id.mark_as_favorite);

        //Initialize the AppDatabase
        mDb = AppDatabase.getInstance(getApplicationContext());

        //Retrieve and instantiate the intent that started the detail activity
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra("MovieObject")) {

            //Unpack the parcel and extract field values from the object mailed with this intent
            mMovieObject = intentThatStartedThisActivity.getParcelableExtra("MovieObject");

            int movieId = mMovieObject.getMovieId();
            Double voteAverage = mMovieObject.getVoteAverage();
            String title = mMovieObject.getTitle();
            String poster = mMovieObject.getMoviePoster();
            String overview = mMovieObject.getOverview();
            String releaseDate = mMovieObject.getReleaseDate();

            URL moviePosterUrl = NetworkUtils.buildImageUrl(poster);
            Picasso.with(this)
                    .load(moviePosterUrl.toString())
                    .into(mPoster);

            mTitle.setText(title);

            mReleaseDate.setText(releaseDate);

            mVoteAverage.setText(voteAverage.toString());

            mOverview.setText(overview);

            //Store the movieId in the member variable
            mMovieId = movieId;

            //Query the database to see if the movie is already a favorite
            queryDatabaseById(movieId);

            //Initialize the LayoutManager for the RecyclerView nested inside the activity_detail layout
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);

            //Attach the LayoutManager to the RecyclerView
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);

            //Load movie reviews into UI
            movieTrailerViewModel.getMovieReview(mMovieId).observe(
                this, new Observer<List<MovieObject>>() {
                    @Override
                    public void onChanged(@Nullable List<MovieObject> movieObjects) {
                        if (movieObjects != null) {
                            //Initialize the MovieReviewAdapter with the List<MovieObject> author and content data
                            mReviewsAdapter = new MovieReviewAdapter(getApplicationContext(), movieObjects);

                            //Attach the adapter to the RecyclerView
                            mRecyclerView.setAdapter(mReviewsAdapter);
                        }
                    }
                }
            );
        }
    }

    /**
     * This method triggered when the user clicks on "Play Trailer" button in DetailActivity UI
     * Fetch the Video Trailers JSON from the TheMovieDatabase on a background thread, extract the
     * YouTube key(s),and fetch the video from YouTube
     */
    public void playTrailer(View view) {

        URL videoRequestUrl = NetworkUtils.buildTrailerUrl(mMovieId);

        movieTrailerViewModel.getMovieTrailer(videoRequestUrl).observe(
            this, new Observer<Uri>() {
                @Override
                public void onChanged(@Nullable Uri uri) {
                    if (uri != null) {
                        Intent videoIntent = new Intent(Intent.ACTION_VIEW, uri);
                        // Verify that the intent will resolve to an activity
                        if (videoIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(videoIntent);
                        }
                    }
                }
            }
        );
    }

    //Determine if the movie is in the database and set the button text accordingly
    public void queryDatabaseById(final int movieId) {
        MovieViewModelFactory factory = new MovieViewModelFactory(mDb, movieId);

        final MovieItemViewModel viewModel
                = ViewModelProviders.of(this, factory).get(MovieItemViewModel.class);

        viewModel.getMovie().observe(this, new Observer<MovieObject>() {
            @Override
            public void onChanged(@Nullable MovieObject movieObject) {
                try {
                    if (movieObject.getMovieId() == mMovieId) {
                        mCurrentState = 1;
                        mFavoriteButton.setText(getString(R.string.show_unfavorite_text));
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Respond to Favorite/Unfavorite button click in DetailActivity interface
    public void onButtonClick(View view) {
        switch (mCurrentState) {
            case FAVORITE:
                addToFavorites();
                break;
            case UNFAVORITE:
                deleteFromFavorites();
                break;
        }
    }

    public void addToFavorites() {

        //Change button text
        mFavoriteButton.setText(getString(R.string.show_unfavorite_text));

        //Change favorite status
        mCurrentState = UNFAVORITE;

        //Get an instance of the single thread AppExecutor to handle the call to the database
        //off the main thread
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                mDb.movieDao().insertMovie(mMovieObject);
            }
        });
    }

    public void deleteFromFavorites() {

        //Change button text
        mFavoriteButton.setText(getString(R.string.show_favorite_text));

        //Change favorite status
        mCurrentState = FAVORITE;

        //Get an instance of the single thread AppExecutor to handle the call to the database
        //off the main thread
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //int position = movieViewHolder.getAdapterPosition();
                //List<MovieObject> movie = mMovieAdapter.getMovies();
                mDb.movieDao().deleteMovie(mMovieObject);
            }
        });
    }
}
