package com.tofallis.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.tofallis.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView mTextView;
    @BindView(R.id.imageGridView)
    GridView mGridView;

    private MasterListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAdapter = new MasterListAdapter(this);
        mGridView.setAdapter(mAdapter);

        loadMovieData(NetworkUtils.SortBy.POPULARITY);
    }

    private void loadMovieData(NetworkUtils.SortBy sortBy) {
        URL url = NetworkUtils.buildUrl(sortBy);
        new NetworkRequest().execute(url);
    }


    private class NetworkRequest extends AsyncTask<URL, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(URL... urls) {
            URL url = urls[0];
            try {
                String response = NetworkUtils.getResponseFromHttpUrl(url);

                Movie[] movieList = NetworkUtils.getMoviesFromJson(
                        MainActivity.this,
                        response);
                mAdapter.setMovies(Arrays.asList(movieList));
                return movieList;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] data) {
            mTextView.setText("");
            if(data != null) {
//                for(String poster : data) {
//                    mTextView.append(poster + "\n\n\n");
//                }
                mGridView.setAdapter(mAdapter);

                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Movie m = mAdapter.getMovies().get(position);
                        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                        intent.putExtra(Movie.IMG_URL, m.getImageUrl());
                        intent.putExtra(Movie.TITLE, m.getOriginalTitle());
                        intent.putExtra(Movie.OVERVIEW, m.getOverview());
                        intent.putExtra(Movie.RELEASE_DATE, m.getReleaseDate());
                        intent.putExtra(Movie.VOTE, m.getVoteAverage());

                        startActivity(intent);
                    }
                });

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
