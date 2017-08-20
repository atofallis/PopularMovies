package com.tofallis.popularmovies;

import android.app.Application;

import com.tofallis.popularmovies.utils.NetworkUtils;

public class PopularMovies extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkUtils.init(this);
    }
}
