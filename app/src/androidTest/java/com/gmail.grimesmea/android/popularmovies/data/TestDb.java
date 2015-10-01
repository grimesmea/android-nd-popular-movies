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
        deleteDatabase();
    }

    void deleteDatabase() {
        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }

    public void testCreateDb() throws Throwable {
        final Set<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MoviesContract.MoviesEntry.TABLE_NAME);

        SQLiteDatabase db = new MoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: The database has not been created correctly",
                cursor.moveToFirst());

        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while (cursor.moveToNext());


        assertTrue("Error: Database was created without the movies entry table",
                tableNameHashSet.isEmpty());

        cursor = db.rawQuery("PRAGMA table_info(" + MoviesContract.MoviesEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the database for table information",
                cursor.moveToFirst());

        final Set<String> moviesColumnHashSet = new HashSet<String>();
        moviesColumnHashSet.add(MoviesContract.MoviesEntry._ID);
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

        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                moviesColumnHashSet.isEmpty());
        db.close();
    }

    public long testInsertMovie() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();

        long movieRowId = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, testValues);

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
                cursor, testValues);

        assertFalse("Error: More than one record returned by query",
                cursor.moveToNext());

        cursor.close();
        db.close();
        return movieRowId;
    }
}
