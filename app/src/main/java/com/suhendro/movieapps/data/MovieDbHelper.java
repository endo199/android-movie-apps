package com.suhendro.movieapps.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Suhendro on 8/5/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_ENTRY = "CREATE TABLE "+ MovieDbContract.MovieEntry.TABLE_NAME + " (" +
                    MovieDbContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieDbContract.MovieEntry.COLUMN_NAME_MOVIE_ID + " INTEGER UNIQUE," +
                    MovieDbContract.MovieEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                    MovieDbContract.MovieEntry.COLUMN_NAME_POSTER + " TEXT NOT NULL," +
                    MovieDbContract.MovieEntry.COLUMN_NAME_RATING + " NUMERIC NOT NULL," +
                    MovieDbContract.MovieEntry.COLUMN_NAME_DURATION + " INTEGER NOT NULL," +
                    MovieDbContract.MovieEntry.COLUMN_NAME_SYNOPSIS + " TEXT NOT NULL," +
                    MovieDbContract.MovieEntry.COLUMN_NAME_RELEASE_DATE + " INTEGER NOT NULL" +
                ")";

        sqLiteDatabase.execSQL(SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        final String SQL_DROP_ENTRY = "DROP TABLE IF EXISTS "+ MovieDbContract.MovieEntry.TABLE_NAME;

        sqLiteDatabase.execSQL(SQL_DROP_ENTRY);
        onCreate(sqLiteDatabase);
    }
}
