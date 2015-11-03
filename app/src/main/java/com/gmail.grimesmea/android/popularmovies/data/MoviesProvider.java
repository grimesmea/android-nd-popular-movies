package com.gmail.grimesmea.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MoviesProvider extends ContentProvider {

    static final int MOVIES = 100;
    static final int MOVIE = 101;
    static final int FAVORITES = 200;
    static final int POPULAR = 201;
    static final int HIGHLY_RATED = 202;
    static final int REVIEWS = 300;
    static final int REVIEW = 301;
    static final int REVIEWS_FOR_MOVIE = 302;
    static final int VIDEOS = 400;
    static final int VIDEO = 401;
    static final int VIDEOS_FOR_MOVIE = 402;
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private static final String movieSelection =
            MoviesContract.MoviesEntry.TABLE_NAME + "." +
                    MoviesContract.MoviesEntry._ID + " = ?";
    private static final String favoritesSelection =
            MoviesContract.MoviesEntry.TABLE_NAME + "." +
                    MoviesContract.MoviesEntry.COLUMN_FAVORITE + " = 1";
    private static final String popularSelection =
            MoviesContract.MoviesEntry.TABLE_NAME + "." +
                    MoviesContract.MoviesEntry.COLUMN_RETURNED_BY_POPULARITY_QUERY + " = 1";
    private static final String highlyRatedSelection =
            MoviesContract.MoviesEntry.TABLE_NAME + "." +
                    MoviesContract.MoviesEntry.COLUMN_RETURNED_BY_RATING_QUERY + " = 1";
    private static final String reviewSelection =
            MoviesContract.ReviewsEntry.TABLE_NAME + "." +
                    MoviesContract.ReviewsEntry._ID + " = ?";
    private static final String reviewsForMovieSelection =
            MoviesContract.ReviewsEntry.TABLE_NAME + "." +
                    MoviesContract.ReviewsEntry.COLUMN_MDB_ID + " = ?";
    private static final String videoSelection =
            MoviesContract.VideosEntry.TABLE_NAME + "." +
                    MoviesContract.VideosEntry._ID + " = ?";
    private static final String videosForMovieSelection =
            MoviesContract.VideosEntry.TABLE_NAME + "." +
                    MoviesContract.VideosEntry.COLUMN_MDB_ID + " = ?";
    private MoviesDbHelper moviesDbHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIE);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/favoriteMovies", FAVORITES);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/popularMovies", POPULAR);
        uriMatcher.addURI(authority, MoviesContract.PATH_MOVIES + "/highlyRatedMovies", HIGHLY_RATED);
        uriMatcher.addURI(authority, MoviesContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(authority, MoviesContract.PATH_REVIEWS + "/#", REVIEW);
        uriMatcher.addURI(authority, MoviesContract.PATH_REVIEWS + "/reviewsForMovie", REVIEWS_FOR_MOVIE);
        uriMatcher.addURI(authority, MoviesContract.PATH_VIDEOS, VIDEOS);
        uriMatcher.addURI(authority, MoviesContract.PATH_VIDEOS + "/#", VIDEO);
        uriMatcher.addURI(authority, MoviesContract.PATH_VIDEOS + "/videosForMovie", VIDEOS_FOR_MOVIE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case MOVIES: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        movieSelection,
                        new String[]{uri.getPathSegments().get(uri.getPathSegments().size() - 1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVORITES: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        favoritesSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case POPULAR: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        popularSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case HIGHLY_RATED: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        highlyRatedSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEWS: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEW: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        reviewSelection,
                        new String[]{uri.getPathSegments().get(uri.getPathSegments().size() - 1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEWS_FOR_MOVIE: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        reviewsForMovieSelection,
                        new String[]{uri.getQueryParameter(MoviesContract.ReviewsEntry.COLUMN_MDB_ID)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VIDEOS: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.VideosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VIDEO: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.VideosEntry.TABLE_NAME,
                        projection,
                        videoSelection,
                        new String[]{uri.getPathSegments().get(uri.getPathSegments().size() - 1)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VIDEOS_FOR_MOVIE: {
                cursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.VideosEntry.TABLE_NAME,
                        projection,
                        videosForMovieSelection,
                        new String[]{uri.getQueryParameter(MoviesContract.VideosEntry.COLUMN_MDB_ID)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case POPULAR:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case HIGHLY_RATED:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case REVIEWS:
                return MoviesContract.ReviewsEntry.CONTENT_TYPE;
            case REVIEW:
                return MoviesContract.ReviewsEntry.CONTENT_ITEM_TYPE;
            case REVIEWS_FOR_MOVIE:
                return MoviesContract.ReviewsEntry.CONTENT_TYPE;
            case VIDEOS:
                return MoviesContract.VideosEntry.CONTENT_TYPE;
            case VIDEO:
                return MoviesContract.VideosEntry.CONTENT_ITEM_TYPE;
            case VIDEOS_FOR_MOVIE:
                return MoviesContract.VideosEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.MoviesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(MoviesContract.ReviewsEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.ReviewsEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEOS: {
                long _id = db.insert(MoviesContract.VideosEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.VideosEntry.buildVideoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) {
            selection = "1";
        }

        switch (match) {
            case MOVIES: {
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case MOVIE: {
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        movieSelection,
                        new String[]{uri.getPathSegments().get(uri.getPathSegments().size() - 1)}
                );
                break;
            }
            case FAVORITES: {
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        favoritesSelection,
                        selectionArgs
                );
                break;
            }
            case POPULAR: {
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        popularSelection,
                        selectionArgs
                );
                break;
            }
            case HIGHLY_RATED: {
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        highlyRatedSelection,
                        selectionArgs
                );
                break;
            }
            case REVIEWS: {
                rowsDeleted = db.delete(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case REVIEW: {
                rowsDeleted = db.delete(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        reviewSelection,
                        new String[]{uri.getPathSegments().get(uri.getPathSegments().size() - 1)}
                );
                break;
            }
            case REVIEWS_FOR_MOVIE: {
                rowsDeleted = db.delete(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        reviewsForMovieSelection,
                        new String[]{uri.getQueryParameter(MoviesContract.ReviewsEntry.COLUMN_MDB_ID)}
                );
                break;
            }
            case VIDEOS: {
                rowsDeleted = db.delete(
                        MoviesContract.VideosEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case VIDEO: {
                rowsDeleted = db.delete(
                        MoviesContract.VideosEntry.TABLE_NAME,
                        videoSelection,
                        new String[]{uri.getPathSegments().get(uri.getPathSegments().size() - 1)}
                );
                break;
            }
            case VIDEOS_FOR_MOVIE: {
                rowsDeleted = db.delete(
                        MoviesContract.VideosEntry.TABLE_NAME,
                        videosForMovieSelection,
                        new String[]{uri.getQueryParameter(MoviesContract.ReviewsEntry.COLUMN_MDB_ID)}
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES: {
                rowsUpdated = db.update(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case MOVIE: {
                rowsUpdated = db.update(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        values,
                        movieSelection,
                        new String[]{uri.getPathSegments().get(uri.getPathSegments().size() - 1)}
                );
                break;
            }
            case FAVORITES: {
                rowsUpdated = db.update(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        values,
                        favoritesSelection,
                        selectionArgs
                );
                break;
            }
            case POPULAR: {
                rowsUpdated = db.update(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        values,
                        popularSelection,
                        selectionArgs
                );
                break;
            }
            case HIGHLY_RATED: {
                rowsUpdated = db.update(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        values,
                        highlyRatedSelection,
                        selectionArgs
                );
                break;
            }
            case REVIEWS: {
                rowsUpdated = db.update(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case REVIEW: {
                rowsUpdated = db.update(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        values,
                        reviewSelection,
                        new String[]{uri.getPathSegments().get(uri.getPathSegments().size() - 1)}
                );
                break;
            }
            case REVIEWS_FOR_MOVIE: {
                rowsUpdated = db.update(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        values,
                        reviewsForMovieSelection,
                        new String[]{uri.getQueryParameter(MoviesContract.ReviewsEntry.COLUMN_MDB_ID)}
                );
                break;
            }
            case VIDEOS: {
                rowsUpdated = db.update(
                        MoviesContract.VideosEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case VIDEO: {
                rowsUpdated = db.update(
                        MoviesContract.VideosEntry.TABLE_NAME,
                        values,
                        videoSelection,
                        new String[]{uri.getPathSegments().get(uri.getPathSegments().size() - 1)}
                );
                break;
            }
            case VIDEOS_FOR_MOVIE: {
                rowsUpdated = db.update(
                        MoviesContract.VideosEntry.TABLE_NAME,
                        values,
                        videosForMovieSelection,
                        new String[]{uri.getQueryParameter(MoviesContract.VideosEntry.COLUMN_MDB_ID)}
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int returnCount;

        switch (match) {
            case MOVIES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEWS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.ReviewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case VIDEOS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.VideosEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
