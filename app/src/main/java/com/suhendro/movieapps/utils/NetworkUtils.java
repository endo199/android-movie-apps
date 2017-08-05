package com.suhendro.movieapps.utils;

import android.net.Uri;
import android.util.Log;

import com.suhendro.movieapps.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Created by Suhendro on 6/27/2017.
 */

public class NetworkUtils {
    static final String API_KEY = BuildConfig.TMDB_API_KEY;
    static final String IMDB_BASE_URL = "https://api.themoviedb.org";
    static final String PARAM_API_KEY = "api_key";
    static final String PARAM_PAGE = "page";

    public static Uri buildUri(String sortBy, int page) {
        Uri uri = Uri.parse(IMDB_BASE_URL).buildUpon()
                .path("3/movie/"+sortBy)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_PAGE, Integer.toString(page))
                .build();

        Log.i("XXX", "URI: "+uri.toString());

        return uri;
    }

    public static Uri buildMovieUri(Long id) {
        Uri uri = Uri.parse(IMDB_BASE_URL).buildUpon()
                .path("3/movie/"+id)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        Log.i("XXX", "URI: "+uri.toString());

        return uri;
    }

    public static Uri buildMovieTrailerListUri(Long movieId) {
        Uri uri = Uri.parse(IMDB_BASE_URL).buildUpon()
                .path("3/movie/"+movieId+"/videos")
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        Log.i("XXX", "URI: "+uri.toString());

        return uri;
    }

    public static Uri buildMovieReviewsUri(Long movieId) {
        Uri uri = Uri.parse(IMDB_BASE_URL).buildUpon()
                .path("3/movie/"+movieId+"/reviews")
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        Log.i("XXX", "URI: "+uri.toString());

        return uri;
    }

    public static String getHttpResponse(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            InputStream in = conn.getInputStream();

            Scanner scan = new Scanner(in);
            scan.useDelimiter("\\A");

            boolean hasInput = scan.hasNext();
            if(hasInput) {
                return scan.next();
            } else {
                Log.d("XXX", "No input data from server");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("XXX", "Error fetching data from server");
        } finally {
            if(conn != null)
                conn.disconnect();
        }

        return null;
    }
}
