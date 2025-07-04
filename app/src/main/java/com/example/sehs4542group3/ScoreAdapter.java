package com.example.sehs4542group3;

import static com.example.sehs4542group3.DatabaseHelper.COL_USERNAME;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ScoreAdapter extends CursorAdapter {
    private boolean isGlobalMode;

    public ScoreAdapter(Context context, Cursor c, boolean isGlobalMode) {
        super(context, c, 0);
        this.isGlobalMode = isGlobalMode;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_score, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView ivRank = view.findViewById(R.id.ivRank);
        TextView userText = view.findViewById(R.id.userText);
        TextView scoreText = view.findViewById(R.id.scoreText);
        TextView dateText = view.findViewById(R.id.dateText);

        // Safely get column indices
        int usernameIndex = cursor.getColumnIndex(COL_USERNAME);
        int scoreIndex = cursor.getColumnIndex("score");
        int timestampIndex = cursor.getColumnIndex("timestamp");

        // Set values only if columns exist
        if (usernameIndex != -1) {
            userText.setText(cursor.getString(usernameIndex));
        }
        if (scoreIndex != -1) {
            scoreText.setText(String.valueOf(cursor.getInt(scoreIndex)));
        }
        if (timestampIndex != -1) {
            dateText.setText(cursor.getString(timestampIndex));
        }

        int position = cursor.getPosition();
        if (isGlobalMode && position < 3) {
            ivRank.setVisibility(View.VISIBLE);
            switch (position) {
                case 0: ivRank.setImageResource(R.drawable.top1); break;
                case 1: ivRank.setImageResource(R.drawable.top2); break;
                case 2: ivRank.setImageResource(R.drawable.top3); break;
            }
        } else {
            ivRank.setVisibility(View.GONE);
        }

        userText.setVisibility(isGlobalMode ? View.VISIBLE : View.GONE);
    }

    public void setGlobalMode(boolean globalMode) {
        isGlobalMode = globalMode;
    }
}