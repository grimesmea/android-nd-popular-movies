package com.gmail.grimesmea.android.popularmovies;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.gmail.grimesmea.android.popularmovies.data.MoviesContract;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static String MOVIE_PARCELABLE_KEY = "com.gmail.grimesmea.anroid.popularmovies.movie_parcelable";
    static final int COL_MOVIE_ID = 0;
    static final int COL_MDB_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_SYNOPSIS = 3;
    static final int COL_MOVIE_RELEASE_DATE = 4;
    static final int COL_MOVIE_POPULARITY = 5;
    static final int COL_MOVIE_RATING = 6;
    static final int COL_MOVIE_POSTER_PATH = 7;
    static final int COL_MOVIE_BACKDROP_PATH = 8;
    static final int COL_FAVORITE = 9;

    private static final int MOVIES_LOADER = 100;
    public MoviePosterAdapter movieAdapter;
    String sortByPopularityQueryParameter = MoviesContract.MoviesEntry.TABLE_NAME + "." +
            MoviesContract.MoviesEntry.COLUMN_MOVIE_POPULARITY + " DESC";
    private SortByCriteria sortByCriteria = SortByCriteria.POPULARITY;
    private boolean hasFetchedByRating = false;
    private String sortParam = "popularity.desc";
    private String sortByRatingQueryParameter = MoviesContract.MoviesEntry.TABLE_NAME + "." +
            MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING + " DESC";

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey("sortedByPopularity")) {
            sortByCriteria = (SortByCriteria) savedInstanceState.getSerializable("sortByCriteria");
        }

        if (!getActivity().getContentResolver().query(
                MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        ).moveToNext()) {
            fetchMovies();
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieAdapter = new MoviePosterAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Movie movie = new Movie(cursor);
                    ((Callback) getActivity())
                            .onItemSelected(movie);
                }
            }
        });

        return rootView;
    }

    private void fetchMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity(), sortParam);
        moviesTask.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (sortByCriteria == SortByCriteria.POPULARITY) {
            menu.findItem(R.id.action_sort_by_popularity).setChecked(true);
            sortParam = "popularity.desc";
        } else if (sortByCriteria == SortByCriteria.RATING) {
            menu.findItem(R.id.action_sort_by_rating).setChecked(true);
            sortParam = "vote_average.desc";
        } else if (sortByCriteria == SortByCriteria.FAVORITES) {
            menu.findItem(R.id.action_sort_by_favorites).setChecked(true);
        } else {
            try {
                throw new Exception("Unknown sort criteria found when preparing options menu: " +
                        sortByCriteria);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_popularity:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    sortByCriteria = SortByCriteria.POPULARITY;
                    sortParam = "popularity.desc";
                    getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
                }
                return true;
            case R.id.action_sort_by_rating:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    sortByCriteria = SortByCriteria.RATING;
                    sortParam = "vote_average.desc";
                    if (!hasFetchedByRating) {
                        fetchMovies();
                        hasFetchedByRating = true;
                    }
                    getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
                }
                return true;
            case R.id.action_sort_by_favorites:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    sortByCriteria = SortByCriteria.FAVORITES;
                    getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
        outState.putSerializable("sortedByCriteria", sortByCriteria);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader cursorLoader = null;

        if (sortByCriteria == SortByCriteria.POPULARITY) {
            cursorLoader = new CursorLoader(
                    getActivity(),
                    MoviesContract.MoviesEntry.buildMoviesReturnedByPopularityQuery(),
                    null,
                    null,
                    null,
                    sortByPopularityQueryParameter
            );
        } else if (sortByCriteria == SortByCriteria.RATING) {
            cursorLoader = new CursorLoader(
                    getActivity(),
                    MoviesContract.MoviesEntry.buildMoviesReturnedByRatingQuery(),
                    null,
                    null,
                    null,
                    sortByRatingQueryParameter
            );
        } else if (sortByCriteria == SortByCriteria.FAVORITES) {
            cursorLoader = new CursorLoader(
                    getActivity(),
                    MoviesContract.MoviesEntry.buildFavoriteMoviesUri(),
                    null,
                    null,
                    null,
                    null
            );
        } else {
            try {
                throw new Exception("Unknown sort criteria found when creating cursor loader: " +
                        sortByCriteria);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    private enum SortByCriteria {
        POPULARITY,
        RATING,
        FAVORITES
    }

    public interface Callback {
        void onItemSelected(Movie movie);
    }
}
