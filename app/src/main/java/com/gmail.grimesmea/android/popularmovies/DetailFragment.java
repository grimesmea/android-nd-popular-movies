package com.gmail.grimesmea.android.popularmovies;

import android.content.ActivityNotFoundException;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            movie = intent.getBundleExtra("movie").getParcelable("movieParcelable");
        }

        if (!hasReviewsForMovie()) {
            fetchReviews();
        }

        if (!hasVideosForMovie()) {
            fetchVideos();
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

    private boolean hasVideosForMovie() {
        return getActivity().getContentResolver().query(
                MoviesContract.VideosEntry.buildVideosForMovieUri(movie.mdbId),
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

    private void fetchVideos() {
        FetchMovieVideosTask moviesVideosTask = new FetchMovieVideosTask(getActivity(), movie.mdbId);
        moviesVideosTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ListView reviewsListView = (ListView) rootView.findViewById(R.id.review_listview);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View headerView = layoutInflater.inflate(R.layout.list_header_movie_details, reviewsListView, false);
        View footerView = layoutInflater.inflate(R.layout.list_footer_videos, reviewsListView, false);
        reviewAdapter = new ReviewAdapter(getActivity(), null, 0);

        getActivity().setTitle("");
        reviewsListView.addHeaderView(headerView);
        reviewsListView.addHeaderView(footerView);
        reviewsListView.setAdapter(reviewAdapter);

        ImageView backdropView = (ImageView) rootView.findViewById(R.id.detail_fragment_backdrop_imageview);
        Picasso.with(getActivity()).load(movie.getBackdropImageUrl()).into(backdropView);

        ImageView posterView = (ImageView) rootView.findViewById(R.id.detail_fragment_poster_imageview);
        Picasso.with(getActivity()).load(movie.getPosterImageUrl()).into(posterView);

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
        getLoaderManager().initLoader(VIDEOS_LOADER, null, this);
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

    private void addVideosTextView(final Cursor cursor) {
        ViewGroup containerView = (ViewGroup) getView().findViewById(R.id.movie_videos_container);

        if (cursor.moveToFirst() && cursor.getCount() >= 1) {
            do {
                final Video video = new Video(cursor);
                View v = getLayoutInflater(null).inflate(R.layout.list_item_video, null);
                final View rootView = v.getRootView();
                TextView videoName = (TextView) v.findViewById(R.id.list_item_video_textview);
                videoName.setText(video.name);
                videoName.setTag(video);

                containerView.addView(rootView);

                rootView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    "vnd.youtube:" + video.key));
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
}

