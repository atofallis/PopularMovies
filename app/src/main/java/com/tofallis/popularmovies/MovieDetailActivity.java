package com.tofallis.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.movieImage)
    ImageView mImageView;
    @BindView(R.id.original_title)
    TextView mOriginalTitle;
    @BindView(R.id.overview)
    TextView mOverview;
    @BindView(R.id.vote_average)
    TextView mVoteAverage;
    @BindView(R.id.release_date)
    TextView mReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Picasso.with(this).load(getIntent().getStringExtra(Movie.IMG_URL)).into(mImageView);
        mOriginalTitle.setText(getIntent().getStringExtra(Movie.TITLE) + "\n\n\n");
        mOverview.setText(getIntent().getStringExtra(Movie.OVERVIEW) + "\n\n\n");
        mVoteAverage.setText(getIntent().getStringExtra(Movie.VOTE) + "\n\n\n");
        mReleaseDate.setText(getIntent().getStringExtra(Movie.RELEASE_DATE) + "\n\n\n");
    }
}
