package com.gmail.grimesmea.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.gmail.grimesmea.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_VIDEOS = "videos";

    public static final class MoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MDB_ID = "mdb_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_SYNOPSIS = "movie_synopsis";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_MOVIE_POPULARITY = "movie_popularity";
        public static final String COLUMN_MOVIE_RATING = "movie_rating";
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "movie_backdrop_path";
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_RETURNED_BY_POPULARITY_QUERY = "returnedByPopularityQuery";
        public static final String COLUMN_RETURNED_BY_RATING_QUERY = "returnedByRatingQuery";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFavoriteMoviesUri() {
            return CONTENT_URI.buildUpon().appendPath("favoriteMovies").build();
        }

        public static Uri buildMoviesReturnedByPopularityQuery() {
            return CONTENT_URI.buildUpon().appendPath("popularMovies").build();
        }

        public static Uri buildMoviesReturnedByRatingQuery() {
            return CONTENT_URI.buildUpon().appendPath("highlyRatedMovies").build();
        }
    }

    public static final class ReviewsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_MDB_ID = "mdb_id";
        public static final String COLUMN_REVIEW_AUTHOR = "review_author";
        public static final String COLUMN_REVIEW_CONTENT = "review_content";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewsForMovieUri(int mdbId) {
            return CONTENT_URI.buildUpon().appendPath("reviewsForMovie").appendQueryParameter(COLUMN_MDB_ID, Integer.toString(mdbId)).build();
        }
    }

    public static final class VideosEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;

        public static final String TABLE_NAME = "videos";
        public static final String COLUMN_MDB_ID = "mdb_id";
        public static final String COLUMN_VIDEO_TYPE = "video_type";
        public static final String COLUMN_VIDEO_NAME = "video_name";
        public static final String COLUMN_VIDEO_SIZE = "video_size";
        public static final String COLUMN_VIDEO_SITE = "video_site";
        public static final String COLUMN_VIDEO_KEY = "video_key";

        public static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildVideosForMovieUri(int mdbId) {
            return CONTENT_URI.buildUpon().appendPath("videosForMovie").appendQueryParameter(COLUMN_MDB_ID, Integer.toString(mdbId)).build();
        }
    }

}
