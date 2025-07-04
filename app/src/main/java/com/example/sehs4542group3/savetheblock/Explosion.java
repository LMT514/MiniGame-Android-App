package com.example.sehs4542group3.savetheblock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.sehs4542group3.R;

// Represents an explosion animation when a spike hits the ground
public class Explosion {
    Bitmap explosion[] = new Bitmap[4];  // Array to hold 4 animation frames of the explosion
    int explosionFrame = 0;              // Current frame of explosion animation
    int explosionX, explosionY;          // Position coordinates of the explosion

    // Constructor initializes explosion with animation frames
    public Explosion(Context context) {
        // Load explosion animation frames from drawable resources
        explosion[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode0_id7);
        explosion[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode1_id7);
        explosion[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode2_id7);
        explosion[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.explode3_id7);
    }

    // Returns the current frame of the explosion animation
    public Bitmap getExplosion(int explosionFrame) {
        return explosion[explosionFrame];
    }
}
