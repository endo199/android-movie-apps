package com.suhendro.movieapps;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.suhendro.movieapps.model.Movie;
import com.suhendro.movieapps.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

        Log.i("XXX", "Running");

        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mErrorDisplay = (TextView) findViewById(R.id.tv_error_display);

        mPosterGrid = (RecyclerView) findViewById(R.id.rv_movie_poster);
        mPosterGrid.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, NUM_OF_COLUMN);
        mPosterGrid.setLayoutManager(gridLayoutManager);

        fetchMoviesData(SORT_BY_POPULARITY);
        mAdapter = new MovieAdapter(mMovies, this);
        mPosterGrid.setAdapter(mAdapter);

//        mPosterGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if(newState == RecyclerView.SCROLL_STATE_SETTLING) {
//                    fetchMoviesData();
//                }
//            }
//        });

        Log.i("XXX", "Selesai");
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
        }

        return true;
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
                Uri movieDbUrl = NetworkUtils.buildUrl(sorts[0], page);
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
        Long movieId = mMovies.get(index).getId();

        Intent intentThatCallMovieDetail = new Intent(getApplicationContext(), DetailActivity.class);
        intentThatCallMovieDetail.putExtra(Intent.EXTRA_UID, movieId);
        startActivity(intentThatCallMovieDetail);
    }
}
