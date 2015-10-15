package com.gmail.grimesmea.android.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {

    private static final Uri TEST_MOVIES_DIR = MoviesContract.MoviesEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_DIR = MoviesContract.MoviesEntry.buildMovieUri(1);
    private static final Uri TEST_FAVORITES_DIR = MoviesContract.MoviesEntry.buildFavoriteMoviesUri();
    private static final Uri TEST_REVIEWS_DIR = MoviesContract.ReviewsEntry.CONTENT_URI;
    private static final Uri TEST_REVIEW_DIR = MoviesContract.ReviewsEntry.buildReviewUri(1);
    private static final Uri TEST_REVIEWS_FOR_MOVIE_DIR = MoviesContract.ReviewsEntry.buildReviewsForMovieUri(1);


    public void testUriMatcher() {
        UriMatcher testUriMatcher = MoviesProvider.buildUriMatcher();

        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                MoviesProvider.MOVIES, testUriMatcher.match(TEST_MOVIES_DIR));
        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                MoviesProvider.MOVIE, testUriMatcher.match(TEST_MOVIE_DIR));
        assertEquals("Error: The FAVORITES URI was matched incorrectly.",
                MoviesProvider.FAVORITES, testUriMatcher.match(TEST_FAVORITES_DIR));

        assertEquals("Error: The REVIEWS URI was matched incorrectly.",
                MoviesProvider.REVIEWS, testUriMatcher.match(TEST_REVIEWS_DIR));
        assertEquals("Error: The REVIEWS URI was matched incorrectly.",
                MoviesProvider.REVIEW, testUriMatcher.match(TEST_REVIEW_DIR));
        assertEquals("Error: The REVIEWS_FOR_MOVIE_DIR URI was matched incorrectly.",
                MoviesProvider.REVIEWS_FOR_MOVIE, testUriMatcher.match(TEST_REVIEWS_FOR_MOVIE_DIR));
    }
}
