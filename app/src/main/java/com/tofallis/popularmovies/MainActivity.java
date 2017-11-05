package com.tofallis.popularmovies;

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

import com.tofallis.popularmovies.utils.AsyncTaskResult;
import com.tofallis.popularmovies.utils.MovieListRequest;
import com.tofallis.popularmovies.utils.NetworkUtils;

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
    NetworkUtils.SortBy mCurrentSort = NetworkUtils.SortBy.POPULARITY;

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
                mCurrentSort = NetworkUtils.SortBy.valueOf(savedInstanceState.getString(SORT_BY));
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

    private void loadMovieData(NetworkUtils.SortBy sortBy) {
        URL url = NetworkUtils.buildUrl(sortBy);
        new MovieListRequest(this, new GetMovieList()).execute(url);
        mCurrentSort = sortBy;
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
        if(item.getItemId() == R.id.sortByMostPopular) {
            loadMovieData(NetworkUtils.SortBy.POPULARITY);
            return true;
        } else if(item.getItemId() == R.id.sortByTopRated) {
            loadMovieData(NetworkUtils.SortBy.RATING);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
