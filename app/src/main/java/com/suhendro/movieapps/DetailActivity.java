package com.suhendro.movieapps;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.suhendro.movieapps.data.MovieDbContract;
import com.suhendro.movieapps.model.Movie;
import com.suhendro.movieapps.model.Review;
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
import java.util.List;

public class DetailActivity extends AppCompatActivity implements MovieTrailerAdapter.TrailerOnClickListener {
    private Movie mMovieDetail;
    private List<Trailer> mMovieTrailers = new ArrayList<>();
    private List<Review> mMovieReviews = new ArrayList<>();

    private TextView mRelease;
    private TextView mDuration;
    private TextView mSynopsis;
    private TextView mRating;
    private ImageView mPoster;
    private Button mFavorite;

    private Toolbar mToolbar;
    private ImageView mToolbarBackgroundImg;

    private MovieTrailerAdapter mTrailerAdapter;
    private MovieReviewAdapter mReviewAdapter;
    private RecyclerView mTrailerList;
    private RecyclerView mReviewList;
    private CollapsingToolbarLayout collapsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mRelease = (TextView) findViewById(R.id.tv_release_date);
        mDuration = (TextView) findViewById(R.id.tv_movie_duration);
        mSynopsis = (TextView) findViewById(R.id.tv_synopsis);
        mRating = (TextView) findViewById(R.id.tv_movie_rating);
        mPoster = (ImageView) findViewById(R.id.iv_poster);

        collapsToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarBackgroundImg = (ImageView) findViewById(R.id.app_bar_image);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTrailerList = (RecyclerView) findViewById(R.id.rv_movie_trailer);
        mTrailerList.setNestedScrollingEnabled(true);

        mTrailerAdapter = new MovieTrailerAdapter(mMovieTrailers, this);
        mTrailerList.setAdapter(mTrailerAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTrailerList.setLayoutManager(layoutManager);
        mTrailerList.setHasFixedSize(true);

        Intent intentFromCaller = getIntent();
        mMovieDetail = intentFromCaller.getParcelableExtra("movieObj");
        if(mMovieDetail != null) {
            showDetail();
            new MovieTrailerAsync().execute(mMovieDetail.getId());
            new MovieReviewsAsycnTask().execute(mMovieDetail.getId());

            // movie duration is not available, so request for detail
            new MovieDetailAsyncTask().execute(mMovieDetail.getId());
        }

        mReviewList = (RecyclerView) findViewById(R.id.rv_movie_reviews);
        mReviewList.setNestedScrollingEnabled(true);

        mReviewAdapter = new MovieReviewAdapter(mMovieReviews);
        mReviewList.setAdapter(mReviewAdapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        mReviewList.setLayoutManager(layoutManager2);
        mReviewList.setHasFixedSize(true);

        mFavorite = (Button) findViewById(R.id.tb_mark_favorite);
    }

    public void setFavorite(View view) {
        ContentValues cv = new ContentValues();
        cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_MOVIE_ID, mMovieDetail.getId());
        cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_TITLE, mMovieDetail.getTitle());
        cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_POSTER, mMovieDetail.getPosterUrl());
        cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_RATING, mMovieDetail.getRating());
        cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, mMovieDetail.getReleaseDate().getTime());
        cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_SYNOPSIS, mMovieDetail.getSynopsis());
        cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_DURATION, mMovieDetail.getRuntime());

        Uri uri = getContentResolver().insert(MovieDbContract.MovieEntry.CONTENT_URI, cv);
    }

    private void showDetail() {
        collapsToolbar.setTitle(mMovieDetail.getTitle());

        Calendar cal = Calendar.getInstance();
        cal.setTime(mMovieDetail.getReleaseDate());

        mRelease.setText(""+cal.get(Calendar.YEAR));
        mDuration.setText(mMovieDetail.getRuntime()+" mins");
        mSynopsis.setText(mMovieDetail.getSynopsis());
        mRating.setText(mMovieDetail.getRating() + "/10");

        Picasso.with(getApplicationContext())
                .load(mMovieDetail.getPosterUrl())
                .into(mPoster);

        Picasso.with(getApplicationContext())
                .load(mMovieDetail.getPosterUrl())
                .into(mToolbarBackgroundImg);
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
            Uri movieUri = NetworkUtils.buildMovieUri(movieIds[0]);
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
            Uri trailerUri = NetworkUtils.buildMovieTrailerListUri(movieIds[0]);
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

    class MovieReviewsAsycnTask extends AsyncTask<Long, Void, List<Review>> {

        @Override
        protected List<Review> doInBackground(Long... longs) {
            Uri uri = NetworkUtils.buildMovieReviewsUri(longs[0]);
            URL reviewsUrl;
            try {
                reviewsUrl = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            String reviews = NetworkUtils.getHttpResponse(reviewsUrl);
            if(reviews != null) {
                try {
                    JSONObject jsonObj = (JSONObject) new JSONTokener(reviews).nextValue();
                    JSONArray trailerListJson = jsonObj.getJSONArray("results");

                    List<Review> reviewList = new ArrayList<>();
                    Gson gson = new Gson();
                    Review tmp;
                    JSONObject obj;
                    for(int i = 0; i < trailerListJson.length(); i++) {
                        obj = (JSONObject) trailerListJson.get(i);
                        tmp = gson.fromJson(obj.toString(), Review.class);
                        reviewList.add(tmp);
                    }

                    return reviewList;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            super.onPostExecute(reviews);
            if(reviews != null && reviews.size() > 0) {
                mMovieReviews.addAll(reviews);
                mReviewAdapter.notifyDataSetChanged();
            }
        }
    }
}
