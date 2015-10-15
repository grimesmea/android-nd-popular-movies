package com.gmail.grimesmea.android.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.gmail.grimesmea.android.popularmovies.data.MoviesContract.MoviesEntry;


public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestUtilities.deleteAllRecordsFromProvider(mContext);
    }

    public void testDeleteReviewsRecordsFromProvider() {
        TestUtilities.deleteReviewsRecordsFromProvider(mContext);

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Reviews table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void testDeleteMoviesRecordsFromProvider() {
        TestUtilities.deleteMoviesRecordsFromProvider(mContext);

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
        String moviesUriType = mContext.getContentResolver().getType(MoviesContract.MoviesEntry.CONTENT_URI);
        assertEquals("Error: the MoviesEntry CONTENT_URI should return MoviesEntry.CONTENT_TYPE",
                MoviesContract.MoviesEntry.CONTENT_TYPE, moviesUriType);

        String movieUriType = mContext.getContentResolver().getType(MoviesContract.MoviesEntry.buildMovieUri(1));
        assertEquals("Error: the MoviesEntry movie URI should return MoviesEntry.CONTENT_ITEM_TYPE",
                MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE, movieUriType);

        String favoritesUriType = mContext.getContentResolver().getType(MoviesContract.MoviesEntry.buildFavoriteMoviesUri());
        assertEquals("Error: the MoviesEntry favorite movies URI should return MoviesEntry.CONTENT_TYPE",
                MoviesContract.MoviesEntry.CONTENT_TYPE, favoritesUriType);

        String reviewsUriType = mContext.getContentResolver().getType(MoviesContract.ReviewsEntry.CONTENT_URI);
        assertEquals("Error: the ReviewsEntry CONTENT_URI should return ReviewsEntry.CONTENT_TYPE",
                MoviesContract.ReviewsEntry.CONTENT_TYPE, reviewsUriType);

        String reviewUriType = mContext.getContentResolver().getType(MoviesContract.ReviewsEntry.buildReviewUri(1));
        assertEquals("Error: the ReviewsEntry review URI should return ReviewsEntry.CONTENT_ITEM_TYPE",
                MoviesContract.ReviewsEntry.CONTENT_ITEM_TYPE, reviewUriType);

        String reviewsForMovieUriType = mContext.getContentResolver().getType(MoviesContract.ReviewsEntry.buildReviewsForMovieUri(1));
        assertEquals("Error: the ReviewsEntry reviewsForMovie URI should return MoviesEntry.CONTENT_TYPE",
                MoviesContract.ReviewsEntry.CONTENT_TYPE, reviewsForMovieUriType);

    }

    public void testInsertMovies() {
        ContentValues testValues = TestUtilities.createMovieValues(2);

        TestUtilities.deleteAllRecordsFromProvider(mContext);
        long movieRowId = TestUtilities.insertMoviesIntoProvider(mContext, testValues);

        assertTrue(movieRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("movie update ", cursor, testValues);

        cursor.close();
    }

    public void testInsertReviews() {
        ContentValues testValues = TestUtilities.createReviewValues(2);

        TestUtilities.deleteAllRecordsFromProvider(mContext);
        long reviewRowId = TestUtilities.insertReviewsIntoProvider(mContext, testValues);

        assertTrue(reviewRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("reviews update ", cursor, testValues);

        cursor.close();
    }

    public void testMoviesQuery() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        TestUtilities.deleteAllRecordsFromProvider(mContext);
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
                MoviesEntry.buildMovieUri(movieRowId),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("movie by id query ", movieCursor, testValues);

        // Test with FAVORITES URI
        movieCursor = mContext.getContentResolver().query(
                MoviesEntry.buildFavoriteMoviesUri(),
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("favorites query", movieCursor, testValues);

        movieCursor.close();
        db.close();
    }

    public void testReviewsQuery() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        TestUtilities.deleteAllRecordsFromProvider(mContext);
        ContentValues testValues = TestUtilities.createReviewValues();
        int mdbIdTestValue = TestUtilities.TEST_MDB_ID;

        long reviewRowId = TestUtilities.insertReviewsIntoProvider(mContext, testValues);
        assertTrue("Error: Failed to insert test reviews values", reviewRowId != -1);

        // Test with REVIEWS URI
        Cursor reviewCursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("reviews query ", reviewCursor, testValues);

        // Test with REVIEW URI
        reviewCursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.buildReviewUri(reviewRowId),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("review by id query", reviewCursor, testValues);

        // Test with REVIEWS_FOR_MOVIE URI
        reviewCursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.buildReviewsForMovieUri(mdbIdTestValue),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("reviews for movie query ", reviewCursor, testValues);

        reviewCursor.close();
        db.close();
    }

    public void testUpdateMovies() {
        ContentValues testValues = TestUtilities.createMovieValues();

        TestUtilities.deleteAllRecordsFromProvider(mContext);
        long movieRowId = TestUtilities.insertMoviesIntoProvider(mContext, testValues);
        assertTrue("Error: Failed to insert test reviews values", movieRowId != -1);

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
        cursor.unregisterContentObserver(testContentObserver);

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
        cursor.unregisterContentObserver(testContentObserver);

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
        cursor.unregisterContentObserver(testContentObserver);

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

    public void testUpdateReviews() {
        ContentValues testValues = TestUtilities.createReviewValues();
        int mdbIdTestValue = TestUtilities.TEST_MDB_ID;

        TestUtilities.deleteAllRecordsFromProvider(mContext);
        long reviewRowId = TestUtilities.insertReviewsIntoProvider(mContext, testValues);
        assertTrue("Error: Failed to insert test review values", reviewRowId != -1);

        ContentValues updatedValues = new ContentValues(testValues);
        updatedValues.put(MoviesContract.ReviewsEntry._ID, reviewRowId);
        updatedValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR, "Updated Review Author");

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        cursor.registerContentObserver(testContentObserver);

        // Test with REVIEWS URI
        int count = mContext.getContentResolver().update(
                MoviesContract.ReviewsEntry.CONTENT_URI, updatedValues, MoviesContract.ReviewsEntry._ID + "= ?",
                new String[]{Long.toString(reviewRowId)}
        );
        assertEquals(1, count);
        testContentObserver.waitForNotificationOrFail();
        cursor.unregisterContentObserver(testContentObserver);

        cursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("reviews update ", cursor, updatedValues);

        // Test with REVIEW URI
        cursor.registerContentObserver(testContentObserver);
        updatedValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR, "Updated Review Author 2");

        count = mContext.getContentResolver().update(
                MoviesContract.ReviewsEntry.buildReviewUri(reviewRowId), updatedValues, null, null
        );
        assertEquals(1, count);
        testContentObserver.waitForNotificationOrFail();
        cursor.unregisterContentObserver(testContentObserver);

        cursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("review update ", cursor, updatedValues);

        // Test with REVIEWS_FOR_MOVIE URI
        cursor.registerContentObserver(testContentObserver);
        updatedValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR, "Updated Review Author 3");

        count = mContext.getContentResolver().update(
                MoviesContract.ReviewsEntry.buildReviewsForMovieUri(mdbIdTestValue), updatedValues, null, null
        );
        assertEquals(1, count);
        testContentObserver.waitForNotificationOrFail();
        cursor.unregisterContentObserver(testContentObserver);

        cursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("reviews for movie update ", cursor, updatedValues);

        mContext.getContentResolver().unregisterContentObserver(testContentObserver);
        testContentObserver.closeHandlerThread();
        cursor.close();
    }

    public void testDeleteReviews() {
        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.ReviewsEntry.CONTENT_URI, true, testContentObserver);
        ContentValues testValues = TestUtilities.createReviewValues();
        long reviewRowId;
        int mdbIdTestValue = TestUtilities.TEST_MDB_ID;

        // Test with REVIEWS URI
        TestUtilities.deleteAllRecordsFromProvider(mContext);
        reviewRowId = TestUtilities.insertReviewsIntoProvider(mContext, testValues);

        mContext.getContentResolver().delete(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null
        );
        testContentObserver.waitForNotificationOrFail();

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Reviews table during reviews delete", 0, cursor.getCount());

        // Test with REVIEW URI
        reviewRowId = TestUtilities.insertReviewsIntoProvider(mContext, testValues);

        mContext.getContentResolver().delete(
                MoviesContract.ReviewsEntry.buildReviewUri(reviewRowId),
                null,
                null
        );
        testContentObserver.waitForNotificationOrFail();

        cursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Reviews table during review delete", 0, cursor.getCount());

        // Test with REVIEWS_FOR_MOVIE URI
        reviewRowId = TestUtilities.insertReviewsIntoProvider(mContext, testValues);

        mContext.getContentResolver().delete(
                MoviesContract.ReviewsEntry.buildReviewsForMovieUri(mdbIdTestValue),
                null,
                null
        );
        testContentObserver.waitForNotificationOrFail();

        cursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Reviews table during reviews for movie delete", 0, cursor.getCount());

        mContext.getContentResolver().unregisterContentObserver(testContentObserver);
        testContentObserver.closeHandlerThread();
        cursor.close();
    }

    public void testDeleteMovies() {
        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesEntry.CONTENT_URI, true, testContentObserver);
        ContentValues testValues = TestUtilities.createMovieValues();
        long movieRowId;

        // Test with MOVIES URI
        TestUtilities.deleteAllRecordsFromProvider(mContext);
        movieRowId = TestUtilities.insertMoviesIntoProvider(mContext, testValues);

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
        movieRowId = TestUtilities.insertMoviesIntoProvider(mContext, testValues);

        mContext.getContentResolver().delete(
                MoviesEntry.buildMovieUri(movieRowId),
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
        movieRowId = TestUtilities.insertMoviesIntoProvider(mContext, testValues);

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

    public void testBulkInsertMovies() {
        ContentValues[] bulkInsertContentValues = TestUtilities.createBulkInsertMoviesValues();

        TestUtilities.deleteAllRecordsFromProvider(mContext);
        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesEntry.CONTENT_URI, true, testContentObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MoviesEntry.CONTENT_URI, bulkInsertContentValues);

        testContentObserver.waitForNotificationOrFail();

        assertEquals(insertCount, TestUtilities.BULK_INSERT_RECORDS_TO_INSERT);

        Cursor cursor = mContext.getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
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

    public void testBulkInsertReviews() {
        ContentValues[] bulkInsertContentValues = TestUtilities.createBulkInsertReviewsValues();

        TestUtilities.deleteAllRecordsFromProvider(mContext);
        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.ReviewsEntry.CONTENT_URI, true, testContentObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MoviesContract.ReviewsEntry.CONTENT_URI, bulkInsertContentValues);

        testContentObserver.waitForNotificationOrFail();

        assertEquals(insertCount, TestUtilities.BULK_INSERT_RECORDS_TO_INSERT);

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
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
