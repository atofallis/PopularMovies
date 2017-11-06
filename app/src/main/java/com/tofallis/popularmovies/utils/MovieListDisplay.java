package com.tofallis.popularmovies.utils;

public enum MovieListDisplay {
    POPULARITY("popular"),
    RATING("top_rated"),
    FAVOURITES("favourites");

    String mFilter;
    MovieListDisplay(String s) {
        this.mFilter = s;
    }

    @Override
    public String toString() {
        return mFilter;
    }
}
