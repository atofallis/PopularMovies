package com.tofallis.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    /*
    "id":"5979004892514107d100100a",
    "iso_639_1":"en",
    "iso_3166_1":"US",
    "key":"c44kam_8N3I",
    "name":"Official Trailer",
    "site":"YouTube",
    "size":1080,
    "type":"Trailer"
     */
    public static final String ID = "id";
    public static final String KEY = "key";

    String id;
    String key;

    public Trailer(String id, String key) {
        this.id = id;
        this.key = key;
    }

    protected Trailer(Parcel in) {
        id = in.readString();
        key = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getKey());
    }
}
