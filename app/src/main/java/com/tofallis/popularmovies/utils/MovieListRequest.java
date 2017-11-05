package com.tofallis.popularmovies.utils;

import android.os.AsyncTask;

import com.tofallis.popularmovies.MainActivity;
import com.tofallis.popularmovies.Movie;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MovieListRequest extends AsyncTask<URL, Void, Movie[]> {

    private MainActivity mContext;
    private AsyncTaskResult<Movie[]> mResultsListener;

    public MovieListRequest(MainActivity context, AsyncTaskResult<Movie[]> resultsListener) {
        mContext = context;
        mResultsListener = resultsListener;
    }

    @Override
    protected Movie[] doInBackground(URL... urls) {
        URL url = urls[0];
        try {
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            Movie[] movieList = NetworkUtils.getMoviesFromJson(
                    mContext,
                    response);
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
        super.onPostExecute(data);
        mResultsListener.onTaskCompleted(data);
    }
}
