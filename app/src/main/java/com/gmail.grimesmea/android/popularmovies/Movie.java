package com.gmail.grimesmea.android.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.gmail.grimesmea.android.popularmovies.data.MoviesContract;

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

    final static String MDB_ID = "id";
    final static String MDB_TITLE = "original_title";
    final static String MDB_SYNOPSIS = "overview";
    final static String MDB_RELEASE_DATE = "release_date";
    final static String MDB_POPULARITY = "popularity";
    final static String MDB_RATING = "vote_average";
    final static String MDB_POSTER_PATH = "poster_path";
    final static String MDB_BACKDROP_PATH = "backdrop_path";

    int mdbId;
    String title;
    String synopsis;
    String releaseDate;
    String popularity;
    String rating;
    String posterImagePath;
    String backdropImagePath;
    Boolean isFavorite;

    public Movie(JSONObject movieJson) throws JSONException {
        this(
                movieJson.getInt(MDB_ID),
                movieJson.getString(MDB_TITLE),
                movieJson.getString(MDB_SYNOPSIS),
                movieJson.getString(MDB_RELEASE_DATE),
                movieJson.getString(MDB_POPULARITY),
                movieJson.getString(MDB_RATING),
                movieJson.getString(MDB_POSTER_PATH),
                movieJson.getString(MDB_BACKDROP_PATH),
                false
        );
    }

    public Movie(Cursor movieCursor) {
        this(
                movieCursor.getInt(MainFragment.COL_MDB_ID),
                movieCursor.getString(MainFragment.COL_MOVIE_TITLE),
                movieCursor.getString(MainFragment.COL_MOVIE_SYNOPSIS),
                movieCursor.getString(MainFragment.COL_MOVIE_RELEASE_DATE),
                movieCursor.getString(MainFragment.COL_MOVIE_POPULARITY),
                movieCursor.getString(MainFragment.COL_MOVIE_RATING),
                movieCursor.getString(MainFragment.COL_MOVIE_POSTER_PATH),
                movieCursor.getString(MainFragment.COL_MOVIE_BACKDROP_PATH),
                getBooleanValue(movieCursor.getInt(MainFragment.COL_FAVORITE))
        );
    }

    public Movie(int mdbId, String title, String synopsis, String releaseDate, String popularity, String rating,
                 String posterImagePath, String backdropPath, boolean isFavorite) {
        this.mdbId = mdbId;
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
        this.rating = rating;
        this.posterImagePath = posterImagePath;
        this.backdropImagePath = backdropPath;
        this.isFavorite = isFavorite;
    }

    private Movie(Parcel parcel) {
        mdbId = parcel.readInt();
        title = parcel.readString();
        synopsis = parcel.readString();
        releaseDate = parcel.readString();
        popularity = parcel.readString();
        rating = parcel.readString();
        posterImagePath = parcel.readString();
        backdropImagePath = parcel.readString();
        isFavorite = parcel.readByte() != 0;
    }

    private static boolean getBooleanValue(int i) {
        return i == 1 ? true : false;

    }

    public ContentValues createContentValues() {
        ContentValues movieValues = new ContentValues();

        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MDB_ID, mdbId);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE, title);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_SYNOPSIS, synopsis);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POPULARITY, popularity);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING, rating);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER_PATH, posterImagePath);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_BACKDROP_PATH, backdropImagePath);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, isFavorite);

        return movieValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mdbId);
        parcel.writeString(title);
        parcel.writeString(synopsis);
        parcel.writeString(releaseDate);
        parcel.writeString(popularity);
        parcel.writeString(rating);
        parcel.writeString(posterImagePath);
        parcel.writeString(backdropImagePath);
        parcel.writeByte((byte) (isFavorite ? 1 : 0));
    }

    public String getPosterImageUrl() {
        final String MOVIEDBPOSTER_BASE_URL = "http://image.tmdb.org/t/p/";

        String sizeParam = "w500";

        Uri builtUri = Uri.parse(MOVIEDBPOSTER_BASE_URL).buildUpon()
                .appendPath(sizeParam)
                .appendEncodedPath(posterImagePath)
                .build();

        return builtUri.toString();
    }

    public String getBackdropImageUrl() {
        final String MOVIEDBPOSTER_BASE_URL = "http://image.tmdb.org/t/p/";

        String sizeParam = "w500";

        Uri builtUri = Uri.parse(MOVIEDBPOSTER_BASE_URL).buildUpon()
                .appendPath(sizeParam)
                .appendEncodedPath(backdropImagePath)
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

    public int getMdbId() {
        return mdbId;
    }

    public String getTitle() {
        return title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getRating() {
        return rating;
    }

    public String getPosterImagePath() {
        return posterImagePath;
    }

    public String getBackdropImagePath() {
        return backdropImagePath;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }
}
