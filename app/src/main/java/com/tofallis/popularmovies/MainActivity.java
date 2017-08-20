package com.tofallis.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.tofallis.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mTextView = (TextView) findViewById(R.id.textView);
        loadMovieData(NetworkUtils.SortBy.POPULARITY_DESC);
    }

    private void loadMovieData(NetworkUtils.SortBy sortBy) {
        URL url = NetworkUtils.buildUrl(mTextView.getText().toString(), sortBy);
        new NetworkRequest().execute(url);
    }

    private class NetworkRequest extends AsyncTask<URL, Void, String[]> {

        @Override
        protected String[] doInBackground(URL... urls) {
            URL url = urls[0];
            try {
                String response = NetworkUtils.getResponseFromHttpUrl(url);

                String[] posterList = NetworkUtils.getMoviePostersFromJson(
                        MainActivity.this,
                        response);
                return posterList;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] data) {
            mTextView.setText("");
            if(data != null) {
                for(String poster : data) {
                    mTextView.append(poster + "\n\n\n");
                }
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
