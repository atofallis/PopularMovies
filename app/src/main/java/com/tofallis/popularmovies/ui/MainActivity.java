package com.tofallis.popularmovies.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.tofallis.popularmovies.data.Movie;
import com.tofallis.popularmovies.data.MovieAdapter;
import com.tofallis.popularmovies.R;
import com.tofallis.popularmovies.network.AsyncTaskResult;
import com.tofallis.popularmovies.network.MovieListRequest;
import com.tofallis.popularmovies.network.NetworkUtils;
import com.tofallis.popularmovies.data.MovieListDisplay;

import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final String SORT_BY = "SORT_BY";
    @BindView(R.id.textView)
    TextView mTextView;
    @BindView(R.id.rvMovies)
    RecyclerView mMoviePosters;

    private MovieAdapter mAdapter;
    MovieListDisplay mCurrentSort = MovieListDisplay.POPULARITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAdapter = new MovieAdapter(this);
        mMoviePosters.setAdapter(mAdapter);

        setRecyclerViewLayoutManager(getResources().getConfiguration());

        if(savedInstanceState != null && savedInstanceState.getString(SORT_BY) != null) {
            try {
                mCurrentSort = MovieListDisplay.valueOf(savedInstanceState.getString(SORT_BY));
                Log.d(TAG, "onCreate. Using mCurrentSort from bundle: " + mCurrentSort.name());
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Unexpected sort by value. Exception:" + e.getMessage() );
            }
        } else {
            Log.d(TAG, "onCreate. mCurrentSort: " + mCurrentSort.name());
        }
        loadMovieData(mCurrentSort);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRecyclerViewLayoutManager(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState. mCurrentSort: " + mCurrentSort.name());
        outState.putString(SORT_BY, mCurrentSort.name());
    }

    private void setRecyclerViewLayoutManager(Configuration newConfig) {
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            mMoviePosters.setLayoutManager(new GridLayoutManager(this, 3));
        }
        else{
            mMoviePosters.setLayoutManager(new GridLayoutManager(this, 5));
        }
    }

    private void loadMovieData(MovieListDisplay movieListDisplay) {
        if(movieListDisplay == MovieListDisplay.FAVOURITES) {
            // load from contentProvider
        } else {
            URL url = NetworkUtils.buildUrl(movieListDisplay);
            new MovieListRequest(this, new GetMovieList()).execute(url);
        }
        mCurrentSort = movieListDisplay;
    }

    private class GetMovieList implements AsyncTaskResult<Movie[]>
    {
        @Override
        public void onTaskCompleted(Movie[] result) {
            mTextView.setText("");
            if(result != null) {
                mAdapter.setMovies(Arrays.asList(result));
                mAdapter.notifyDataSetChanged();
            } else {
                mTextView.setText("No data - ensure you are using a valid api key");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mTextView.setText("");
        switch(item.getItemId()) {
            case R.id.sortByMostPopular: {
                loadMovieData(MovieListDisplay.POPULARITY);
                return true;
            }
            case R.id.sortByTopRated: {
                loadMovieData(MovieListDisplay.RATING);
                return true;
            }
            case R.id.showFavourites: {
                loadMovieData(MovieListDisplay.FAVOURITES);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
