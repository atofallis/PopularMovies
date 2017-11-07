package com.tofallis.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    /*
        "id":"59afaa2cc3a3682e6401064f",
        "author":"Screen-Space",
        "content":"....",
        "url":"https://www.themoviedb.org/review/59afaa2cc3a3682e6401064f"
     */
    public static final String ID = "id";
    public static final String AUTHOR = "author";
    public static final String CONTENT = "content";
    public static final String URL = "url";

    String id;
    String author;
    String content;
    String url;

    public Review(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    protected Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getAuthor());
        dest.writeString(getContent());
        dest.writeString(getUrl());
    }
}
