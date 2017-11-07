package com.tofallis.popularmovies.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.tofallis.popularmovies.data.Trailer;
import com.tofallis.popularmovies.network.AsyncTaskResult;
import com.tofallis.popularmovies.network.NetworkUtils;
import com.tofallis.popularmovies.network.TrailerListRequest;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.movieImage)
    ImageView mImageView;
    @BindView(R.id.trailer)
    ImageView mTrailer;
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

    int mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        mMovieId = getIntent().getIntExtra(Movie.ID, 0);

        mToggleFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(FavouritesContract.FavouritesTable.COL_MOVIE_ID, mMovieId);
                Uri uri;
                if (((ToggleButton)view).isChecked()) {
                    uri = getContentResolver().insert(FavouritesContract.CONTENT_URI, contentValues);
                    if(uri != null) {
                        Toast.makeText(getBaseContext(), "Added to favourites. ID:" + mMovieId, Toast.LENGTH_LONG).show();
                    }
                } else {
                    uri = FavouritesContract.CONTENT_URI.buildUpon().appendPath(Integer.toString(mMovieId)).build();
                    int deleted = getContentResolver().delete(uri, null, null);
                    if(deleted > 0) {
                        Toast.makeText(getBaseContext(), "Removed from favourites", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        Picasso.with(this).load(getIntent().getStringExtra(Movie.IMG_URL)).into(mImageView);
        mOriginalTitle.setText(getIntent().getStringExtra(Movie.TITLE) + "\n\n\n");
        mOverview.setText(getIntent().getStringExtra(Movie.OVERVIEW) + "\n\n\n");
        mVoteAverage.setText(getIntent().getStringExtra(Movie.VOTE) + "\n\n\n");
        mReleaseDate.setText(getIntent().getStringExtra(Movie.RELEASE_DATE) + "\n\n\n");

        loadTrailerData();
    }

    private void loadTrailerData() {
        URL url = NetworkUtils.getTrailers(mMovieId);
        new TrailerListRequest(this, new MovieDetailActivity.GetTrailerList()).execute(url);
    }

    private class GetTrailerList implements AsyncTaskResult<Trailer[]>
    {
        @Override
        public void onTaskCompleted(final Trailer[] result) {
            if(result != null) {
                mTrailer.setVisibility(View.VISIBLE);
                mTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // play the first trailer
                        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://youtube.com/watch?v=" + result[0].getKey())));
                    }
                });
            } else {
                Log.d(this.getClass().getName(), "No movie trailers for this movie");
            }
        }
    }
}
