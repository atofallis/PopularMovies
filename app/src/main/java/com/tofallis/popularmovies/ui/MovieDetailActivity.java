package com.tofallis.popularmovies.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.tofallis.popularmovies.R;
import com.tofallis.popularmovies.data.FavouritesContract;
import com.tofallis.popularmovies.data.Movie;
import com.tofallis.popularmovies.data.Review;
import com.tofallis.popularmovies.data.Trailer;
import com.tofallis.popularmovies.data.TrailerAdapter;
import com.tofallis.popularmovies.network.AsyncTaskResult;
import com.tofallis.popularmovies.network.NetworkUtils;
import com.tofallis.popularmovies.network.ReviewListRequest;
import com.tofallis.popularmovies.network.TrailerListRequest;

import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.movieImage)
    ImageView mImageView;
    @BindView(R.id.toggleFavourite)
    ToggleButton mToggleFavourite;
    @BindView(R.id.original_title)
    TextView mOriginalTitle;
    @BindView(R.id.overview)
    TextView mOverview;
    @BindView(R.id.vote_average)
    TextView mVoteAverage;
    @BindView(R.id.release_date)
    TextView mReleaseDate;
    @BindView(R.id.reviews)
    TextView mReviews;
    @BindView(R.id.trailers)
    RecyclerView mTrailers;

    Movie mMovie;
    TrailerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        mMovie = new Movie(
                getIntent().getIntExtra(Movie.ID, 0),
                getIntent().getStringExtra(Movie.IMG_URL),
                getIntent().getStringExtra(Movie.TITLE),
                getIntent().getStringExtra(Movie.OVERVIEW),
                getIntent().getStringExtra(Movie.VOTE),
                getIntent().getStringExtra(Movie.RELEASE_DATE)
        );

        mAdapter = new TrailerAdapter(this);
        mTrailers.setAdapter(mAdapter);
        mTrailers.setLayoutManager(new LinearLayoutManager(this));

        // get current favourite state from Content Provider query
        checkFavouriteState();

        mToggleFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(FavouritesContract.FavouritesTable.COL_MOVIE_ID, mMovie.getId());
                contentValues.put(FavouritesContract.FavouritesTable.COL_IMG_URL, mMovie.getImageUrl());
                contentValues.put(FavouritesContract.FavouritesTable.COL_TITLE, mMovie.getOriginalTitle());
                contentValues.put(FavouritesContract.FavouritesTable.COL_OVERVIEW, mMovie.getOverview());
                contentValues.put(FavouritesContract.FavouritesTable.COL_VOTE, mMovie.getVoteAverage());
                contentValues.put(FavouritesContract.FavouritesTable.COL_RELEASE_DATE, mMovie.getReleaseDate());
                Uri uri;
                if (((ToggleButton) view).isChecked()) {
                    uri = getContentResolver().insert(FavouritesContract.CONTENT_URI, contentValues);
                    if (uri != null) {
                        Toast.makeText(getBaseContext(), "Added to favourites. ID:" + mMovie.getId(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    uri = FavouritesContract.CONTENT_URI.buildUpon().appendPath(Integer.toString(mMovie.getId())).build();
                    int deleted = getContentResolver().delete(uri, null, null);
                    if (deleted > 0) {
                        Toast.makeText(getBaseContext(), "Removed from favourites", Toast.LENGTH_LONG).show();
                    } else {
                        Log.e(this.getClass().getName(), "Failed to delete. result: " + deleted + " Uri: " + uri.toString());
                    }
                }
            }
        });

        Picasso.with(this).load(mMovie.getImageUrl()).into(mImageView);
        mOriginalTitle.setText(mMovie.getOriginalTitle() + "\n\n\n");
        mOverview.setText(mMovie.getOverview() + "\n\n\n");
        mVoteAverage.setText(mMovie.getVoteAverage() + "\n\n\n");
        mReleaseDate.setText(mMovie.getReleaseDate() + "\n\n\n");

        loadTrailerData();
        loadReviewData();
    }

    private void loadTrailerData() {
        URL url = NetworkUtils.getTrailers(mMovie.getId());
        new TrailerListRequest(new MovieDetailActivity.GetTrailerList()).execute(url);
    }

    private class GetTrailerList implements AsyncTaskResult<Trailer[]> {
        @Override
        public void onTaskCompleted(final Trailer[] result) {
            if (result != null) {
                mAdapter.setTrailers(Arrays.asList(result));
                mAdapter.notifyDataSetChanged();
            } else {
                Log.e(this.getClass().getName(), "No trailers available");
            }
        }
    }

    private void loadReviewData() {
        URL url = NetworkUtils.getReviews(mMovie.getId());
        new ReviewListRequest(new MovieDetailActivity.GetReviewList()).execute(url);
    }

    private class GetReviewList implements AsyncTaskResult<Review[]> {
        @Override
        public void onTaskCompleted(final Review[] result) {
            StringBuilder sb = new StringBuilder();
            sb.append("Reviews: ");
            if (result != null && result.length > 0) {
                for (Review review : result) {
                    sb.append("\n\n");
                    sb.append(review.getAuthor() + ":\n\n");
                    sb.append(review.getContent());
                }
            } else {
                sb.append("None");
                Log.d(this.getClass().getName(), "No reviews for this movie");
            }
            mReviews.setText(sb.toString());
        }
    }

    private void checkFavouriteState() {
        Uri uri = FavouritesContract.CONTENT_URI.buildUpon().appendPath(Integer.toString(mMovie.getId())).build();
        Cursor c = getContentResolver().query(uri,
                null,
                Integer.toString(mMovie.getId()),
                null,
                null);
        mToggleFavourite.setChecked(c != null && c.moveToNext());
        c.close();
    }
}
