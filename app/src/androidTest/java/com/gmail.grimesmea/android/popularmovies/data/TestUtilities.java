package com.gmail.grimesmea.android.popularmovies.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.gmail.grimesmea.android.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase {

    static final int TEST_MDB_ID = 123;
    static final String TEST_MOVIE_TITLE = "Test Movie";
    static final String TEST_MOVIE_SYNOPSIS = "This is a test synopsis";
    static final String TEST_MOVIE_RELEASE_DATE = "2015-15-15";
    static final float TEST_MOVIE_POPULARITY = 8.1f;
    static final float TEST_MOVIE_RATING = 2.3f;
    static final String TEST_MOVIE_POSTER_PATH = "/test/posterpath";
    static final String TEST_MOVIE_BACKDROP_PATH = "/test/backdroppath";
    static final int TEST_FAVORITE = 1;

    static final String TEST_REVIEW_AUTHOR = "Test Review Author";
    static final String TEST_REVIEW_CONTENT = "Test Review Content";


    static final int BULK_INSERT_RECORDS_TO_INSERT = 15;

    static void deleteDatabase(Context context) {
        context.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }

    static void deleteReviewsRecordsFromProvider(Context context) {
        context.getContentResolver().delete(
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null
        );
    }

    static void deleteVideosRecordsFromProvider(Context context) {
        context.getContentResolver().delete(
                MoviesContract.VideosEntry.CONTENT_URI,
                null,
                null
        );
    }

    static void deleteMoviesRecordsFromProvider(Context context) {
        context.getContentResolver().delete(
                MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null
        );
    }

    static void deleteAllRecordsFromProvider(Context context) {
        deleteReviewsRecordsFromProvider(context);
        deleteVideosRecordsFromProvider(context);
        deleteMoviesRecordsFromProvider(context);
    }

    static ContentValues createMovieValues(int i) {
        ContentValues movieValues = new ContentValues();

        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MDB_ID, TEST_MDB_ID + i);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE, TEST_MOVIE_TITLE + i);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_SYNOPSIS, TEST_MOVIE_SYNOPSIS + i);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE, TEST_MOVIE_RELEASE_DATE);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POPULARITY, TEST_MOVIE_POPULARITY);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING, TEST_MOVIE_RATING);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER_PATH, TEST_MOVIE_POSTER_PATH + i);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_BACKDROP_PATH, TEST_MOVIE_BACKDROP_PATH + i);
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, TEST_FAVORITE);

        return movieValues;
    }

    static ContentValues createMovieValues() {
        return createMovieValues(0);
    }

    static ContentValues[] createBulkInsertMoviesValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues movieValues = createMovieValues(i);
            returnContentValues[i] = movieValues;
        }

        return returnContentValues;
    }

    static ContentValues createReviewValues(int i) {
        ContentValues reviewValues = new ContentValues();

        reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_MDB_ID, TEST_MDB_ID + i);
        reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR, TEST_REVIEW_AUTHOR + i);
        reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_CONTENT, TEST_REVIEW_CONTENT + i);

        return reviewValues;
    }

    static ContentValues createReviewValues() {
        return createReviewValues(0);
    }

    static ContentValues[] createBulkInsertReviewsValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues reviewValues = createReviewValues(i);
            returnContentValues[i] = reviewValues;
        }

        return returnContentValues;
    }

    static ContentValues createVideoValues(int i) {
        ContentValues videoValues = new ContentValues();

        videoValues.put(MoviesContract.VideosEntry.COLUMN_MDB_ID, TEST_MDB_ID + i);
        videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_TYPE, TEST_REVIEW_AUTHOR + i);
        videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_NAME, TEST_REVIEW_CONTENT + i);
        videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_SIZE, TEST_REVIEW_CONTENT + i);
        videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_SITE, TEST_REVIEW_CONTENT + i);
        videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_KEY, TEST_REVIEW_CONTENT + i);


        return videoValues;
    }

    static ContentValues createVideoValues() {
        return createVideoValues(0);
    }

    static ContentValues[] createBulkInsertVideosValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues videoValues = createVideoValues(i);
            returnContentValues[i] = videoValues;
        }

        return returnContentValues;
    }

    static long insertMovieValuesIntoDb(SQLiteDatabase db, ContentValues contentValues) {
        return db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, contentValues);
    }

    static long insertReviewValuesIntoDb(SQLiteDatabase db, ContentValues contentValues) {
        return db.insert(MoviesContract.ReviewsEntry.TABLE_NAME, null, contentValues);
    }

    static long insertVideoValuesIntoDb(SQLiteDatabase db, ContentValues contentValues) {
        return db.insert(MoviesContract.VideosEntry.TABLE_NAME, null, contentValues);
    }

    static long insertMoviesIntoProvider(Context context, ContentValues contentValues) {
        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        context.getContentResolver().registerContentObserver(MoviesContract.MoviesEntry.CONTENT_URI, true, testContentObserver);

        Uri movieUri = context.getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);

        testContentObserver.waitForNotificationOrFail();

        context.getContentResolver().unregisterContentObserver(testContentObserver);
        testContentObserver.closeHandlerThread();

        return ContentUris.parseId(movieUri);
    }

    static long insertReviewsIntoProvider(Context context, ContentValues contentValues) {
        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        context.getContentResolver().registerContentObserver(MoviesContract.ReviewsEntry.CONTENT_URI, true, testContentObserver);

        Uri reviewUri = context.getContentResolver().insert(MoviesContract.ReviewsEntry.CONTENT_URI, contentValues);

        testContentObserver.waitForNotificationOrFail();

        context.getContentResolver().unregisterContentObserver(testContentObserver);
        testContentObserver.closeHandlerThread();

        return ContentUris.parseId(reviewUri);
    }

    static long insertVideosIntoProvider(Context context, ContentValues contentValues) {
        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        context.getContentResolver().registerContentObserver(MoviesContract.VideosEntry.CONTENT_URI, true, testContentObserver);

        Uri reviewUri = context.getContentResolver().insert(MoviesContract.VideosEntry.CONTENT_URI, contentValues);

        testContentObserver.waitForNotificationOrFail();

        context.getContentResolver().unregisterContentObserver(testContentObserver);
        testContentObserver.closeHandlerThread();

        return ContentUris.parseId(reviewUri);
    }


    static void validateCursor(String cursorSource, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Error: Empty cursor returned by " + cursorSource, valueCursor.moveToFirst());
        validateCurrentRecord(cursorSource, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String cursorSource, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Error: Column '" + columnName + "' not found in " + cursorSource + " records", idx == -1);

            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHandlerTread;
        boolean mContentChanged;

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHandlerTread = ht;
        }

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
        }

        public void closeHandlerThread() {
            mHandlerTread.quit();
        }
    }
}