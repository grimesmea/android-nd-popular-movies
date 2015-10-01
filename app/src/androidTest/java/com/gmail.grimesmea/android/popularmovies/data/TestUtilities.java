package com.gmail.grimesmea.android.popularmovies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.gmail.grimesmea.android.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase {

    static final String TEST_MOVIE_TITLE = "Test Movie";
    static final String TEST_MOVIE_SYNOPSIS = "This is a test synopsis";
    static final String TEST_MOVIE_RELEASE_DATE = "2015-15-15";
    static final float TEST_MOVIE_POPULARITY = 8.1f;
    static final float TEST_MOVIE_RATING = 2.3f;
    static final String TEST_MOVIE_POSTER_PATH = "/test/posterpath";
    static final String TEST_MOVIE_BACKDROP_PATH = "/test/backdroppath";
    static final int TEST_FAVORITE = 1;

    static final int BULK_INSERT_RECORDS_TO_INSERT = 15;


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

    static ContentValues createMovieValues(int i) {
        ContentValues movieValues = new ContentValues();

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

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHandlerTread;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHandlerTread = ht;
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

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}