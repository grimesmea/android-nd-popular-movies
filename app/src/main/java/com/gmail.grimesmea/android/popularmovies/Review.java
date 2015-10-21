package com.gmail.grimesmea.android.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.grimesmea.android.popularmovies.data.MoviesContract;

import org.json.JSONException;
import org.json.JSONObject;

public class Review {

    final static String MDB_ID = "id";
    final static String MDB_AUTHOR = "author";
    final static String MDB_COTNENT = "content";

    int mdbId;
    String author;
    String content;

    public Review(JSONObject reviewJson, int mdbId) throws JSONException {
        this(
                mdbId,
                reviewJson.getString(MDB_AUTHOR),
                reviewJson.getString(MDB_COTNENT)
        );
    }

    public Review(Cursor reviewCursor) {
        this(
                reviewCursor.getInt(DetailFragment.COL_MDB_ID),
                reviewCursor.getString(DetailFragment.COL_REVIEW_AUTHOR),
                reviewCursor.getString(DetailFragment.COL_REVIEW_CONTENT)
        );
    }

    public Review(int mdbId, String author, String content) {
        this.mdbId = mdbId;
        this.author = author;
        this.content = content;
    }

    public ContentValues createContentValues() {
        ContentValues reviewValues = new ContentValues();

        reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_MDB_ID, mdbId);
        reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR, author);
        reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_CONTENT, content);

        return reviewValues;
    }


    public int getMdbId() {
        return mdbId;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
