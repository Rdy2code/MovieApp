package com.example.android.movieappstage1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MovieReviewAdapter extends
        RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {

    private static final String TAG = MovieReviewAdapter.class.getSimpleName();

    private List<MovieObject> mMovieReviews;
    private LayoutInflater mInflater;
    private Context mContext;

    public MovieReviewAdapter (Context context, List<MovieObject> movieReviews) {
        mContext = context;
        mMovieReviews = movieReviews;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MovieReviewAdapter.MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LinearLayout view = (LinearLayout) mInflater.inflate(R.layout.movie_review_item,
                parent, false);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewAdapter.MovieReviewViewHolder viewHolder, int position) {
        MovieObject movieReview = mMovieReviews.get(position);
        String author = movieReview.getAuthor();
        String content = movieReview.getContent();
        viewHolder.authorTV.setText(author);
        viewHolder.contentTV.setText(content);
    }

    @Override
    public int getItemCount() {
        if (mMovieReviews == null) {
            return 0;
        } else {
            return mMovieReviews.size();
        }
    }

    /** ViewHolder objects cache the views that are bound with data by the adapter */
    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        TextView authorTV;
        TextView contentTV;

        public MovieReviewViewHolder (LinearLayout itemView) {
            super(itemView);
            authorTV = (TextView) itemView.findViewById(R.id.author);
            contentTV = (TextView) itemView.findViewById(R.id.content);
        }

    }
}
