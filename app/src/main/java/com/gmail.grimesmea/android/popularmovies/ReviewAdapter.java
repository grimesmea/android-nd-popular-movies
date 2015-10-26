package com.gmail.grimesmea.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ReviewAdapter extends CursorAdapter {

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        RelativeLayout reviewRelativeLayout = (RelativeLayout) view;
        TextView reviewAuthor = (TextView) reviewRelativeLayout.findViewById(R.id.review_author);
        TextView reviewContent = (TextView) reviewRelativeLayout.findViewById(R.id.review_content);
        Review review = new Review(cursor);
        reviewAuthor.setText(review.getAuthor());
        reviewContent.setText(review.getContent());
    }
}
