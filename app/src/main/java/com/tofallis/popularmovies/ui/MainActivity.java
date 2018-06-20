package com.tofallis.popularmovies.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.tofallis.popularmovies.R;
import com.tofallis.popularmovies.data.FavouritesContract;
import com.tofallis.popularmovies.data.Movie;
import com.tofallis.popularmovies.data.MovieAdapter;
import com.tofallis.popularmovies.data.MovieListDisplay;
import com.tofallis.popularmovies.network.AsyncTaskResult;
import com.tofallis.popularmovies.network.MovieListRequest;
import com.tofallis.popularmovies.network.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final String SORT_BY = "SORT_BY";
    private static final String RV_STATE = "RV_STATE";
    private static final String RV_FIRST_ITEM = "RV_FIRST_ITEM";
    private static final String RV_MOVIES = "RV_MOVIES";
    private static Movie[] mMovies;

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

        if (!restoreViewState(savedInstanceState)) {
            loadMovieData(mCurrentSort);
        }
    }

    private boolean restoreViewState(Bundle savedInstanceState) {
        boolean populated = false;
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(SORT_BY) != null) {
                try {
                    mCurrentSort = MovieListDisplay.valueOf(savedInstanceState.getString(SORT_BY));
                    Log.d(TAG, "Using mCurrentSort from bundle: " + mCurrentSort.name());
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Unexpected sort by value. Exception:" + e.getMessage());
                }
            } else {
                Log.d(TAG, "mCurrentSort: " + mCurrentSort.name());
            }

            mMovies = (Movie[]) savedInstanceState.getParcelableArray(RV_MOVIES);
            if (mMovies != null) {
                Log.d(TAG, "movies set from state");
                mAdapter.setMovies(Arrays.asList(mMovies));
                populated = true;
            }

            RecyclerView.LayoutManager layoutManager = mMoviePosters.getLayoutManager();
            if (layoutManager == null) {
                throw new IllegalStateException("Layout manager not initialised");
            }

            Parcelable rvState = savedInstanceState.getParcelable(RV_STATE);
            if (rvState != null) {
                layoutManager.onRestoreInstanceState(rvState);
            }
            Integer scrollTo = savedInstanceState.getInt(RV_FIRST_ITEM);
            int count = layoutManager.getItemCount();
            Log.d(TAG, "scrollTo: " + scrollTo);
            Log.d(TAG, "layoutManager.getItemCount(): " + count);
            if (scrollTo != null && scrollTo != NO_POSITION && scrollTo < count) {
                layoutManager.scrollToPosition(scrollTo);
                Log.d(TAG, "Scrolling to mMovies[scrollTo]: " + mMovies[scrollTo].getOriginalTitle());
            }
        }
        return populated;
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

        RecyclerView.LayoutManager layoutManager = mMoviePosters.getLayoutManager();
        if (layoutManager != null && layoutManager instanceof GridLayoutManager) {
            outState.putParcelable(RV_STATE, layoutManager.onSaveInstanceState());
            final int pos = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
            outState.putInt(RV_FIRST_ITEM, pos);
            Log.d(TAG, "onSaveInstanceState. RV_FIRST_ITEM: " + pos);
        }

        if(mMovies != null) {
            outState.putParcelableArray(RV_MOVIES, mMovies);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreViewState(savedInstanceState);
    }

    private void setRecyclerViewLayoutManager(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "setRecyclerViewLayoutManager:: Portrait");
            mMoviePosters.setLayoutManager(new MoviesLayoutManager(this, 3));
        } else {
            Log.d(TAG, "setRecyclerViewLayoutManager:: Landscape");
            mMoviePosters.setLayoutManager(new MoviesLayoutManager(this, 5));
        }
    }

    private class MoviesLayoutManager extends GridLayoutManager {

        public MoviesLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        @Override
        public void onLayoutCompleted(RecyclerView.State state) {
            super.onLayoutCompleted(state);
            final int pos = findFirstVisibleItemPosition();
            if(pos != NO_POSITION && mMovies != null) {
                Log.d(TAG, "Updated first visible item pos: " + pos);
                Log.d(TAG, "Updated first visible item: " + mMovies[pos].getOriginalTitle());
            }
        }
    };

    private void loadMovieData(MovieListDisplay movieListDisplay) {
        if (movieListDisplay == MovieListDisplay.FAVOURITES) {
            // load from contentProvider
            getFavourites();
        } else {
            Log.d(TAG, "Loading movies from network");
            URL url = NetworkUtils.getMovies(movieListDisplay);
            new MovieListRequest(new GetMovieList()).execute(url);
        }
        mCurrentSort = movieListDisplay;
    }

    private void getFavourites() {
        Log.d(TAG, "Loading movies from favourites");
        Cursor c = getContentResolver().query(FavouritesContract.CONTENT_URI,
                null,
                null,
                null,
                null);
        List<Movie> favourites = new ArrayList<>();

        if (c != null) {
            int id = c.getColumnIndex(FavouritesContract.FavouritesTable.COL_MOVIE_ID);
            int img = c.getColumnIndex(FavouritesContract.FavouritesTable.COL_IMG_URL);
            int title = c.getColumnIndex(FavouritesContract.FavouritesTable.COL_TITLE);
            int overview = c.getColumnIndex(FavouritesContract.FavouritesTable.COL_OVERVIEW);
            int vote = c.getColumnIndex(FavouritesContract.FavouritesTable.COL_VOTE);
            int releaseDate = c.getColumnIndex(FavouritesContract.FavouritesTable.COL_RELEASE_DATE);

            while (c.moveToNext()) {
                Movie m = new Movie(
                        c.getInt(id),
                        c.getString(img),
                        c.getString(title),
                        c.getString(overview),
                        c.getString(vote),
                        c.getString(releaseDate));
                favourites.add(m);
            }
            c.close();
        }
        mAdapter.setMovies(favourites);
        mAdapter.notifyDataSetChanged();
    }

    private class GetMovieList implements AsyncTaskResult<Movie[]> {
        @Override
        public void onTaskCompleted(Movie[] result) {
            mTextView.setText("");
            if (result != null) {
                mMovies = result;
                mAdapter.setMovies(Arrays.asList(result));
                mAdapter.notifyDataSetChanged();
            } else {
                mTextView.setText("No data - ensure you are using a valid api key");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovieData(mCurrentSort);
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
        switch (item.getItemId()) {
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
