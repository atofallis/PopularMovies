/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tofallis.popularmovies.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.tofallis.popularmovies.BuildConfig;
import com.tofallis.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the Movie DB servers.
 */
public final class NetworkUtils {

    public enum SortBy {
        POPULARITY("popular"),
        RATING("top_rated");

        String mName;
        SortBy(String s) {
            this.mName = s;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String API_KEY_PREFIX = "api_key";
    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String MOVIES_BASE_URL =
            "https://api.themoviedb.org/3/movie";

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    public static URL buildUrl(SortBy sortBy) {
        Uri uri = Uri.parse(MOVIES_BASE_URL)
                .buildUpon()
                .appendPath(sortBy.toString())
                .appendQueryParameter(API_KEY_PREFIX, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (UnknownHostException uhe) {
            return "Host address not found. Ensure you are connected to a network";
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Movie[] getMoviesFromJson(Context context, String movieJsonStr)
            throws JSONException {

        final String RESULTS_LIST = "results";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS_LIST);
        Movie[] movies = new Movie[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movie = movieArray.getJSONObject(i);
            String img = movie.getString(Movie.POSTER_PATH);

            if(img != null) {
                movies[i] = new Movie(
                        POSTER_BASE_URL + img,
                        movie.getString(Movie.TITLE),
                        movie.getString(Movie.OVERVIEW),
                        movie.getString(Movie.VOTE),
                        movie.getString(Movie.RELEASE_DATE)
                );
            } else {
                Log.e(TAG, "Poster was not found for json: " + movie.toString());
            }
        }

        return movies;
    }
}