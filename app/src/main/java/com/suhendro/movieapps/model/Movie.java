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

    private boolean adult;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("belongs_to_collection")
    private MovieCollection collection;
    private int budget;
    private Genre[] genres;
    private String homepage;
    @SerializedName("imdb_id")
    private String imdbID; // ada 3 validasi
    @SerializedName("original_language")
    private String originalLanguage;
    private float popularity;
    @SerializedName("production_companies")
    private Company[] productionCompanies;
    @SerializedName("production_countries")
    private Country[] productionCountries;
    private int revenue;
    @SerializedName("spoken_languages")
    private Language[] spokenLanguage;
    private String status;
    private String tagline;
    private boolean video;
    @SerializedName("vote_count")
    private int voteCount;

    public Movie() {}
    public Movie(Parcel parcel) {
        this.id = parcel.readLong();
        this.title = parcel.readString();
        this.rating = parcel.readFloat();
        this.posterUrl = parcel.readString();
        this.backdropPath = parcel.readString();
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

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public MovieCollection getCollection() {
        return collection;
    }

    public void setCollection(MovieCollection collection) {
        this.collection = collection;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public Genre[] getGenres() {
        return genres;
    }

    public void setGenres(Genre[] genres) {
        this.genres = genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public Company[] getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(Company[] productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public Country[] getProductionCountries() {
        return productionCountries;
    }

    public void setProductionCountries(Country[] productionCountries) {
        this.productionCountries = productionCountries;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public Language[] getSpokenLanguage() {
        return spokenLanguage;
    }

    public void setSpokenLanguage(Language[] spokenLanguage) {
        this.spokenLanguage = spokenLanguage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
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
        parcel.writeString(this.backdropPath);
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

    public class MovieCollection {
        private int id;
        private String name;
        @SerializedName("poster_path")
        private String posterPath;
        @SerializedName("backdropPath")
        private String backdropPath;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public String getBackdropPath() {
            return backdropPath;
        }

        public void setBackdropPath(String backdropPath) {
            this.backdropPath = backdropPath;
        }
    }

    public class Language {
        @SerializedName("iso_639_1")
        private String isoCode;
        private String name;

        public String getIsoCode() {
            return isoCode;
        }

        public void setIsoCode(String isoCode) {
            this.isoCode = isoCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Country {
        @SerializedName("iso_3166_1")
        private String isoCode;
        private String name;

        public String getIsoCode() {
            return isoCode;
        }

        public void setIsoCode(String isoCode) {
            this.isoCode = isoCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Company {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Genre {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
