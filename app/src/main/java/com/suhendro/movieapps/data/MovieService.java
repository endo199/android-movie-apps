package com.suhendro.movieapps.data;

import com.suhendro.movieapps.BuildConfig;
import com.suhendro.movieapps.model.Movie;
import com.suhendro.movieapps.model.MovieList;
import com.suhendro.movieapps.model.ReviewList;
import com.suhendro.movieapps.model.Trailer;
import com.suhendro.movieapps.model.TrailerList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Suhendro on 8/5/2017.
 */

public interface MovieService {
    static final String API_KEY = BuildConfig.TMDB_API_KEY;
    static final String IMDB_BASE_URL = "https://api.themoviedb.org/";

    @GET("3/movie/{sortBy}")
    Call<MovieList> getMovies(@Path("sortBy") String sortBy, @Query("api_key") String key, @Query("page") int page);

    @GET("3/movie/{id}")
    Call<Movie> getMovieDetail(@Path("id") long id, @Query("api_key") String key);

    @GET("3/movie/{id}/reviews")
    Call<ReviewList> getMovieReviews(@Path("id") long id, @Query("api_key") String key, @Query("page") int page);

    @GET("3/movie/{id}/videos")
    Call<TrailerList> getMovieVideos(@Path("id") long id, @Query("api_key") String key);
}
