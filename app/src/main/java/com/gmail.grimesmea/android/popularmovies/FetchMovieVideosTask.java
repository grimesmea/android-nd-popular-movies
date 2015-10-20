package com.gmail.grimesmea.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.gmail.grimesmea.android.popularmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

public class FetchMovieVideosTask extends AsyncTask<Void, Void, Video[]> {

    private final String LOG_TAG = FetchMovieVideosTask.class.getSimpleName();
    private final Context context;
    private final int mdbId;

    private String[] movieVideossArray;

    public FetchMovieVideosTask(Context context, int mdbId) {
        this.context = context;
        this.mdbId = mdbId;
    }

    @Override
    protected Video[] doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String videosJsonStr = null;
        String apiKey = BuildConfig.API_KEY;

        try {
            final String MOVIEDB_BASE_URL =
                    "http://api.themoviedb.org/3/movie";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendPath(Integer.toString(mdbId))
                    .appendPath("videos")
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.d(LOG_TAG, url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            videosJsonStr = buffer.toString();
            Log.d(LOG_TAG, videosJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getVideosFromJson(videosJsonStr, mdbId);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private Video[] getVideosFromJson(String videosJsonStr, int mdbId) throws JSONException {
        final String MDB_RESULTS = "results";

        JSONObject videosJson = new JSONObject(videosJsonStr);
        JSONArray videosJsonArray = videosJson.getJSONArray(MDB_RESULTS);
        Video[] videosArray = new Video[videosJsonArray.length()];

        for (int i = 0; i < videosJsonArray.length(); i++) {
            JSONObject videoJson = videosJsonArray.getJSONObject(i);
            videosArray[i] = new Video(videoJson, mdbId);
        }

        Log.d(LOG_TAG, videosJsonArray.length() + " videos fetched");

        return videosArray;
    }

    private int bulkInsertVideosDataIntoMoviesContentProvider(Video[] videosArray) {
        int insertedRows = 0;

        if (videosArray.length > 0) {
            List<ContentValues> videosContentValuesVector = new Vector<ContentValues>(videosArray.length);
            ContentValues[] videosContentValuesArray = new ContentValues[videosArray.length];

            for (Video video : videosArray) {
                videosContentValuesVector.add(video.createContentValues());
            }
            videosContentValuesVector.toArray(videosContentValuesArray);

            insertedRows = context.getContentResolver().
                    bulkInsert(MoviesContract.VideosEntry.CONTENT_URI, videosContentValuesArray);
        }

        Log.d(LOG_TAG, insertedRows + " rows inserted");

        return insertedRows;
    }

    @Override
    protected void onPostExecute(Video[] result) {
        if (result != null) {
            bulkInsertVideosDataIntoMoviesContentProvider(result);
        }
    }
}
