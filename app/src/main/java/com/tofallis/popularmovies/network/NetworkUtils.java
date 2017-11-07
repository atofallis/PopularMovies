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
package com.tofallis.popularmovies.network;

import android.net.Uri;
import android.util.Log;

import com.tofallis.popularmovies.BuildConfig;
import com.tofallis.popularmovies.data.Movie;
import com.tofallis.popularmovies.data.MovieListDisplay;
import com.tofallis.popularmovies.data.Trailer;

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

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String API_KEY_PREFIX = "api_key";
    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String MOVIES_BASE_URL =
            "https://api.themoviedb.org/3/movie";

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    public static URL getMovies(MovieListDisplay movieListDisplay) {
        Uri uri = Uri.parse(MOVIES_BASE_URL)
                .buildUpon()
                .appendPath(movieListDisplay.toString())
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
    public static URL getTrailers(int movieId) {
        Uri uri = Uri.parse(MOVIES_BASE_URL)
                .buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath("videos")
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

    public static Movie[] getMoviesFromJson(String movieJsonStr)
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
                        movie.getInt(Movie.ID),
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

    public static Trailer[] getTrailersFromJson(String trailerJsonStr)
            throws JSONException {

        final String RESULTS_LIST = "results";

        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(RESULTS_LIST);
        Trailer[] trailers = new Trailer[trailerArray.length()];

        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailer = trailerArray.getJSONObject(i);
            trailers[i] = new Trailer(
                    trailer.getString(Trailer.ID),
                    trailer.getString(Trailer.KEY)
            );
        }

        return trailers;
    }
}