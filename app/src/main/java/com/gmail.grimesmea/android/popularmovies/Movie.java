package com.gmail.grimesmea.android.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    final static String MDB_TITLE = "original_title";
    final static String MDB_SYNOPSIS = "overview";
    final static String MDB_RELEASE_DATE = "release_date";
    final static String MDB_POPULARITY = "popularity";
    final static String MDB_RATING = "vote_average";
    final static String MDB_POSTER_PATH = "poster_path";
    final static String MDB_BACKDROP_PATH = "backdrop_path";
    String title;
    String synopsis;
    String releaseDate;
    String popularity;
    String rating;
    String posterImagePath;
    String backdropImagePath;

    public Movie(JSONObject movieJson) throws JSONException {

        this(
                movieJson.getString(MDB_TITLE),
                movieJson.getString(MDB_SYNOPSIS),
                movieJson.getString(MDB_RELEASE_DATE),
                movieJson.getString(MDB_POPULARITY),
                movieJson.getString(MDB_RATING),
                movieJson.getString(MDB_POSTER_PATH),
                movieJson.getString((MDB_BACKDROP_PATH))
        );
    }

    public Movie(String title, String synopsis, String releaseDate, String popularity, String rating,
                 String posterImagePath, String backdropPath) {

        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
        this.rating = rating;
        this.posterImagePath = posterImagePath;
        this.backdropImagePath = backdropPath;
    }

    private Movie(Parcel parcel) {
        title = parcel.readString();
        synopsis = parcel.readString();
        releaseDate = parcel.readString();
        popularity = parcel.readString();
        rating = parcel.readString();
        posterImagePath = parcel.readString();
        backdropImagePath = parcel.readString();
    }

    public String getImageUrl(String imagePath) {

        final String MOVIEDBPOSTER_BASE_URL = "http://image.tmdb.org/t/p/";

        String sizeParam = "w500";

        Uri builtUri = Uri.parse(MOVIEDBPOSTER_BASE_URL).buildUpon()
                .appendPath(sizeParam)
                .appendEncodedPath(imagePath)
                .build();

        return builtUri.toString();
    }

    public String getFormattedReleaseDate() throws ParseException {

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date inputDate = inputFormat.parse(releaseDate);

        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");
        String formattedReleaseDate = outputFormat.format(inputDate);

        return formattedReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(synopsis);
        parcel.writeString(releaseDate);
        parcel.writeString(popularity);
        parcel.writeString(rating);
        parcel.writeString(posterImagePath);
        parcel.writeString(backdropImagePath);
    }
}
