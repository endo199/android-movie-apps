package com.suhendro.movieapps;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.suhendro.movieapps.model.Movie;
import com.suhendro.movieapps.model.Trailer;
import com.suhendro.movieapps.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity implements MovieTrailerAdapter.TrailerOnClickListener {
    private Movie mMovieDetail;
    private List<Trailer> mMovieTrailers = new ArrayList<>();

    private TextView mTitle;
    private TextView mRelease;
    private TextView mDuration;
    private TextView mSynopsis;
    private TextView mRating;
    private ImageView mPoster;

    private MovieTrailerAdapter mTrailerAdapter;
    private RecyclerView mTrailerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitle = (TextView) findViewById(R.id.tv_movie_title);
        mRelease = (TextView) findViewById(R.id.tv_release_date);
        mDuration = (TextView) findViewById(R.id.tv_movie_duration);
        mSynopsis = (TextView) findViewById(R.id.tv_synosis);
        mRating = (TextView) findViewById(R.id.tv_movie_rating);
        mPoster = (ImageView) findViewById(R.id.iv_poster);

        mTrailerList = (RecyclerView) findViewById(R.id.rv_movie_trailer);
        mTrailerList.setNestedScrollingEnabled(true);

        mTrailerAdapter = new MovieTrailerAdapter(mMovieTrailers, this);
        mTrailerList.setAdapter(mTrailerAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTrailerList.setLayoutManager(layoutManager);
        mTrailerList.setHasFixedSize(true);

        Intent intentFromCaller = getIntent();
        if(intentFromCaller.hasExtra(Intent.EXTRA_UID)) {
            Long movieId = intentFromCaller.getLongExtra(Intent.EXTRA_UID, 0);

            if(movieId > 0) {
                AsyncTask<Long, Void, Movie> fetchMovie = new MovieDetailAsyncTask();
                fetchMovie.execute(movieId);

                new MovieTrailerAsync().execute(movieId);
            } else {
                Log.d("XXX", "Movie id is zero");
            }
        }
    }

    private void showDetail() {
        mTitle.setText(mMovieDetail.getTitle());

        Calendar cal = Calendar.getInstance();
        cal.setTime(mMovieDetail.getReleaseDate());

        mRelease.setText(""+cal.get(Calendar.YEAR));
        mDuration.setText(mMovieDetail.getRuntime()+" mins");
        mSynopsis.setText(mMovieDetail.getSynopsis());
        mRating.setText(mMovieDetail.getRating() + "/10");

        Picasso.with(getApplicationContext())
                .load(mMovieDetail.getPosterUrl())
                .into(mPoster);
    }

    @Override
    public void onTrailerClick(int index) {
        Trailer trailer = mMovieTrailers.get(index);

        Intent intentToPlayTrailer = new Intent(Intent.ACTION_VIEW);
        intentToPlayTrailer.setData(Uri.parse("https://www.youtube.com/watch?v="+trailer.getKey()));
        if(intentToPlayTrailer.resolveActivity(getPackageManager()) != null) {
            startActivity(intentToPlayTrailer);
        }
    }

    class MovieDetailAsyncTask extends AsyncTask<Long, Void, Movie> {

        @Override
        protected Movie doInBackground(Long... movieIds) {
            Uri movieUri = NetworkUtils.buildMovieUrl(movieIds[0]);
            URL movieUrl;
            try {
                movieUrl = new URL(movieUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            String response = NetworkUtils.getHttpResponse(movieUrl);
            if(response != null) {
                Gson gson = new Gson();
                Movie movie = gson.fromJson(response, Movie.class);
                movie.setPosterUrl("http://image.tmdb.org/t/p/w500"+movie.getPosterUrl());

                return movie;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);
            mMovieDetail = movie;

            showDetail();
        }
    }

    class MovieTrailerAsync extends AsyncTask<Long, Void, List<Trailer>> {

        @Override
        protected List<Trailer> doInBackground(Long... movieIds) {
            Uri trailerUri = NetworkUtils.buildMovieTrailerListUrl(movieIds[0]);
            URL trailerUrl;
            try {
                trailerUrl = new URL(trailerUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            String response = NetworkUtils.getHttpResponse(trailerUrl);
            if(response != null) {
                try {
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray trailerListJson = jsonObj.getJSONArray("results");

                    List<Trailer> trailers = new ArrayList<>();
                    Gson gson = new Gson();
                    Trailer tmp;
                    JSONObject obj;
                    for(int i = 0; i < trailerListJson.length(); i++) {
                        obj = (JSONObject) trailerListJson.get(i);
                        tmp = gson.fromJson(obj.toString(), Trailer.class);
                        trailers.add(tmp);
                    }

                    return trailers;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            if(trailers != null && trailers.size() > 0) {
                mMovieTrailers.addAll(trailers);
                mTrailerAdapter.notifyDataSetChanged();
            }
        }
    }
}
