package com.tofallis.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tofallis.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private GridView mGridView;

    // store list of Movies from the api query.
    private List<Movie> mMovies = new ArrayList<>();

    private MasterListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);
        mGridView = (GridView) findViewById(R.id.imageGridView);
        mAdapter = new MasterListAdapter(this);
        mGridView.setAdapter(mAdapter);

        loadMovieData(NetworkUtils.SortBy.POPULARITY_DESC);
    }

    private void loadMovieData(NetworkUtils.SortBy sortBy) {
        URL url = NetworkUtils.buildUrl(sortBy);
        new NetworkRequest().execute(url);
    }

    public class MasterListAdapter extends BaseAdapter {

        // Keeps track of the context and list of images to display
        private Context mContext;

        public MasterListAdapter(Context context) {
            mContext = context;
        }

        /**
         * Returns the number of items the adapter will display
         */
        @Override
        public int getCount() {
            return mMovies.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        /**
         * Creates a new ImageView for each item referenced by the adapter
         */
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // If the view is not recycled, this creates a new ImageView to hold an image
                imageView = new ImageView(mContext);
                // Define the layout parameters
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            String url = mMovies.get(position).getImageUrl();
            Picasso.with(MainActivity.this).load(url).into(imageView);

            // Set the image resource and return the newly created ImageView
            //imageView.setImageResource(mImageIds.get(position));
            return imageView;
        }
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
                mMovies = Arrays.asList(movieList);
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
                        Movie m = mMovies.get(position);
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
            loadMovieData(NetworkUtils.SortBy.POPULARITY_DESC);
            return true;
        } else if(item.getItemId() == R.id.sortByTopRated) {
            loadMovieData(NetworkUtils.SortBy.RATING_DESC);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
