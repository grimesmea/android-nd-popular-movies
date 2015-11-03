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

public class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final Context context;
    String sortParam;
    private Movie[] moviesArray;

    public FetchMoviesTask(Context context, String sortParam) {
        this.context = context;
        this.sortParam = sortParam;
    }

    @Override
    protected Movie[] doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;
        String apiKey = BuildConfig.API_KEY;
        String minVoteCount = "1000";

        try {
            final String MOVIEDB_BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";
            final String MIN_VOTE_COUNT = "vote_count.gte";

            Uri builtUri;

            if (sortParam == "vote_average.desc") {
                builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortParam)
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .appendQueryParameter(MIN_VOTE_COUNT, minVoteCount)
                        .build();
                Log.d(LOG_TAG, builtUri.toString());
            } else {
                builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortParam)
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();
            }

            URL url = new URL(builtUri.toString());

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

            moviesJsonStr = buffer.toString();
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
            return getMoviesFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private Movie[] getMoviesFromJson(String moviesJsonStr) throws JSONException {
        final String MDB_RESULTS = "results";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesJsonArray = moviesJson.getJSONArray(MDB_RESULTS);
        moviesArray = new Movie[moviesJsonArray.length()];

        for (int i = 0; i < moviesJsonArray.length(); i++) {
            JSONObject movieJson = moviesJsonArray.getJSONObject(i);
            moviesArray[i] = new Movie(movieJson);
        }

        Log.d(LOG_TAG, moviesJsonArray.length() + " movies fetched");

        return moviesArray;
    }

    private int bulkInsertMoviesDataIntoMoviesContentProvider(Movie[] moviesArray) {
        int insertedRows = 0;

        if (moviesArray.length > 0) {
            ContentValues movieQueryTypeValues = new ContentValues();
            List<ContentValues> moviesContentValuesVector = new Vector<ContentValues>(moviesArray.length);
            ContentValues[] moviesContentValuesArray = new ContentValues[moviesArray.length];

            if (sortParam == "popularity.desc") {
                movieQueryTypeValues.put(MoviesContract.MoviesEntry.COLUMN_RETURNED_BY_POPULARITY_QUERY, 1);
                movieQueryTypeValues.put(MoviesContract.MoviesEntry.COLUMN_RETURNED_BY_RATING_QUERY, 0);
            } else if (sortParam == "vote_average.desc") {
                movieQueryTypeValues.put(MoviesContract.MoviesEntry.COLUMN_RETURNED_BY_POPULARITY_QUERY, 0);
                movieQueryTypeValues.put(MoviesContract.MoviesEntry.COLUMN_RETURNED_BY_RATING_QUERY, 1);
            }

            for (Movie movie : moviesArray) {
                ContentValues movieContentValues = new ContentValues(movie.createContentValues());
                movieContentValues.putAll(movieQueryTypeValues);
                moviesContentValuesVector.add(movieContentValues);
            }
            moviesContentValuesVector.toArray(moviesContentValuesArray);

            insertedRows = context.getContentResolver().
                    bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, moviesContentValuesArray);
        }

        Log.d(LOG_TAG, insertedRows + " rows inserted");

        return insertedRows;
    }

    @Override
    protected void onPostExecute(Movie[] result) {
        if (result != null) {
            bulkInsertMoviesDataIntoMoviesContentProvider(result);
        }
    }
}
