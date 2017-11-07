package com.tofallis.popularmovies.data;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tofallis.popularmovies.R;
import com.tofallis.popularmovies.ui.MovieDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Context mContext;

    public List<Movie> getMovies() {
        return mMovies;
    }

    public void setMovies(List<Movie> movies) {
        mMovies = movies;
    }

    // store list of Movies from the api query.
    private List<Movie> mMovies = new ArrayList<>();

    public MovieAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.movie_poster, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder holder, final int position) {

        if (holder.mPoster == null) {
            // If the view is not recycled, this creates a new ImageView to hold an image
            holder.mPoster = new ImageView(mContext);
            holder.mPoster.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        String url = mMovies.get(position).getImageUrl();
        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.ic_image_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(holder.mPoster);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    /**
     * Creates a new ImageView for each item referenced by the adapter
     */

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mPoster;

        public ViewHolder(View itemView) {
            super(itemView);
            mPoster = itemView.findViewById(R.id.moviePoster);
            mPoster.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Movie m = getMovies().get(position);
                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                intent.putExtra(Movie.IMG_URL, m.getImageUrl());
                intent.putExtra(Movie.TITLE, m.getOriginalTitle());
                intent.putExtra(Movie.OVERVIEW, m.getOverview());
                intent.putExtra(Movie.RELEASE_DATE, m.getReleaseDate());
                intent.putExtra(Movie.VOTE, m.getVoteAverage());
                mContext.startActivity(intent);
            }
        }
    }
}
