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

public class FetchMovieReviewsTask extends AsyncTask<Void, Void, Review[]> {

    private final String LOG_TAG = FetchMovieReviewsTask.class.getSimpleName();
    private final Context context;
    private final int mdbId;

    public FetchMovieReviewsTask(Context context, int mdbId) {
        this.context = context;
        this.mdbId = mdbId;
    }

    @Override
    protected Review[] doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewsJsonStr = null;
        String apiKey = BuildConfig.API_KEY;


        try {
            final String MOVIEDB_BASE_URL =
                    "http://api.themoviedb.org/3/movie";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendPath(Integer.toString(mdbId))
                    .appendPath("reviews")
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();

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

            reviewsJsonStr = buffer.toString();
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
            return getReviewsFromJson(reviewsJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private Review[] getReviewsFromJson(String reviewsJsonStr) throws JSONException {
        final String MDB_RESULTS = "results";

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsJsonArray = reviewsJson.getJSONArray(MDB_RESULTS);
        Review[] reviewsArray = new Review[reviewsJsonArray.length()];

        for (int i = 0; i < reviewsJsonArray.length(); i++) {
            JSONObject reviewJson = reviewsJsonArray.getJSONObject(i);
            reviewsArray[i] = new Review(reviewJson, mdbId);
        }

        return reviewsArray;
    }

    private int bulkInsertReviewsDataIntoMoviesContentProvider(Review[] reviewsArray) {
        int insertedRows = 0;

        if (reviewsArray.length > 0) {
            List<ContentValues> reviewsContentValuesVector = new Vector<ContentValues>(reviewsArray.length);
            ContentValues[] reviewsContentValuesArray = new ContentValues[reviewsArray.length];

            for (Review review : reviewsArray) {
                reviewsContentValuesVector.add(review.createContentValues());
            }
            reviewsContentValuesVector.toArray(reviewsContentValuesArray);

            insertedRows = context.getContentResolver().
                    bulkInsert(MoviesContract.ReviewsEntry.CONTENT_URI, reviewsContentValuesArray);
        }

        return insertedRows;
    }

    @Override
    protected void onPostExecute(Review[] result) {
        if (result != null) {
            bulkInsertReviewsDataIntoMoviesContentProvider(result);
        }
    }
}
