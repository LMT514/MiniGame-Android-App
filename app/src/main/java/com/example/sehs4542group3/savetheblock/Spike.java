package com.example.sehs4542group3.savetheblock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.sehs4542group3.R;

import java.util.Random;

// Represents a falling spike obstacle in the game
public class Spike {
    Bitmap spike[] = new Bitmap[3];  // Array to hold 3 animation frames of the spike
    int spikeFrame = 0;              // Current frame of spike animation
    int spikeX, spikeY;              // Position coordinates of the spike
    int spikeVelocity;               // Speed at which the spike falls
    Random random;                   // Random number generator for positioning and velocity

    // Constructor initializes spike with animation frames and random position
    public Spike(Context context) {
        // Load spike animation frames from drawable resources
        spike[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.spike0_id7);
        spike[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.spike1_id7);
        spike[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.spike2_id7);
        random = new Random();       // Initialize random generator
        resetPosition();             // Set initial random position
    }

    // Returns the current frame of the spike animation
    public Bitmap getSpike(int spikeFrame) {
        return spike[spikeFrame];
    }

    // Returns the width of the spike (using first frame)
    public int getSpikeWidth() {
        return spike[0].getWidth();
    }

    // Returns the height of the spike (using first frame)
    public int getSpikeHeight() {
        return spike[0].getHeight();
    }

    // Resets spike to a random position above the screen with random velocity
    public void resetPosition() {
        spikeX = random.nextInt(savetheblock_GameView.dWidth - getSpikeWidth()); // Random X within screen width
        spikeY = -200 + random.nextInt(600) * -1;                   // Random Y above screen
        spikeVelocity = 35 + random.nextInt(16);                    // Random fall speed (35-50)
    }
}