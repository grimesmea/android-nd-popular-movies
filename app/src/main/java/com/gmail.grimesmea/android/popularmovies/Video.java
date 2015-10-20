package com.gmail.grimesmea.android.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.grimesmea.android.popularmovies.data.MoviesContract;

import org.json.JSONException;
import org.json.JSONObject;

public class Video {
    final static String MDB_ID = "id";
    final static String MDB_TYPE = "type";
    final static String MDB_NAME = "name";
    final static String MDB_SIZE = "size";
    final static String MDB_SITE = "site";
    final static String MDB_KEY = "key";

    int mdbId;
    String type;
    String name;
    int size;
    String site;
    String key;

    public Video(JSONObject videoJson, int mdbId) throws JSONException {
        this(
                mdbId,
                videoJson.getString(MDB_TYPE),
                videoJson.getString(MDB_NAME),
                videoJson.getInt(MDB_SIZE),
                videoJson.getString(MDB_SITE),
                videoJson.getString(MDB_KEY)
        );
    }

    public Video(Cursor videoCursor) {
        this(
                videoCursor.getInt(DetailFragment.COL_MDB_ID),
                videoCursor.getString(DetailFragment.COL_VIDEO_TYPE),
                videoCursor.getString(DetailFragment.COL_VIDEO_NAME),
                videoCursor.getInt(DetailFragment.COL_VIDEO_SIZE),
                videoCursor.getString(DetailFragment.COL_VIDEO_SITE),
                videoCursor.getString(DetailFragment.COL_VIDEO_KEY)

        );
    }

    public Video(int mdbId, String type, String name, int size, String site, String key) {
        this.mdbId = mdbId;
        this.type = type;
        this.name = name;
        this.size = size;
        this.site = site;
        this.key = key;
    }

    public ContentValues createContentValues() {
        ContentValues videoValues = new ContentValues();

        videoValues.put(MoviesContract.VideosEntry.COLUMN_MDB_ID, mdbId);
        videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_TYPE, type);
        videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_NAME, name);
        videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_SIZE, size);
        videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_SITE, site);
        videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_KEY, key);

        return videoValues;
    }
}
