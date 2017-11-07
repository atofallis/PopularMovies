package com.tofallis.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.tofallis.popularmovies.data.FavouritesContract.FavouritesTable.TABLE_NAME;

public class FavouritesContentProvider extends ContentProvider {

    public static final int FAVOURITES = 100;
    public static final int FAVOURITES_WITH_ID = 101;

    private FavouritesDbHelper mFavouritesDbHelper;
    private static final UriMatcher sMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        if(sMatcher == null) {
            // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
            UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            uriMatcher.addURI(FavouritesContract.AUTHORITY, FavouritesContract.PATH_FAVOURITES, FAVOURITES);
            uriMatcher.addURI(FavouritesContract.AUTHORITY, FavouritesContract.PATH_FAVOURITES + "/#", FAVOURITES_WITH_ID);
            return uriMatcher;
        }
        return sMatcher;
    }

    @Override
    public boolean onCreate() {
        mFavouritesDbHelper = new FavouritesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mFavouritesDbHelper.getWritableDatabase();

        int match = sMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case FAVOURITES:
                long id = db.insert(TABLE_NAME, null, contentValues);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavouritesContract.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mFavouritesDbHelper.getWritableDatabase();
        int match = sMatcher.match(uri);
        int deleted;

        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case FAVOURITES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                deleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
