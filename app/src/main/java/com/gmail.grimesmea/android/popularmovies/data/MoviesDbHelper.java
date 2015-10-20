package com.gmail.grimesmea.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.MoviesEntry.COLUMN_MDB_ID + " INTEGER UNIQUE NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE + " TEXT UNIQUE NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE + " REAL NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_POPULARITY + " REAL NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT UNIQUE NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT UNIQUE NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_FAVORITE + " INTEGER NOT NULL" +
                " );";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MoviesContract.ReviewsEntry.TABLE_NAME + " (" +
                MoviesContract.ReviewsEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.MoviesEntry.COLUMN_MDB_ID + " INTEGER NOT NULL, " +
                MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                MoviesContract.ReviewsEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + MoviesContract.ReviewsEntry.COLUMN_MDB_ID + ") REFERENCES " +
                MoviesContract.MoviesEntry.TABLE_NAME + "(" +
                MoviesContract.MoviesEntry.COLUMN_MDB_ID + ")" +
                ");";

        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + MoviesContract.VideosEntry.TABLE_NAME + " (" +
                MoviesContract.VideosEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.VideosEntry.COLUMN_MDB_ID + " INTEGER NOT NULL, " +
                MoviesContract.VideosEntry.COLUMN_VIDEO_TYPE + " TEXT NOT NULL, " +
                MoviesContract.VideosEntry.COLUMN_VIDEO_NAME + " TEXT NOT NULL, " +
                MoviesContract.VideosEntry.COLUMN_VIDEO_SIZE + " INTEGER NOT NULL, " +
                MoviesContract.VideosEntry.COLUMN_VIDEO_SITE + " TEXT NOT NULL, " +
                MoviesContract.VideosEntry.COLUMN_VIDEO_KEY + " TEXT UNIQUE NOT NULL, " +
                "FOREIGN KEY(" + MoviesContract.VideosEntry.COLUMN_MDB_ID + ") REFERENCES " +
                MoviesContract.MoviesEntry.TABLE_NAME + "(" +
                MoviesContract.MoviesEntry.COLUMN_MDB_ID + ")" +
                ");";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_VIDEOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.VideosEntry.TABLE_NAME);
        onCreate(db);
    }
}
