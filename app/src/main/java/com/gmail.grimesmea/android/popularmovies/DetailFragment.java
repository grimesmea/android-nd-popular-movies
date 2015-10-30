package com.gmail.grimesmea.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.grimesmea.android.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

import java.text.ParseException;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final int COL_MDB_ID = 1;
    static final int COL_REVIEW_ID = 0;
    static final int COL_REVIEW_AUTHOR = 2;
    static final int COL_REVIEW_CONTENT = 3;
    static final int COL_VIDEO_ID = 0;
    static final int COL_VIDEO_TYPE = 2;
    static final int COL_VIDEO_NAME = 3;
    static final int COL_VIDEO_SIZE = 4;
    static final int COL_VIDEO_SITE = 5;
    static final int COL_VIDEO_KEY = 6;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int REVIEWS_LOADER = 200;
    private static final int VIDEOS_LOADER = 300;
    public ReviewAdapter reviewAdapter;
    public VideoAdapter videoAdapter;
    private Movie movie;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable("movieParcelable");
        }

        if (movie != null) {
            if (!hasReviewsForMovie()) {
                fetchReviews();
            }
            if (!hasVideosForMovie()) {
                fetchVideos();
            }
        }

        setHasOptionsMenu(true);
    }

    private boolean hasReviewsForMovie() {
        return getActivity().getContentResolver().query(
                MoviesContract.ReviewsEntry.buildReviewsForMovieUri(movie.getMdbId()),
                null,
                null,
                null,
                null
        ).moveToFirst();
    }

    private boolean hasVideosForMovie() {
        return getActivity().getContentResolver().query(
                MoviesContract.VideosEntry.buildVideosForMovieUri(movie.getMdbId()),
                null,
                null,
                null,
                null
        ).moveToFirst();
    }

    private void fetchReviews() {
        FetchMovieReviewsTask moviesReviewTask = new FetchMovieReviewsTask(getActivity(), movie.getMdbId());
        moviesReviewTask.execute();
    }

    private void fetchVideos() {
        FetchMovieVideosTask moviesVideosTask = new FetchMovieVideosTask(getActivity(), movie.getMdbId());
        moviesVideosTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ListView reviewsListView = (ListView) rootView.findViewById(R.id.review_listview);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View detailsHeaderView = layoutInflater.inflate(R.layout.list_header_movie_details, reviewsListView, false);
        View videosHeaderView = layoutInflater.inflate(R.layout.list_header_videos, reviewsListView, false);
        View reviewsLabelHeaderView = layoutInflater.inflate(R.layout.list_header_reviews_label, reviewsListView, false);
        reviewAdapter = new ReviewAdapter(getActivity(), null, 0);

        getActivity().setTitle("");
        reviewsListView.setHeaderDividersEnabled(false);
        reviewsListView.addHeaderView(detailsHeaderView);
        reviewsListView.addHeaderView(videosHeaderView);
        reviewsListView.addHeaderView(reviewsLabelHeaderView);
        reviewsListView.setAdapter(reviewAdapter);

        if (movie != null) {
            populateMovieDetailsInView(rootView);
        }

        return rootView;
    }

    private void populateMovieDetailsInView(View rootView) {
        ImageView backdropView = (ImageView) rootView.findViewById(R.id.detail_fragment_backdrop_imageview);
        Picasso.with(getActivity()).load(movie.getBackdropImageUrl()).into(backdropView);

        ImageView posterView = (ImageView) rootView.findViewById(R.id.detail_fragment_poster_imageview);
        Picasso.with(getActivity()).load(movie.getPosterImageUrl()).into(posterView);

        TextView movieTitleView = (TextView) rootView.findViewById(R.id.detail_fragment_movie_title_textview);
        movieTitleView.setText(movie.getTitle());

        TextView movieReleaseDateView = (TextView) rootView.findViewById(R.id.detail_fragment_movie_release_date_textview);
        try {
            movieReleaseDateView.setText(movie.getFormattedReleaseDate());
        } catch (ParseException e) {
            movieReleaseDateView.setText(movie.getReleaseDate());
            e.printStackTrace();
        }

        TextView movieRatingView = (TextView) rootView.findViewById(R.id.detail_fragment_movie_rating_textview);
        movieRatingView.setText(movie.getRating());

        TextView movieSynopsisView = (TextView) rootView.findViewById(R.id.detail_fragment_movie_synopsis_textview);
        movieSynopsisView.setText(movie.getSynopsis());
    }

    public int setFavoriteStatusOfMovieInProvider(Movie movie, Boolean isFavorite) {
        ContentValues favoritesValue = new ContentValues();
        favoritesValue.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, (byte) (isFavorite ? 1 : 0));
        return getActivity().getContentResolver().update(
                MoviesContract.MoviesEntry.CONTENT_URI,
                favoritesValue,
                MoviesContract.MoviesEntry.COLUMN_MDB_ID + "= ?",
                new String[]{Integer.toString(movie.getMdbId())}
        );
    }

    public void showFavoriteStatusChangeToast() {
        Context context = getActivity();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text = movie.getTitle();

        if (movie.getIsFavorite()) {
            text = text + " is now a favorite!";
        } else {
            text = text + " is no longer a favorite.";
        }

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_detail, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (movie != null && movie.getIsFavorite() == true) {
            MenuItem favoriteButton = menu.findItem(R.id.action_favorite_movie);

            favoriteButton.setChecked(true);
            favoriteButton.setIcon(R.drawable.ic_favorite_36dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite_movie:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    item.setIcon(R.drawable.ic_favorite_36dp);
                    movie.setIsFavorite(true);
                    setFavoriteStatusOfMovieInProvider(movie, true);
                } else {
                    item.setChecked(false);
                    item.setIcon(R.drawable.ic_favorite_outline_36dp);
                    movie.setIsFavorite(false);
                    setFavoriteStatusOfMovieInProvider(movie, false);
                }
                showFavoriteStatusChangeToast();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (movie != null) {
            getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
            getLoaderManager().initLoader(VIDEOS_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case REVIEWS_LOADER:
                return new CursorLoader(
                        getActivity(),
                        MoviesContract.ReviewsEntry.buildReviewsForMovieUri(movie.mdbId),
                        null,
                        null,
                        null,
                        null
                );

            case VIDEOS_LOADER:
                return new CursorLoader(
                        getActivity(),
                        MoviesContract.VideosEntry.buildVideosForMovieUri(movie.mdbId),
                        null,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case REVIEWS_LOADER:
                reviewAdapter.swapCursor(cursor);
                if (cursor.moveToFirst() && cursor.getCount() >= 1) {
                    removeNoReviewsAvailableWarning();
                }
                break;
            case VIDEOS_LOADER:
                addVideosTextView(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case REVIEWS_LOADER:
                reviewAdapter.swapCursor(null);
                break;
            case VIDEOS_LOADER:
                if (getView() != null) {
                    ViewGroup containerView = (ViewGroup) getView().findViewById(R.id.movie_videos_container);
                    containerView.removeAllViewsInLayout();
                }
                break;
        }
    }

    private void removeNoReviewsAvailableWarning() {
        ViewGroup containerView = (ViewGroup) getView().findViewById(R.id.movie_reviews_label_container);

        View noReviewsWarningView = containerView.findViewById(R.id.no_reviews_available_warning);
        containerView.removeViewInLayout(noReviewsWarningView);
    }

    private void addVideosTextView(final Cursor cursor) {
        ViewGroup containerView = (ViewGroup) getView().findViewById(R.id.movie_videos_container);

        if (cursor.moveToFirst() && cursor.getCount() >= 1) {
            removeNoVideosAvailableWarning();

            do {
                final Video video = new Video(cursor);
                View v = getLayoutInflater(null).inflate(R.layout.list_item_video, null);
                final View rootView = v.getRootView();
                TextView videoName = (TextView) v.findViewById(R.id.list_item_video_textview);
                videoName.setText(video.getName());
                videoName.setTag(video);

                containerView.addView(rootView);

                rootView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    "vnd.youtube:" + video.getKey()));
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    video.getVideoUrl()));
                            startActivity(intent);
                        }
                    }
                });
            } while (cursor.moveToNext());
        }
    }

    private void removeNoVideosAvailableWarning() {
        ViewGroup containerView = (ViewGroup) getView().findViewById(R.id.movie_videos_container);

        View noVideosWarningView = containerView.findViewById(R.id.no_videos_available_warning);
        containerView.removeViewInLayout(noVideosWarningView);
    }
}

