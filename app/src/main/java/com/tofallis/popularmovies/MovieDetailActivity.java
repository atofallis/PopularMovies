package com.tofallis.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mOriginalTitle;
    private TextView mOverview;
    private TextView mVoteAverage;
    private TextView mReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mImageView = (ImageView) findViewById(R.id.movieImage);
        mOriginalTitle = (TextView) findViewById(R.id.original_title);
        mOverview = (TextView) findViewById(R.id.overview);
        mVoteAverage = (TextView) findViewById(R.id.vote_average);
        mReleaseDate = (TextView) findViewById(R.id.release_date);

        Picasso.with(this).load(getIntent().getStringExtra(Movie.IMG_URL)).into(mImageView);
        mOriginalTitle.setText(getIntent().getStringExtra(Movie.TITLE));
        mOverview.setText(getIntent().getStringExtra(Movie.OVERVIEW));
        mVoteAverage.setText(getIntent().getStringExtra(Movie.VOTE));
        mReleaseDate.setText(getIntent().getStringExtra(Movie.RELEASE_DATE));
    }
}
