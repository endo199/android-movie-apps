package com.suhendro.movieapps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Suhendro on 6/28/2017.
 */

public class Movie implements Parcelable {

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
    public Movie(Parcel parcel) {
        this.id = parcel.readLong();
        this.title = parcel.readString();
        this.rating = parcel.readFloat();
        this.posterUrl = parcel.readString();
        this.synopsis = parcel.readString();
        this.runtime = parcel.readInt();
        this.releaseDate = new Date(parcel.readLong());
    }

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

    @Override
    public String toString() {
        return this.title + "["+this.id+"] with poster "+this.posterUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(this.id);
        parcel.writeString(this.title);
        parcel.writeFloat(this.rating);
        parcel.writeString(this.posterUrl);
        parcel.writeString(this.synopsis);
        parcel.writeInt(this.runtime != null ? this.runtime : 0);
        parcel.writeLong(this.releaseDate.getTime());
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[0];
        }
    };
}
