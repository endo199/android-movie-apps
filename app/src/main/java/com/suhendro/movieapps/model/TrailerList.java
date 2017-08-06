package com.suhendro.movieapps.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Suhendro on 8/6/2017.
 */

public class TrailerList {
    private int id;
    @SerializedName("results")
    private Trailer[] trailers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Trailer[] getTrailers() {
        return trailers;
    }

    public void setTrailers(Trailer[] trailers) {
        this.trailers = trailers;
    }
}
