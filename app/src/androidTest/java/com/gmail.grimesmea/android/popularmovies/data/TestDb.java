package com.gmail.grimesmea.android.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;
import java.util.Set;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void setUp() {
        TestUtilities.deleteDatabase(mContext);
    }

    public void testCreateDb() throws Throwable {
        final Set<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MoviesContract.MoviesEntry.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.ReviewsEntry.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.VideosEntry.TABLE_NAME);

        SQLiteDatabase db = new MoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: The database has not been created correctly",
                cursor.moveToFirst());

        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while (cursor.moveToNext());
        assertTrue("Error: Database was created without the movies, reviews, and videos entry tables",
                tableNameHashSet.isEmpty());

        cursor.close();
        db.close();
    }

    public void testCreateMoviesTable() throws Throwable {
        SQLiteDatabase db = new MoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor cursor = db.rawQuery("PRAGMA table_info(" + MoviesContract.MoviesEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: Unable to query the database for movies table information",
                cursor.moveToFirst());

        final Set<String> moviesColumnHashSet = new HashSet<String>();
        moviesColumnHashSet.add(MoviesContract.MoviesEntry._ID);
        moviesColumnHashSet.add(MoviesContract.MoviesEntry.COLUMN_MDB_ID);
        moviesColumnHashSet.add(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE);
        moviesColumnHashSet.add(MoviesContract.MoviesEntry.COLUMN_MOVIE_SYNOPSIS);
        moviesColumnHashSet.add(MoviesContract.MoviesEntry.COLUMN_MOVIE_POPULARITY);
        moviesColumnHashSet.add(MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING);
        moviesColumnHashSet.add(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER_PATH);
        moviesColumnHashSet.add(MoviesContract.MoviesEntry.COLUMN_MOVIE_BACKDROP_PATH);
        moviesColumnHashSet.add(MoviesContract.MoviesEntry.COLUMN_FAVORITE);

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            moviesColumnHashSet.remove(columnName);
        } while (cursor.moveToNext());
        assertTrue("Error: The movies table does not contain all of the required columns",
                moviesColumnHashSet.isEmpty());

        cursor.close();
        db.close();
    }

    public void testCreateReviewsTable() throws Throwable {
        SQLiteDatabase db = new MoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor cursor = db.rawQuery("PRAGMA table_info(" + MoviesContract.ReviewsEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: Unable to query the database for reviews table information",
                cursor.moveToFirst());

        final Set<String> reviewsColumnHashSet = new HashSet<String>();
        reviewsColumnHashSet.add(MoviesContract.ReviewsEntry.COLUMN_MDB_ID);
        reviewsColumnHashSet.add(MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR);
        reviewsColumnHashSet.add(MoviesContract.ReviewsEntry.COLUMN_REVIEW_CONTENT);

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            reviewsColumnHashSet.remove(columnName);
        } while (cursor.moveToNext());
        assertTrue("Error: The reviews table does not contain all of the required columns",
                reviewsColumnHashSet.isEmpty());

        cursor.close();
        db.close();
    }

    public void testCreateVideosTable() throws Throwable {
        SQLiteDatabase db = new MoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor cursor = db.rawQuery("PRAGMA table_info(" + MoviesContract.VideosEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: Unable to query the database for videos table information",
                cursor.moveToFirst());

        final Set<String> videosColumnHashSet = new HashSet<String>();
        videosColumnHashSet.add(MoviesContract.VideosEntry.COLUMN_MDB_ID);
        videosColumnHashSet.add(MoviesContract.VideosEntry.COLUMN_VIDEO_TYPE);
        videosColumnHashSet.add(MoviesContract.VideosEntry.COLUMN_VIDEO_NAME);
        videosColumnHashSet.add(MoviesContract.VideosEntry.COLUMN_VIDEO_SIZE);
        videosColumnHashSet.add(MoviesContract.VideosEntry.COLUMN_VIDEO_SITE);
        videosColumnHashSet.add(MoviesContract.VideosEntry.COLUMN_VIDEO_KEY);

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            videosColumnHashSet.remove(columnName);
        } while (cursor.moveToNext());
        assertTrue("Error: The videos table does not contain all of the required columns",
                videosColumnHashSet.isEmpty());

        cursor.close();
        db.close();
    }

    public long testInsertMovie() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testMovieValues = TestUtilities.createMovieValues();

        long movieRowId = TestUtilities.insertMovieValuesIntoDb(db, testMovieValues);
        assertTrue("Error: Failed to insert test movies values", movieRowId != -1);

        Cursor cursor = db.query(
                MoviesContract.MoviesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: Query returned no records", cursor.moveToFirst());


        TestUtilities.validateCurrentRecord("testInsertMovie",
                cursor, testMovieValues);
        assertFalse("Error: More than one record returned by query",
                cursor.moveToNext());

        cursor.close();
        db.close();
        return movieRowId;
    }

    public long testInsertReview() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testMovieValues = TestUtilities.createMovieValues();
        ContentValues testReviewValues = TestUtilities.createReviewValues();

        TestUtilities.insertMovieValuesIntoDb(db, testMovieValues);
        long reviewRowId = TestUtilities.insertReviewValuesIntoDb(db, testReviewValues);
        assertTrue("Error: Failed to insert test review values", reviewRowId != -1);

        Cursor cursor = db.query(
                MoviesContract.ReviewsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: Query returned no records", cursor.moveToFirst());


        TestUtilities.validateCurrentRecord("testInsertReview",
                cursor, testReviewValues);
        assertFalse("Error: More than one record returned by query",
                cursor.moveToNext());

        cursor.close();
        db.close();
        return reviewRowId;
    }

    public long testInsertVideo() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testMovieValues = TestUtilities.createMovieValues();
        ContentValues testVideoValues = TestUtilities.createVideoValues();

        TestUtilities.insertMovieValuesIntoDb(db, testMovieValues);
        long videoRowId = TestUtilities.insertVideoValuesIntoDb(db, testVideoValues);
        assertTrue("Error: Failed to insert test video values", videoRowId != -1);

        Cursor cursor = db.query(
                MoviesContract.VideosEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: Query returned no records", cursor.moveToFirst());


        TestUtilities.validateCurrentRecord("testInsertVideo",
                cursor, testVideoValues);
        assertFalse("Error: More than one record returned by query",
                cursor.moveToNext());

        cursor.close();
        db.close();
        return videoRowId;
    }
}
