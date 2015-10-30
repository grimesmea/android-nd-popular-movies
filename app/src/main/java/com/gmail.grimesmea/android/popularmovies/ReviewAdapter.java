package com.gmail.grimesmea.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ReviewAdapter extends CursorAdapter {

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);

        ReviewViewHolder viewHolder = new ReviewViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ReviewViewHolder viewHolder = (ReviewViewHolder) view.getTag();

        Review review = new Review(cursor);
        viewHolder.authorView.setText(review.getAuthor());
        viewHolder.contentView.setText(review.getContent());
    }

    public static class ReviewViewHolder {
        public final TextView authorView;
        public final TextView contentView;

        public ReviewViewHolder(View view) {
            authorView = (TextView) view.findViewById(R.id.review_author);
            contentView = (TextView) view.findViewById(R.id.review_content);
        }
    }
}
