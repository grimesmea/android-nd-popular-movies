package com.gmail.grimesmea.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;

public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private Movie movie;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            movie = (Movie) intent.getBundleExtra("movie").getParcelable("movieParcelable");
        }

        getActivity().setTitle("");

        ImageView backdropView = (ImageView) rootView.findViewById(R.id.detail_fragment_backdrop_imageview);
        Picasso.with(getActivity()).load(movie.getImageUrl(movie.backdropImagePath)).into(backdropView);

        ImageView posterView = (ImageView) rootView.findViewById(R.id.detail_fragment_poster_imageview);
        Picasso.with(getActivity()).load(movie.getImageUrl(movie.posterImagePath)).into(posterView);

        TextView movieTitleView = (TextView) rootView.findViewById(R.id.detail_fragment_movie_title_textview);
        movieTitleView.setText(movie.title);

        TextView movieReleaseDateView = (TextView) rootView.findViewById(R.id.detail_fragment_movie_release_date_textview);
        try {
            movieReleaseDateView.append("\n" + movie.getFormattedReleaseDate());
        } catch (ParseException e) {
            movieReleaseDateView.append("\n" + movie.releaseDate);
            e.printStackTrace();
        }

        TextView movieRatingView = (TextView) rootView.findViewById(R.id.detail_fragment_movie_rating_textview);
        movieRatingView.setText(movie.rating);

        TextView movieSynopsisView = (TextView) rootView.findViewById(R.id.detail_fragment_movie_synopsis_textview);
        movieSynopsisView.setText(movie.synopsis);

        return rootView;
    }
}
