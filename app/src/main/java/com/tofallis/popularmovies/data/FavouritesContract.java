package com.tofallis.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavouritesContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.tofallis.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_FAVOURITES = "favourites";

    // TaskEntry content URI = base content URI + path
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();

    public static final Uri CONTENT_URI_ITEM = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).appendPath("#").build();

    public static final class FavouritesTable implements BaseColumns {
        public static final String TABLE_NAME = "favourites";
        public static final String COL_MOVIE_ID = "movie_id";
        public static final String COL_IMG_URL = "img_url";
        public static final String COL_TITLE = "original_title";
        public static final String COL_OVERVIEW = "overview";
        public static final String COL_VOTE = "vote_average";
        public static final String COL_RELEASE_DATE = "release_date";
    }
}
