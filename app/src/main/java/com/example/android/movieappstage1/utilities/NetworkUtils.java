package com.example.android.movieappstage1.utilities;

import android.net.Uri;

import com.example.android.movieappstage1.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Contains helper methods for communicating with the Movie db API
 */

public class NetworkUtils {

    //URL String constants for building URL query to Movie API
    private final static String MOVIE_API_BASE_URL = "https://api.themoviedb.org/3/movie";
    private final static String IMAGE_API_BASE_URL = "https://image.tmdb.org/t/p/w185";
    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    private final static String API_PARAM = "api_key";
    private final static String MOVIE_TRAILER = "videos";
    private final static String MOVIE_REVIEWS = "reviews";
    public final static String YOUTUBE_API_BASE_URL = "http://www.youtube.com/watch?v=";


    //TODO:Insert your theMovieDB API Key here
    private final static String API_KEY = BuildConfig.KEY;

    private NetworkUtils() {
    }

    /**
     * This method builds the URL used to fetch the base JSON object that contains movie information,
     * including links to movie posters.
     */
    public static URL buildMovieUrl (String userSelection) {

        //Construct a URI request that will be accepted by the Movie Database (TMDb)
        //The userSelection for the path will be chosen from the drop down menu in the Main Activity
        //Current choices are most popular and top rated
        Uri builtUri = Uri.parse (MOVIE_API_BASE_URL).buildUpon()
                .appendPath(userSelection)
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        URL url = null;

        //Convert the URI to a URL string
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    /**Build a Url for fetching JSON with information for obtaining the Movie trailer Video in YouTube*/
    public static URL buildTrailerUrl (int movieID) {

        //Construct a URI request that will be accepted by the Movie Database (TMDb)
        Uri builtUri = Uri.parse (MOVIE_API_BASE_URL).buildUpon()
                .appendEncodedPath(String.valueOf(movieID))
                .appendPath(MOVIE_TRAILER)
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        URL url = null;

        //Convert the URI to a URL string
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**Build movie reviews URL*/
    public static URL buildReviewsUrl (int movieID) {

        //Construct a URI request that will be accepted by the Movie Database (TMDb)
        Uri builtUri = Uri.parse (MOVIE_API_BASE_URL).buildUpon()
                .appendEncodedPath(String.valueOf(movieID))
                .appendPath(MOVIE_REVIEWS)
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        URL url = null;

        //Convert the URI to a URL string
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method takes as input the relative path to the movie poster and appends it to the
     * base Url to build a complete Url request for a movie poster.
     */
    public static URL buildImageUrl (String imageID) {

        //Construct a URI request that will be accepted by the Movie Database (TMDb)
        //The userSelection for the path will be chosen from the drop down menu in the Main Activity
        //Current choices are most popular and top rated
        Uri builtUri = Uri.parse (IMAGE_API_BASE_URL).buildUpon()
                .appendEncodedPath(imageID)
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        URL url = null;

        //Convert the URI to a URL string
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /** This method returns the complete http response from the Movie Db server */
    public static String getURLResponse (URL url) throws IOException {

        //Return early with empty string if URL is empty
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
