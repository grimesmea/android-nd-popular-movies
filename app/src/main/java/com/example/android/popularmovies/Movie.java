package com.example.android.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie {

    final static String MDB_TITLE = "original_title";
    final static String MDB_SYNOPSIS = "overview";
    final static String MDB_POPULARITY = "popularity";
    final static String MDB_RATING = "vote_average";
    final static String MDB_POSTER_PATH = "poster_path";

    String title;
    String synopsis;
    String popularity;
    String rating;
    String posterImagePath;

    public Movie(JSONObject movieJson) throws JSONException {

        this(
                movieJson.getString(MDB_TITLE),
                movieJson.getString(MDB_SYNOPSIS),
                movieJson.getString(MDB_POPULARITY),
                movieJson.getString(MDB_RATING),
                movieJson.getString(MDB_POSTER_PATH)
        );
    }

    public Movie(String title, String synopsis, String popularity, String rating, String posterImagePath) {

        this.title = title;
        this.synopsis = synopsis;
        this.popularity = popularity;
        this.rating = rating;
        this.posterImagePath = posterImagePath;
    }

}
