package com.example.android.movieappstage1;

//Custom class for defining objects that hold information about movies retrieved from the Movie
//database.

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity (tableName = "movie")
public class MovieObject implements Parcelable {

    //Define global variables for fields the object will hold
    @PrimaryKey
    private int mMovieId;
    @ColumnInfo
    private Double mVoteAverage;
    @ColumnInfo
    private String mTitle;
    @ColumnInfo
    private String mMoviePoster;
    @ColumnInfo
    private String mOverview;
    @ColumnInfo
    private String mReleaseDate;
    @ColumnInfo
    private String mAuthor;
    @ColumnInfo
    private String mContent;

    //Constructor
    public MovieObject (int movieId,
                        Double voteAverage,
                        String title,
                        String moviePoster,
                        String overview,
                        String releaseDate,
                        String author,
                        String content) {

        //Initialize the member variables to the values passed into the constructor
        mMovieId = movieId;
        mVoteAverage = voteAverage;
        mTitle = title;
        mMoviePoster = moviePoster;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mAuthor = author;
        mContent = content;
    }

    @Ignore
    //Constructor
    public MovieObject (int movieId,
                        Double voteAverage,
                        String title,
                        String moviePoster,
                        String overview,
                        String releaseDate) {

        //Initialize the member variables to the values passed into the constructor
        mMovieId = movieId;
        mVoteAverage = voteAverage;
        mTitle = title;
        mMoviePoster = moviePoster;
        mOverview = overview;
        mReleaseDate = releaseDate;
    }

    @Ignore
    //Constructor
    public MovieObject (String author, String content) {

        //Initialize the member variables to the values passed into the constructor
        mAuthor = author;
        mContent = content;
    }

    //Create public 'getter' methods so other classes can access information about a Movie
    //object instance

    /** Get the offical Movie ID */
    public int getMovieId () {
        return mMovieId;
    }

    /** Get the movie vote_average */
    public Double getVoteAverage () {
        return mVoteAverage;
    }

    /** Get the movie title */
    public String getTitle () {
        return mTitle;
    }

    /** Get the movie poster_path */
    public String getMoviePoster() {
        return mMoviePoster;
    }

    /** Get the movie overview */
    public String getOverview () {
        return mOverview;
    }

    /** Get the movie release_date */
    public String getReleaseDate () {
        return mReleaseDate;
    }

    /** Get the movie review author */
    public String getAuthor () {
        return mAuthor;
    }

    /** Get the movie review */
    public String getContent () {
        return mContent;
    }


    //Copy MovieObject into a parcel for transmission from one activity to another

    //Write object values to parcel for storage
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mMovieId);
        parcel.writeDouble(mVoteAverage);
        parcel.writeString(mTitle);
        parcel.writeString(mMoviePoster);
        parcel.writeString(mOverview);
        parcel.writeString(mReleaseDate);

        //Add Movie review content
        parcel.writeString(mAuthor);
        parcel.writeString(mContent);
    }

    //Constructor that is called by the receiving activity where values are retrieved
    public MovieObject (Parcel parcel) {
        mMovieId = parcel.readInt();
        mVoteAverage = parcel.readDouble();
        mTitle = parcel.readString();
        mMoviePoster = parcel.readString();
        mOverview = parcel.readString();
        mReleaseDate = parcel.readString();
        mAuthor = parcel.readString();
        mContent = parcel.readString();
    }

    //Bind the parcel together
    public static final Parcelable.Creator<MovieObject> CREATOR =
            new Parcelable.Creator<MovieObject>() {

                @Override
                public MovieObject createFromParcel(Parcel parcel) {
                    return new MovieObject(parcel);
                }

                @Override
                public MovieObject[] newArray(int i) {
                    return new MovieObject[i];
                }
            };

    @Override
    public int describeContents() {
        return hashCode();
    }
}
