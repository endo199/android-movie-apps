package com.suhendro.movieapps;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.suhendro.movieapps.model.Movie;
import com.suhendro.movieapps.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {
    private MovieAdapter mAdapter;
    private final int NUM_OF_COLUMN = 2;
    private RecyclerView mPosterGrid;
    private List<Movie> mMovies = new ArrayList<>();
    private int page = 1;

    private ProgressBar mLoadingIndicator;
    private TextView mErrorDisplay;

    private final String SORT_BY_POPULARITY = "popular";
    private final String SORT_BY_RATING = "top_rated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: fix the title and action menu color, change it into white or template color
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mErrorDisplay = (TextView) findViewById(R.id.tv_error_display);

        mPosterGrid = (RecyclerView) findViewById(R.id.rv_movie_poster);
        mPosterGrid.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, NUM_OF_COLUMN);
        mPosterGrid.setLayoutManager(gridLayoutManager);

        fetchMoviesData(SORT_BY_POPULARITY);
        mAdapter = new MovieAdapter(mMovies, this);
        mPosterGrid.setAdapter(mAdapter);
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
                resetAdapter();
                fetchMoviesData(SORT_BY_POPULARITY);
                break;
            case R.id.action_order_rating:
                resetAdapter();
                fetchMoviesData(SORT_BY_RATING);
                break;
            case R.id.action_favorite:
                // TODO: implement show list of movies based on user's favorite
                resetAdapter();
                fetchFavoriteMovies();
                break;
        }

        return true;
    }

    private void fetchFavoriteMovies() {
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

    // TODO: ganti dengan menggunakan retrofit
    private void fetchMoviesData(String sortBy) {
        AsyncTask<String, Void, JSONObject> fetch = new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mLoadingIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            protected JSONObject doInBackground(String... sorts) {
                Log.i("XXX", "Fetching data in background");
                Uri movieDbUrl = NetworkUtils.buildUri(sorts[0], page);
                String fetchResult = null;
                try {
                    fetchResult = NetworkUtils.getHttpResponse(new URL(movieDbUrl.toString()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e("XXX", "URL Format exception");
                    showErrorMessage("URL Format exception");

                    return null;
                }

                if(fetchResult != null) {
                    try {
                        JSONObject json = (JSONObject) new JSONTokener(fetchResult).nextValue();
                        return json;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("XXX", "Error parsing JSON String");
                        showErrorMessage("Error parsing JSON String");
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                if(jsonObject == null) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    Log.d("XXX", "No data");
                    return;
                }

                try {
                    Gson gson = new Gson();
                    JSONArray moviesJson = jsonObject.getJSONArray("results");
                    Movie tmp;
                    for(int i = 0; i < moviesJson.length(); i++) {
                        tmp = gson.fromJson(moviesJson.getJSONObject(i).toString(), Movie.class);
                        tmp.setPosterUrl("http://image.tmdb.org/t/p/w500"+tmp.getPosterUrl());
                        mMovies.add(tmp);
                    }
                    mAdapter.notifyDataSetChanged();
                    showData();

                    // increment page to show next page
                    page++;
                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorMessage("Error processing movies data");
                } finally {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                }
            }
        };

        fetch.execute(sortBy);
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
