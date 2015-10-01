package com.gmail.grimesmea.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY," +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE + " TEXT UNIQUE NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE + " REAL NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_POPULARITY + " REAL NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT UNIQUE NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT UNIQUE NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_FAVORITE + " INTEGER NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
