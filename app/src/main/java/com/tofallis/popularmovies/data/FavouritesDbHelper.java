package com.tofallis.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.tofallis.popularmovies.data.FavouritesContract.FavouritesTable.COL_MOVIE_ID;
import static com.tofallis.popularmovies.data.FavouritesContract.FavouritesTable.TABLE_NAME;
import static com.tofallis.popularmovies.data.FavouritesContract.FavouritesTable._ID;

public class FavouritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favouritesDb.db";
    private static final int VERSION = 1;

    FavouritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COL_MOVIE_ID + " INTEGER NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
