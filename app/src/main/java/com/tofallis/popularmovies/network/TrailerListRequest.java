package com.tofallis.popularmovies.network;

import android.os.AsyncTask;

import com.tofallis.popularmovies.data.Trailer;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class TrailerListRequest extends AsyncTask<URL, Void, Trailer[]> {

    private AsyncTaskResult<Trailer[]> mResultsListener;

    public TrailerListRequest(AsyncTaskResult<Trailer[]> resultsListener) {
        mResultsListener = resultsListener;
    }

    @Override
    protected Trailer[] doInBackground(URL... urls) {
        URL url = urls[0];
        try {
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            Trailer[] trailerList = NetworkUtils.getTrailersFromJson(response);
            return trailerList;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Trailer[] data) {
        super.onPostExecute(data);
        mResultsListener.onTaskCompleted(data);
    }
}
