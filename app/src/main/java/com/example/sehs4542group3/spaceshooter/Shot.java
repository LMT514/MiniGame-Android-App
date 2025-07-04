package com.example.sehs4542group3.spaceshooter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.sehs4542group3.R;

public class Shot {

    Bitmap shot;
    Context context;
    int shx, shy;

    public Shot (Context context, int shx, int shy) {
        this.context = context;
        shot = BitmapFactory.decodeResource(context.getResources(), R.drawable.shot_id10);
        this.shx = shx;
        this.shy = shy;
    }

    public Bitmap getShot() {
        return shot;
    }
}
