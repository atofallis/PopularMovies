package com.tofallis.popularmovies.network;

import android.os.AsyncTask;

import com.tofallis.popularmovies.data.Review;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class ReviewListRequest extends AsyncTask<URL, Void, Review[]> {

    private AsyncTaskResult<Review[]> mResultsListener;

    public ReviewListRequest(AsyncTaskResult<Review[]> resultsListener) {
        mResultsListener = resultsListener;
    }

    @Override
    protected Review[] doInBackground(URL... urls) {
        URL url = urls[0];
        try {
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            Review[] reviewList = NetworkUtils.getReviewsFromJson(response);
            return reviewList;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Review[] data) {
        super.onPostExecute(data);
        mResultsListener.onTaskCompleted(data);
    }
}
