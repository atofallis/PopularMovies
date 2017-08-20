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

import com.tofallis.popularmovies.R;

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
        POPULARITY_DESC("popularity.desc"),
        RATING_DESC("rating.desc");

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
    private static final String SORT_BY_PREFIX = "sort_by";

    private static final String MOVIES_BASE_URL =
            "https://api.themoviedb.org/3/discover/movie?api_key=";

    private static String sMoviesUrl;

    public static void init(Context c) {
        sMoviesUrl = MOVIES_BASE_URL + c.getString(R.string.api_key);
    }

    public static String getMoviesUrl() {
        return sMoviesUrl;
    }

    public static URL buildUrl(String locationQuery, SortBy sortBy) {
        Uri uri = Uri.parse(getMoviesUrl())
                .buildUpon()
                .appendQueryParameter(SORT_BY_PREFIX, sortBy.toString())
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
}