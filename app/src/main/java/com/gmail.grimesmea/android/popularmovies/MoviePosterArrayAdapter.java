package com.gmail.grimesmea.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviePosterArrayAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MoviePosterArrayAdapter.class.getSimpleName();


    public MoviePosterArrayAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item_movie, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.grid_item_movie_imageview);
        Picasso.with(getContext()).load(movie.getPosterImageUrl()).into(iconView);

        return convertView;
    }
}
