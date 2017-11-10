package com.tofallis.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.tofallis.popularmovies.data.FavouritesContract.FavouritesTable.COL_IMG_URL;
import static com.tofallis.popularmovies.data.FavouritesContract.FavouritesTable.COL_MOVIE_ID;
import static com.tofallis.popularmovies.data.FavouritesContract.FavouritesTable.COL_OVERVIEW;
import static com.tofallis.popularmovies.data.FavouritesContract.FavouritesTable.COL_RELEASE_DATE;
import static com.tofallis.popularmovies.data.FavouritesContract.FavouritesTable.COL_TITLE;
import static com.tofallis.popularmovies.data.FavouritesContract.FavouritesTable.COL_VOTE;
import static com.tofallis.popularmovies.data.FavouritesContract.FavouritesTable.TABLE_NAME;
import static com.tofallis.popularmovies.data.FavouritesContract.FavouritesTable._ID;

public class FavouritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favouritesDb.db";
    private static final int VERSION = 2;
    private static final String ALTER_PREFIX = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN ";
    private static final String STRING_SUFFIX = " STRING NOT NULL";

    FavouritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COL_MOVIE_ID + " INTEGER NOT NULL, " +
                COL_IMG_URL + STRING_SUFFIX + ", " +
                COL_TITLE + STRING_SUFFIX + ", " +
                COL_OVERVIEW + STRING_SUFFIX + ", " +
                COL_VOTE + STRING_SUFFIX + ", " +
                COL_RELEASE_DATE + STRING_SUFFIX + ");";
        db.execSQL(CREATE_TABLE);
    }

    private static final String V2_MOVIE_ID = ALTER_PREFIX + COL_MOVIE_ID + " INTEGER NOT NULL;";
    private static final String V2_ING = ALTER_PREFIX + COL_IMG_URL + STRING_SUFFIX;
    private static final String V2_TITLE = ALTER_PREFIX + COL_IMG_URL + STRING_SUFFIX;
    private static final String V2_OVERVIEW = ALTER_PREFIX + COL_IMG_URL + STRING_SUFFIX;
    private static final String V2_VOTE = ALTER_PREFIX + COL_IMG_URL + STRING_SUFFIX;
    private static final String V2_RELEASE_DATE = ALTER_PREFIX + COL_IMG_URL + STRING_SUFFIX;

    private static final String[] DATABASE_ALTER_V2 = {
            V2_MOVIE_ID, V2_ING, V2_TITLE, V2_OVERVIEW, V2_VOTE, V2_RELEASE_DATE
    };

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            for (String alter : DATABASE_ALTER_V2) {
                db.execSQL(alter);
            }
        }
    }
}
