package com.gmail.grimesmea.android.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.gmail.grimesmea.android.popularmovies.data.MoviesContract.MoviesEntry;


public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MoviesEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movies table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MoviesProvider.class.getName());
        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: MoviesProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MoviesContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MoviesContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: MoviesProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        String moviesUriType = mContext.getContentResolver().getType(MoviesEntry.CONTENT_URI);
        assertEquals("Error: the MoviesEntry CONTENT_URI should return MoviesEntry.CONTENT_TYPE",
                MoviesEntry.CONTENT_TYPE, moviesUriType);

        String movieUriType = mContext.getContentResolver().getType(MoviesEntry.buildMovieUri(1));
        assertEquals("Error: the MoviesEntry movie URI should return MoviesEntry.CONTENT_ITEM_TYPE",
                MoviesEntry.CONTENT_ITEM_TYPE, movieUriType);

        String favoritesUriType = mContext.getContentResolver().getType(MoviesEntry.buildFavoriteMoviesUri());
        assertEquals("Error: the MoviesEntry favorite movies URI should return MoviesEntry.CONTENT_TYPE",
                MoviesEntry.CONTENT_TYPE, favoritesUriType);

    }

    public void testQuery() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();

        long movieRowId = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failed to insert test movies values", movieRowId != -1);

        // Test with MOVIES URI
        Cursor movieCursor = mContext.getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("basic movies query ", movieCursor, testValues);

        // Test with MOVIE URI
        movieCursor = mContext.getContentResolver().query(
                MoviesEntry.buildFavoriteMoviesUri(),
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("favorites query", movieCursor, testValues);

        // Test with FAVORITES URI
        movieCursor = mContext.getContentResolver().query(
                MoviesEntry.buildMovieUri(movieRowId),
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("movie by id query ", movieCursor, testValues);

        movieCursor.close();
        db.close();
    }

    public void testInsert() {
        ContentValues testValues = TestUtilities.createMovieValues();

        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesEntry.CONTENT_URI, true, testContentObserver);

        Uri movieUri = mContext.getContentResolver().insert(MoviesEntry.CONTENT_URI, testValues);

        testContentObserver.waitForNotificationOrFail();


        long movieRowId = ContentUris.parseId(movieUri);

        assertTrue(movieRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("movie update ", cursor, testValues);

        mContext.getContentResolver().unregisterContentObserver(testContentObserver);
        testContentObserver.closeHandlerThread();
        cursor.close();
    }

    public void testUpdate() {
        ContentValues testValues = TestUtilities.createMovieValues();

        Uri moviesUri = mContext.getContentResolver().insert(MoviesEntry.CONTENT_URI, testValues);
        long movieRowId = ContentUris.parseId(moviesUri);

        assertTrue(movieRowId != -1);

        ContentValues updatedValues = new ContentValues(testValues);
        updatedValues.put(MoviesEntry._ID, movieRowId);
        updatedValues.put(MoviesEntry.COLUMN_MOVIE_TITLE, "Updated Test Movie Title 1");

        Cursor cursor = mContext.getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        cursor.registerContentObserver(testContentObserver);

        // Test with MOVIES URI
        int count = mContext.getContentResolver().update(
                MoviesEntry.CONTENT_URI, updatedValues, MoviesEntry._ID + "= ?",
                new String[]{Long.toString(movieRowId)}
        );
        assertEquals(1, count);
        testContentObserver.waitForNotificationOrFail();

        cursor = mContext.getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("movies update ", cursor, updatedValues);

        // Test with MOVIE URI
        cursor.registerContentObserver(testContentObserver);
        updatedValues.put(MoviesEntry.COLUMN_MOVIE_TITLE, "Updated Test Movie Title 2");

        count = mContext.getContentResolver().update(
                MoviesEntry.buildMovieUri(movieRowId), updatedValues, null, null
        );
        assertEquals(1, count);
        testContentObserver.waitForNotificationOrFail();

        cursor = mContext.getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("movie update ", cursor, updatedValues);

        // Test with FAVORITES URI
        cursor.registerContentObserver(testContentObserver);
        updatedValues.put(MoviesEntry.COLUMN_MOVIE_TITLE, "Updated Test Movie Title 3");

        count = mContext.getContentResolver().update(
                MoviesEntry.buildFavoriteMoviesUri(), updatedValues, null, null
        );
        assertEquals(1, count);
        testContentObserver.waitForNotificationOrFail();

        cursor = mContext.getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("favorites update ", cursor, updatedValues);

        mContext.getContentResolver().unregisterContentObserver(testContentObserver);
        testContentObserver.closeHandlerThread();
        cursor.close();
    }

    public void testDelete() {
        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesEntry.CONTENT_URI, true, testContentObserver);

        // Test with MOVIES URI
        testInsert();

        mContext.getContentResolver().delete(
                MoviesEntry.CONTENT_URI,
                null,
                null
        );
        testContentObserver.waitForNotificationOrFail();

        Cursor cursor = mContext.getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movies table during movies delete", 0, cursor.getCount());

        // Test with MOVIE URI
        testInsert();

        mContext.getContentResolver().delete(
                MoviesEntry.CONTENT_URI,
                null,
                null
        );
        testContentObserver.waitForNotificationOrFail();

        cursor = mContext.getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movies table during movie delete", 0, cursor.getCount());

        // Test with FAVORITES URI
        testInsert();

        mContext.getContentResolver().delete(
                MoviesEntry.buildMovieUri(1),
                null,
                null
        );
        testContentObserver.waitForNotificationOrFail();

        cursor = mContext.getContentResolver().query(
                MoviesEntry.buildFavoriteMoviesUri(),
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movies table during favorites delete", 0, cursor.getCount());

        mContext.getContentResolver().unregisterContentObserver(testContentObserver);
        testContentObserver.closeHandlerThread();
        cursor.close();
    }

    public void testBulkInsert() {
        ContentValues[] bulkInsertContentValues = TestUtilities.createBulkInsertMoviesValues();

        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesEntry.CONTENT_URI, true, testContentObserver);

        Log.d(LOG_TAG, bulkInsertContentValues.toString());

        int insertCount = mContext.getContentResolver().bulkInsert(MoviesEntry.CONTENT_URI, bulkInsertContentValues);

        testContentObserver.waitForNotificationOrFail();

        assertEquals(insertCount, TestUtilities.BULK_INSERT_RECORDS_TO_INSERT);

        Cursor cursor = mContext.getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                MoviesEntry.COLUMN_MOVIE_POPULARITY + " DESC"
        );

        assertEquals(cursor.getCount(), TestUtilities.BULK_INSERT_RECORDS_TO_INSERT);

        cursor.moveToFirst();
        for (int i = 0; i < TestUtilities.BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("bulk insert " + i,
                    cursor, bulkInsertContentValues[i]);
        }

        mContext.getContentResolver().unregisterContentObserver(testContentObserver);
        testContentObserver.closeHandlerThread();
        cursor.close();
    }
}
