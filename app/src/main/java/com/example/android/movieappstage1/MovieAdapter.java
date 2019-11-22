package com.example.android.movieappstage1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.movieappstage1.utilities.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

//MovieAdapter class manages ViewHolder objects and binds these objects to their data
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    final private ListItemClickListener mOnClickListener;

    //Member variables
    public List<MovieObject> mMovieData;
    private LayoutInflater mInflater;
    private Context mContext;

    //Nested interface for binding listeners to poster image items on the Main UI
    public interface ListItemClickListener {
        void onItemClick (int clickedItemIndex);
    }

    public MovieAdapter (Context context, List<MovieObject> movieData, ListItemClickListener listener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mMovieData = movieData;
        mOnClickListener = listener;
    }

    //Inflate the views that go inside the ViewHolder objects
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item, parent, false);
        return new MovieViewHolder(view);
    }

    //Bind the MovieObject poster image to the view inside the ViewHolder object
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        MovieObject movie = mMovieData.get(position);
        URL moviePosterUrl = NetworkUtils.buildImageUrl(movie.getMoviePoster());

        Picasso.with(mContext)
            .load(moviePosterUrl.toString())
            .error(R.mipmap.ic_launcher)
            .into(holder.mImageItemView, new Callback() {

                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Toast.makeText(mContext, R.string.error_message, Toast.LENGTH_LONG);
                }
            });
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null) {
            return 0;
        } else {
            return mMovieData.size();
        }
    }

    public List<MovieObject> getMovies() {
        return mMovieData;
    }

    //Notify the adapter when new movie data is available and update the list of movies accordingly
    public void setMovieData (List<MovieObject> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    //Inner ViewHolder class. ViewHolder objects currently hold references to an image view
    //for displaying movie posters
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageItemView;

        public MovieViewHolder (View itemView) {
            super(itemView);
            mImageItemView = (ImageView) itemView.findViewById(R.id.image_item);
            //Attach a listener to the ImageView so it will be responsive to user clicks
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Get the position of the item in the list
            int clickedPosition = getAdapterPosition();
            //Pass the item position to the OnClickListener of the other class
            mOnClickListener.onItemClick(clickedPosition);
        }
    }
}
