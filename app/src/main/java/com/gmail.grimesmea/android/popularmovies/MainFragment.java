package com.gmail.grimesmea.android.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.gmail.grimesmea.android.popularmovies.data.MoviesContract;

/**
 * Encapsulates fetching the movie data and displaying it as a {@link ListView} layout.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIES_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_SYNOPSIS,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_POPULARITY,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER_PATH,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_BACKDROP_PATH,
            MoviesContract.MoviesEntry.COLUMN_FAVORITE
    };

    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_SYNOPSIS = 2;
    static final int COL_MOVIE_RELEASE_DATE = 3;
    static final int COL_MOVIE_POPULARITY = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_POSTER_PATH = 6;
    static final int COL_MOVIE_BACKDROP_PATH = 7;
    static final int COL_FAVORITE = 8;

    public MoviePosterAdapter movieAdapter;
    public final static String MOVIE_PARCELABLE_KEY = "com.gmail.grimesmea.anroid.popularmovies.movie_parcelable";

    private boolean isSortedByPopularity = true;
    private String sortByPopularityQueryParameter = MoviesContract.MoviesEntry.TABLE_NAME + "." +
            MoviesContract.MoviesEntry.COLUMN_MOVIE_POPULARITY + " DESC";
    private String sortByRatingQueryParameter = MoviesContract.MoviesEntry.TABLE_NAME + "." +
            MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING + " DESC";

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
//            updateMovies();
//            movieAdapter = new MoviePosterAdapter(
//                    getActivity(),
//                    moviesCursor = getActivity().getContentResolver().query(
//                            MoviesContract.MoviesEntry.CONTENT_URI,
//                            null,
//                            null,
//                            null,
//                            sortByPopularityQueryParameter
//                    ),
//                    MoviePosterAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
//        } else {
//            movieAdapter = new MoviePosterAdapter(getActivity(), new ArrayList<>(Arrays.asList((Movie[]) savedInstanceState.getParcelableArray("movies"))));
//        }

        if (savedInstanceState != null && savedInstanceState.containsKey("sortedByPopularity")) {
            isSortedByPopularity = savedInstanceState.getBoolean("sortedByPopularity");
        }

        updateMovies();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieAdapter = new MoviePosterAdapter(getActivity(), null, 0);
        Log.d("MainFragment: ", "movieAdapter populated");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setColumnWidth(gridView.getWidth() / 2);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = (Movie) movieAdapter.getItem(position);
                if (movie != null) {
                    Bundle movieBundle = new Bundle();
                    movieBundle.putParcelable("movieParcelable", movie);
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra("movie", movieBundle);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
        moviesTask.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (isSortedByPopularity == true) {
            menu.findItem(R.id.action_sort_by_popularity).setChecked(true);
        } else {
            menu.findItem(R.id.action_sort_by_rating).setChecked(true);
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_sort_by_popularity:
//                if (!item.isChecked()) {
//                    item.setChecked(true);
//                    isSortedByPopularity = true;
//                    movieAdapter.sort(MoviePosterAdapter.createPopularityComparator());
//                }
//                return true;
//            case R.id.action_sort_by_rating:
//                if (!item.isChecked()) {
//                    item.setChecked(true);
//                    isSortedByPopularity = false;
//                    movieAdapter.sort(MoviePosterAdapter.createRatingComparator());
//                }
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("sortedByPopularity", isSortedByPopularity);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader cursorLoader;

        if (isSortedByPopularity) {
            cursorLoader = new CursorLoader(
                    getActivity(),
                    MoviesContract.MoviesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    sortByPopularityQueryParameter
            );
        } else {
            cursorLoader = new CursorLoader(
                    getActivity(),
                    MoviesContract.MoviesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    sortByRatingQueryParameter
            );
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        movieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
}
