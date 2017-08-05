package com.suhendro.movieapps.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Suhendro on 8/5/2017.
 */

public class MovieProvider extends ContentProvider {
    public static final int MOVIES = 100;
    public static final int MOVIES_DETAIL = 101;
    public static final int MOVIES_FAVORITE = 200;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
//        sUriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_MOVIES, MOVIES);
        sUriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_MOVIES + "/favorite", MOVIES_FAVORITE);
//        sUriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_MOVIES+"/#", MOVIES_DETAIL);
    }

    private MovieDbHelper mMovieDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int matchCode = sUriMatcher.match(uri);
        Log.d("XXX", "Fetching "+matchCode + " from "+uri.toString());
        Cursor cursor = null;

        switch (matchCode) {
            case MOVIES:
                break;
            case MOVIES_DETAIL:
                break;
            case MOVIES_FAVORITE:
                final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
//                cursor = db.query(MovieDbContract.MovieEntry.TABLE_NAME, null, null, null, null, null, MovieDbContract.MovieEntry._ID);
                cursor = db.query(MovieDbContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        long id = db.insertWithOnConflict(MovieDbContract.MovieEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_ABORT);

        if(id < 0) {
            Log.e("XXX", "Insert new favorite movie failed");
            return null;
        }

        Uri newFavoriteUri = ContentUris.withAppendedId(MovieDbContract.MovieEntry.CONTENT_URI, id);
        getContext().getContentResolver().notifyChange(uri, null);

        Log.d("XXX", "inserting new favorite: "+newFavoriteUri.toString());

        return newFavoriteUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
