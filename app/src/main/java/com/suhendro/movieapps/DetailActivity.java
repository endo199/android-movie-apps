package com.suhendro.movieapps;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.suhendro.movieapps.data.MovieDbContract;
import com.suhendro.movieapps.data.MovieService;
import com.suhendro.movieapps.model.Movie;
import com.suhendro.movieapps.model.Review;
import com.suhendro.movieapps.model.ReviewList;
import com.suhendro.movieapps.model.Trailer;
import com.suhendro.movieapps.model.TrailerList;
import com.suhendro.movieapps.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity implements MovieTrailerAdapter.TrailerOnClickListener {
    private Movie mMovieDetail;
    private List<Trailer> mMovieTrailers = new ArrayList<>();
    private List<Review> mMovieReviews = new ArrayList<>();

    private TextView mRelease;
    private TextView mDuration;
    private TextView mSynopsis;
    private TextView mRating;
    private ImageView mPoster;
    private ToggleButton mFavorite;

    private Toolbar mToolbar;
    private ImageView mToolbarBackgroundImg;

    private MovieTrailerAdapter mTrailerAdapter;
    private MovieReviewAdapter mReviewAdapter;
    private RecyclerView mTrailerList;
    private RecyclerView mReviewList;
    private CollapsingToolbarLayout collapsToolbar;

    private Retrofit retrofit;
    private MovieService movieService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mRelease = (TextView) findViewById(R.id.tv_release_date);
        mDuration = (TextView) findViewById(R.id.tv_movie_duration);
        mSynopsis = (TextView) findViewById(R.id.tv_synopsis);
        mRating = (TextView) findViewById(R.id.tv_movie_rating);
        mPoster = (ImageView) findViewById(R.id.iv_poster);
        mFavorite = (ToggleButton) findViewById(R.id.tb_mark_favorite);

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

        mReviewList = (RecyclerView) findViewById(R.id.rv_movie_reviews);
        mReviewList.setNestedScrollingEnabled(true);

        mReviewAdapter = new MovieReviewAdapter(mMovieReviews);
        mReviewList.setAdapter(mReviewAdapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        mReviewList.setLayoutManager(layoutManager2);
        mReviewList.setHasFixedSize(true);

        this.retrofit = new Retrofit.Builder()
                .baseUrl(MovieService.IMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.movieService = this.retrofit.create(MovieService.class);

        Intent intentFromCaller = getIntent();
        mMovieDetail = intentFromCaller.getParcelableExtra("movieObj");
        if(mMovieDetail != null) {
            showDetail();

            Call<TrailerList> tmp = this.movieService.getMovieVideos(mMovieDetail.getId(), MovieService.API_KEY);
            tmp.enqueue(new Callback<TrailerList>() {
                @Override
                public void onResponse(Call<TrailerList> call, Response<TrailerList> response) {
                    if(response.isSuccessful()) {
                        List<Trailer> trailerList = Arrays.asList(response.body().getTrailers());
                        mMovieTrailers.addAll(trailerList);
                        mTrailerAdapter.notifyDataSetChanged();
                    } else {
                        try {
                            Log.e("XXX", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TrailerList> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            Call<ReviewList> movieReview = this.movieService.getMovieReviews(mMovieDetail.getId(), MovieService.API_KEY, 1);
            movieReview.enqueue(new Callback<ReviewList>() {
                @Override
                public void onResponse(Call<ReviewList> call, Response<ReviewList> response) {
                    if(response.isSuccessful()) {
                        List<Review> reviewList = Arrays.asList(response.body().getReviews());
                        mMovieReviews.addAll(reviewList);
                        mReviewAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("XXX", "Error "+response.raw().message());
                    }
                }

                @Override
                public void onFailure(Call<ReviewList> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            // movie duration is not available, so request for detail
            if(mMovieDetail.getRuntime() == null || mMovieDetail.getRuntime() == 0) {
                Call<Movie> detail = this.movieService.getMovieDetail(mMovieDetail.getId(), MovieService.API_KEY);
                detail.enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        if (response.isSuccessful()) {
                            mMovieDetail = response.body();
                            mMovieDetail.setPosterUrl("http://image.tmdb.org/t/p/w500" + mMovieDetail.getPosterUrl());
                            mMovieDetail.setBackdropPath("http://image.tmdb.org/t/p/w500" + mMovieDetail.getBackdropPath());
                            Log.d("XXX", "Movie detail loaded part 2: " + mMovieDetail.toString());
                            showDetail();
                        } else {
                            Log.e("XXX", "Error getting movie's detail " + response.errorBody().toString());
                            try {
                                Log.e("XXX", "Error getting movie's detail " + response.errorBody().string());
                            } catch (IOException e) {
                                Log.e("XXX", "Error getting detail movie");
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        }
    }

    public void setFavorite(View view) {
        if(!mFavorite.isChecked()) {
            Log.d("XXX", "Deleting from favorite");
            // remove from favorite
            Uri deleteItemUri = ContentUris.withAppendedId(MovieDbContract.MovieEntry.CONTENT_URI, mMovieDetail.getId());
            int result = getContentResolver().delete(deleteItemUri, null, null);
            if(result > 0) {
                // success removing from favorite
                mFavorite.setButtonDrawable(android.R.drawable.btn_star_big_off);
            } else {
                Toast.makeText(this, "Error removing from favorite", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("XXX", "Inserting into favorite");
            // add to favorite
            ContentValues cv = new ContentValues();
            cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_MOVIE_ID, mMovieDetail.getId());
            cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_TITLE, mMovieDetail.getTitle());
            cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_POSTER, mMovieDetail.getPosterUrl());
            cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_BACKDROP, mMovieDetail.getBackdropPath());
            cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_RATING, mMovieDetail.getRating());
            cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, mMovieDetail.getReleaseDate().getTime());
            cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_SYNOPSIS, mMovieDetail.getSynopsis());
            cv.put(MovieDbContract.MovieEntry.COLUMN_NAME_DURATION, mMovieDetail.getRuntime());

            Uri uri = getContentResolver().insert(MovieDbContract.MovieEntry.CONTENT_URI, cv);
            mFavorite.setButtonDrawable(android.R.drawable.btn_star_big_on);
        }
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
                .load(mMovieDetail.getBackdropPath())
                .into(mToolbarBackgroundImg);

        Cursor searchMovieInFavorite = getContentResolver().query(
                Uri.withAppendedPath(MovieDbContract.MovieEntry.CONTENT_URI, "favorite"),
                new String[] {MovieDbContract.MovieEntry.COLUMN_NAME_MOVIE_ID},
                MovieDbContract.MovieEntry.COLUMN_NAME_MOVIE_ID + "=? ",
                new String[] {String.valueOf(mMovieDetail.getId())}, null);

        if(searchMovieInFavorite != null && searchMovieInFavorite.getCount() > 0) {
            mFavorite.setChecked(true);
            mFavorite.setButtonDrawable(android.R.drawable.star_big_on);
        }
        searchMovieInFavorite.close();
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
}
