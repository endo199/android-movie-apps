package com.suhendro.movieapps.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Suhendro on 6/28/2017.
 */

public class Movie {

    private Long id;
    @SerializedName("original_title")
    private String title;
    @SerializedName("vote_average")
    private Float rating;
    @SerializedName("poster_path")
    private String posterUrl;
    @SerializedName("overview")
    private String synopsis;
    @SerializedName("release_date")
    private Date releaseDate;
    private Integer runtime;

    public Movie() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }
}
