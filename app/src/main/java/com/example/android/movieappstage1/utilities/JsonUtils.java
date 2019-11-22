package com.example.android.movieappstage1.utilities;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.movieappstage1.MovieObject;
import com.example.android.movieappstage1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    //Constant for logging messages for debugging purposes
    private static final String LOG_TAG = JsonUtils.class.getName();

    //Following best practices: Define constant strings as separate variables
    private static final String JSON_ARRAY = "results";
    private static final String JSON_VOTE_AVERAGE = "vote_average";
    private static final String JSON_TITLE = "title";
    private static final String JSON_POSTER_PATH = "poster_path";
    private static final String JSON_OVERVIEW = "overview";
    private static final String JSON_RELEASE_DATE = "release_date";
    private static final String JSON_MOVIE_ID = "id";
    private static final String YOUTUBE_MOVIE_KEY = "key";
    private static final String REVIEW_AUTHOR = "author";
    private static final String MOVIE_REVIEW = "content";
    public static String mtrailerKey;

    //private constructor ensures no instances created outside this class
    private JsonUtils() {
    }

    /**Parse the JSON response from the Movie db server, storing extracted values in variables, and
     * variable:value pairs in custom Movie objects.*/
    public static List<MovieObject> extractFeatureFromJson (String movieJson) {

        //If JSON string is empty or null, return early
        if (TextUtils.isEmpty(movieJson)) {
            return null;
        }

        //Create a dynamic list of Movies
        ArrayList<MovieObject> movies = new ArrayList<MovieObject>();

        try {
            //Construct a JSONObject instance that contains the entire base JSON string
            JSONObject baseJSONObject = new JSONObject(movieJson);

            //Extract the 'results' array
            JSONArray JSONArray = baseJSONObject.optJSONArray(JSON_ARRAY);

            //Iterate the array to extract features relevant to the UI display
            for (int i = 0; i < JSONArray.length(); i++) {

                JSONObject jsonObject = JSONArray.getJSONObject(i);

                //Get values for movie features
                int movieId = jsonObject.optInt(JSON_MOVIE_ID);
                Double voteAverage = jsonObject.optDouble(JSON_VOTE_AVERAGE);
                String title = jsonObject.optString(JSON_TITLE);
                String posterPath = jsonObject.optString(JSON_POSTER_PATH);
                String overView = jsonObject.optString(JSON_OVERVIEW);
                String releaseDate = convertDate(jsonObject.optString(JSON_RELEASE_DATE));

                //Add variable:value pairs to a MovieObject and store that object in movies
                //ArrayList
                movies.add(new MovieObject (movieId, voteAverage, title, posterPath, overView, releaseDate));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Error", e);
        }

        //Return the ArrayList of Movie objects for individual movies and their features
        return movies;
    }

    public static String extractYouTubeId (String videoJson) {
        //If JSON string is empty or null, return early
        if (TextUtils.isEmpty(videoJson)) {
            return null;
        }

        try {
            //Construct a JSONObject instance that contains the entire base JSON string
            JSONObject baseJSONObject = new JSONObject(videoJson);

            //Extract the 'results' array from the JSON
            JSONArray JSONArray = baseJSONObject.optJSONArray(JSON_ARRAY);

            //Get the first trailer in the list of returned results
            JSONObject firstTrailer = JSONArray.getJSONObject(0);

            //Get YouTube trailer key from the first trailer
            mtrailerKey = firstTrailer.getString(YOUTUBE_MOVIE_KEY);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Error", e);
        }

        //Return the ArrayList of Movie objects for individual movies and their features
        return mtrailerKey;
    }

    /**This method called when the user clicks on a movie and navigates to the DetailActivity screen */
    public static List<MovieObject> extractMovieReviews (String reviewsJson) {

        //Create a dynamic list of Movie Reviews
        ArrayList<MovieObject> reviews = new ArrayList<MovieObject>();

        //Variables for storing author and content
        String author = "";
        String content = "";

        if (TextUtils.isEmpty(reviewsJson)) {
            return null;
        }

        try {
            //Construct a JSONObject instance that contains the entire base JSON reviews string
            JSONObject baseJSONObject = new JSONObject(reviewsJson);

            //Extract the 'results' array from the JSON
            JSONArray JSONArray = baseJSONObject.optJSONArray(JSON_ARRAY);

            if (JSONArray.length() == 0) {
                author = "Not available";
                content = "Not available";
                reviews.add(new MovieObject(author, content));
            }

            for (int i = 0; i < JSONArray.length(); i++) {

                JSONObject movieReview = JSONArray.getJSONObject(i);
                author = movieReview.optString(REVIEW_AUTHOR);
                content = movieReview.optString(MOVIE_REVIEW);

                //Add variable:value pairs to a ReviewObject and store that object in reviews
                //ArrayList
                reviews.add(new MovieObject (author, content));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Error", e);
        }

        return reviews;
    }

    //Takes the ISO-8601 release date and converts it to a date in the form MM, DD, YYYY
    private static String convertDate(String dtstr) {

        //Extract the portion of the ISO-8601 date that corresponds to the year, month, and day
        String dtstrSplit = dtstr.substring(0, 10);

        //Remove the hyphen and store the year, month, and day in that order in an array
        String [] arrayString = dtstrSplit.split("-");

        //Store the month as an integer for input into the switch block below
        int month = Integer.parseInt(arrayString[1]);

        //Create an empty string in which to store the text returned by the switch block
        String abbMonth = "";

        //Convert the integer month to a three-letter abbreviation for the month
        switch (month) {
            case 0:
            case 1:
                abbMonth = "Jan";
                break;
            case 2:
                abbMonth = "Feb";
                break;
            case 3:
                abbMonth = "Mar";
                break;
            case 4:
                abbMonth = "Apr";
                break;
            case 5:
                abbMonth = "May";
                break;
            case 6:
                abbMonth = "Jun";
                break;
            case 7:
                abbMonth = "Jul";
                break;
            case 8:
                abbMonth = "Aug";
                break;
            case 9:
                abbMonth = "Sep";
                break;

            case 10:
                abbMonth = "Oct";
                break;

            case 11:
                abbMonth = "Nov";
                break;

            case 12:
                abbMonth = "Dec";
                break;
            default:
                break;
        }

        //Format the date, by rearranging the elements in the string with the output from the
        //switch statement
        String dateToDisplay = abbMonth + " " + arrayString[2] + ", " + arrayString[0];
        return dateToDisplay;
    }
}
