package com.gmail.grimesmea.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MoviePosterAdapter extends CursorAdapter {

    private static final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();


    public MoviePosterAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView posterView = (ImageView) view;
        Movie movie = new Movie(cursor);
        Picasso.with(context).load(movie.getImageUrl(movie.posterImagePath)).into(posterView);
    }
}
