package com.tofallis.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {


    public static final String POSTER_PATH = "poster_path";

    // used for passing around the full image URL.
    public static final String IMG_URL = "img_url";

    public static final String TITLE = "original_title";
    public static final String OVERVIEW = "overview";
    public static final String VOTE = "vote_average";
    public static final String RELEASE_DATE = "release_date";

    String imageUrl;
    String original_title;
    String overview;
    String vote_average;
    String release_date;

    public Movie(String imgUrl, String title, String overview, String vote, String release) {
        this.imageUrl = imgUrl;
        this.original_title = title;
        this.overview = overview;
        this.vote_average = vote;
        this.release_date = release;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public void setOriginalTitle(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return vote_average;
    }

    public void setVoteAverage(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getImageUrl());
        dest.writeString(getOriginalTitle());
        dest.writeString(getOverview());
        dest.writeString(getVoteAverage());
        dest.writeString(getReleaseDate());
    }
}
