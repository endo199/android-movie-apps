package com.suhendro.movieapps;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.suhendro.movieapps.data.MovieDbContract;
import com.suhendro.movieapps.data.MovieService;
import com.suhendro.movieapps.model.Movie;
import com.suhendro.movieapps.model.MovieList;
import com.suhendro.movieapps.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {
    private MovieAdapter mAdapter;
    private final int NUM_OF_COLUMN = 2;
    private RecyclerView mPosterGrid;
    private List<Movie> mMovies = new ArrayList<Movie>();
    private int page = 1;

    private ProgressBar mLoadingIndicator;
    private TextView mErrorDisplay;

    private final String SORT_BY_POPULARITY = "popular";
    private final String SORT_BY_RATING = "top_rated";

    private Retrofit retrofit;
    private MovieService movieService;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mErrorDisplay = (TextView) findViewById(R.id.tv_error_display);

        mPosterGrid = (RecyclerView) findViewById(R.id.rv_movie_poster);
        mPosterGrid.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, NUM_OF_COLUMN);
        mPosterGrid.setLayoutManager(gridLayoutManager);

        mAdapter = new MovieAdapter(mMovies, this);
        mPosterGrid.setAdapter(mAdapter);

        this.retrofit = new Retrofit.Builder()
                .baseUrl(MovieService.IMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.movieService = this.retrofit.create(MovieService.class);

        if(savedInstanceState != null) {
            Log.i("XXX", "Using saved data");

            this.isFavorite = savedInstanceState.getBoolean("favorite", false);

            if(this.isFavorite) {
                fetchFavoriteMovies();
            } else {
                this.page = savedInstanceState.getInt("page");
                ArrayList<Movie> restoredMovies = savedInstanceState.getParcelableArrayList("movies");
                if (restoredMovies != null) {
                    mMovies.addAll(restoredMovies);
                    mAdapter.notifyDataSetChanged();
                }
            }
        } else {
            retrofitMovieData(SORT_BY_POPULARITY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isFavorite) {
            resetAdapter();
            fetchFavoriteMovies();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(isFavorite) {
            outState.putBoolean("favorite", isFavorite);
        } else {
            outState.putInt("page", this.page);
            ArrayList<Movie> lMovies = new ArrayList<>(mMovies);
            outState.putParcelableArrayList("movies", lMovies);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void resetAdapter() {
        page = 1;
        mMovies.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_order_popularity:
                isFavorite = false;
                resetAdapter();
                retrofitMovieData(SORT_BY_POPULARITY);
                break;
            case R.id.action_order_rating:
                isFavorite = false;
                resetAdapter();
                retrofitMovieData(SORT_BY_RATING);
                break;
            case R.id.action_favorite:
                isFavorite = true;
                resetAdapter();
                fetchFavoriteMovies();
                break;
        }

        return true;
    }

    private void fetchFavoriteMovies() {
        Log.i("XXX", "Fetching favorite movies");
        mLoadingIndicator.setVisibility(View.VISIBLE);
        Uri favoriteMoviesUri = Uri.withAppendedPath(MovieDbContract.MovieEntry.CONTENT_URI, "favorite");

        Cursor result = getContentResolver().query(favoriteMoviesUri, MovieDbContract.TABLE_MOVIE_COLUMNS, null, null, MovieDbContract.MovieEntry._ID);
        if(result == null || result.getCount() == 0) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            showErrorMessage("You don't have any favorite movies, yet.");
            return;
        }

        result.moveToFirst();
        Movie movie;
        do {
            movie = new Movie();

            movie.setId(result.getLong(MovieDbContract.TABLE_COLUMN_MOVIE_ID_IDX));
            movie.setPosterUrl(result.getString(MovieDbContract.TABLE_COLUMN_POSTER_IDX));
            movie.setRating(result.getFloat(MovieDbContract.TABLE_COLUMN_RATING_IDX));
            movie.setReleaseDate(new Date(result.getLong(MovieDbContract.TABLE_COLUMN_RELEASE_DATE_IDX)));
            movie.setRuntime(result.getInt(MovieDbContract.TABLE_COLUMN_DURATION_IDX));
            movie.setSynopsis(result.getString(MovieDbContract.TABLE_COLUMN_SYNOPSIS_IDX));
            movie.setTitle(result.getString(MovieDbContract.TABLE_COLUMN_TITLE_IDX));
            movie.setBackdropPath(result.getString(MovieDbContract.TABLE_COLUMN_BACKDROP_IDX));

            mMovies.add(movie);
        } while(result.moveToNext());
        result.close();

        if(result.getCount() > 0) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mAdapter.notifyDataSetChanged();
            showData();
        }
    }

    private void showData() {
        mPosterGrid.setVisibility(View.VISIBLE);
        mErrorDisplay.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage(String errorMsg) {
        mPosterGrid.setVisibility(View.INVISIBLE);
        mErrorDisplay.setVisibility(View.VISIBLE);
        mErrorDisplay.setText(errorMsg);
    }

    private void retrofitMovieData(String sortBy) {
        mLoadingIndicator.setVisibility(View.VISIBLE);

        Log.i("XXX", "Fetching movie data from server");
        Call<MovieList> moviesPromise = this.movieService.getMovies(sortBy, MovieService.API_KEY, page);
        moviesPromise.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                if(response.isSuccessful()) {
                    for (Movie m : response.body().getResults()) {
                        m.setPosterUrl("http://image.tmdb.org/t/p/w500"+m.getPosterUrl());
                        mMovies.add(m);
                    }
                    mAdapter.notifyDataSetChanged();
                    showData();
                } else {
                    showErrorMessage(response.raw().message());
                }

                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                t.printStackTrace();
                showErrorMessage("Error connection");
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onListItemClick(int index) {
        Movie movie = mMovies.get(index);

        Intent intentThatCallMovieDetail = new Intent(getApplicationContext(), DetailActivity.class);
        // basic information about the movie doesn't contain duration of the movie
        // so, in detail it will fetch again from server to get full detail
        intentThatCallMovieDetail.putExtra("movieObj", movie);

        startActivity(intentThatCallMovieDetail);
    }
}
