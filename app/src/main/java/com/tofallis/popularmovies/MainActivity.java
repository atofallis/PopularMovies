package com.tofallis.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

    // store list of Image URLs from the api query.
    private List<String> mImageUrls = new ArrayList<String>();

    private MasterListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView);
        mGridView = findViewById(R.id.imageGridView);
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
            return mImageUrls.size();
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

            String url = mImageUrls.get(position);
            Picasso.with(MainActivity.this).load(url).into(imageView);

            // Set the image resource and return the newly created ImageView
            //imageView.setImageResource(mImageIds.get(position));
            return imageView;
        }
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
                mImageUrls = Arrays.asList(posterList);
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
//                for(String poster : data) {
//                    mTextView.append(poster + "\n\n\n");
//                }
                mGridView.setAdapter(mAdapter);
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
