package com.gmail.grimesmea.android.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {

    private static final Uri TEST_MOVIES_DIR = MoviesContract.MoviesEntry.CONTENT_URI;
    private static final Uri TEST_FAVORITES_DIR = MoviesContract.MoviesEntry.buildFavoriteMoviesUri();

    public void testUriMatcher() {
        UriMatcher testUriMatcher = MoviesProvider.buildUriMatcher();

        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                MoviesProvider.MOVIES, testUriMatcher.match(TEST_MOVIES_DIR));
        assertEquals("Error: The FAVORITES URI was matched incorrectly.",
                MoviesProvider.FAVORITES, testUriMatcher.match(TEST_FAVORITES_DIR));
    }
}
