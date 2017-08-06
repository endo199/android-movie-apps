package com.suhendro.movieapps.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Suhendro on 8/5/2017.
 */

public final class MovieDbContract {
    public static final String AUTHORITY = "com.suhendro.movieapps";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final String[] TABLE_MOVIE_COLUMNS = {
            MovieEntry.COLUMN_NAME_MOVIE_ID,
            MovieEntry.COLUMN_NAME_POSTER,
            MovieEntry.COLUMN_NAME_TITLE,
            MovieEntry.COLUMN_NAME_SYNOPSIS,
            MovieEntry.COLUMN_NAME_RATING,
            MovieEntry.COLUMN_NAME_RELEASE_DATE,
            MovieEntry.COLUMN_NAME_DURATION,
            MovieEntry.COLUMN_NAME_BACKDROP
    };
    public static final int TABLE_COLUMN_MOVIE_ID_IDX = 0;
    public static final int TABLE_COLUMN_POSTER_IDX = 1;
    public static final int TABLE_COLUMN_TITLE_IDX = 2;
    public static final int TABLE_COLUMN_SYNOPSIS_IDX = 3;
    public static final int TABLE_COLUMN_RATING_IDX = 4;
    public static final int TABLE_COLUMN_RELEASE_DATE_IDX = 5;
    public static final int TABLE_COLUMN_DURATION_IDX = 6;
    public static final int TABLE_COLUMN_BACKDROP_IDX = 7;


    public static class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_NAME_MOVIE_ID = "movieId";
        public static final String COLUMN_NAME_POSTER = "poster";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SYNOPSIS = "synopsis";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_RELEASE_DATE = "release";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_BACKDROP = "backdrop";
    }
}
