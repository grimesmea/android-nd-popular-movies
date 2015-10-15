package com.gmail.grimesmea.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.grimesmea.android.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

import java.text.ParseException;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final int COL_REVIEW_ID = 0;
    static final int COL_MDB_ID = 1;
    static final int COL_REVIEW_AUTHOR = 2;
    static final int COL_REVIEW_CONTENT = 3;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int REVIEWS_LOADER = 200;
    public ReviewAdapter reviewAdapter;
    private Movie movie;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            movie = intent.getBundleExtra("movie").getParcelable("movieParcelable");
        }

        if (!hasReviewsForMovie()) {
            fetchReviews();
        }
    }

    private boolean hasReviewsForMovie() {
        return getActivity().getContentResolver().query(
                MoviesContract.ReviewsEntry.buildReviewsForMovieUri(movie.mdbId),
                null,
                null,
                null,
                null
        ).moveToFirst();
    }

    private void fetchReviews() {
        FetchMovieReviewsTask moviesReviewTask = new FetchMovieReviewsTask(getActivity(), movie.mdbId);
        moviesReviewTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ListView reviewsListView = (ListView) rootView.findViewById(R.id.review_listview);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View headerView = layoutInflater.inflate(R.layout.list_header_detail, reviewsListView, false);
        reviewAdapter = new ReviewAdapter(getActivity(), null, 0);

        getActivity().setTitle("");
        reviewsListView.addHeaderView(headerView);
        reviewsListView.setAdapter(reviewAdapter);

        ImageView backdropView = (ImageView) rootView.findViewById(R.id.detail_fragment_backdrop_imageview);
        Picasso.with(getActivity()).load(movie.getImageUrl(movie.backdropImagePath)).into(backdropView);

        ImageView posterView = (ImageView) rootView.findViewById(R.id.detail_fragment_poster_imageview);
        Picasso.with(getActivity()).load(movie.getImageUrl(movie.posterImagePath)).into(posterView);

        TextView movieTitleView = (TextView) rootView.findViewById(R.id.detail_fragment_movie_title_textview);
        movieTitleView.setText(movie.title);

        TextView movieReleaseDateView = (TextView) rootView.findViewById(R.id.detail_fragment_movie_release_date_textview);
        try {
            movieReleaseDateView.setText(movie.getFormattedReleaseDate());
        } catch (ParseException e) {
            movieReleaseDateView.setText(movie.releaseDate);
            e.printStackTrace();
        }

        TextView movieRatingView = (TextView) rootView.findViewById(R.id.detail_fragment_movie_rating_textview);
        movieRatingView.setText(movie.rating);

        TextView movieSynopsisView = (TextView) rootView.findViewById(R.id.detail_fragment_movie_synopsis_textview);
        movieSynopsisView.setText(movie.synopsis);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader cursorLoader = new CursorLoader(
                getActivity(),
                MoviesContract.ReviewsEntry.buildReviewsForMovieUri(movie.mdbId),
                null,
                null,
                null,
                null
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        reviewAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        reviewAdapter.swapCursor(null);
    }
}

