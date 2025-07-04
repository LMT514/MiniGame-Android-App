package com.example.sehs4542group3.spaceshooter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.sehs4542group3.R;

public class spaceshooter_Explosion {

    Bitmap explosion[] = new Bitmap[9];
    int explosionFrame;
    int eX, eY;
    public spaceshooter_Explosion(Context context, int eX, int eY) {
        explosion[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion0_id10);
        explosion[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion1_id10);
        explosion[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion2_id10);
        explosion[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion3_id10);
        explosion[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion4_id10);
        explosion[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion5_id10);
        explosion[6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion6_id10);
        explosion[7] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion7_id10);
        explosion[8] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explosion8_id10);
        explosionFrame = 0;
        this.eX = eX;
        this.eY = eY;
    }

    public Bitmap getExplosion(int explosionFrame) {
        return explosion[explosionFrame];
    }
}
