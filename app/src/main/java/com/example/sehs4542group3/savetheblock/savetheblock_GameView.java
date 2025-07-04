package com.example.sehs4542group3.savetheblock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;

import java.util.ArrayList;
import java.util.Random;

// Main game view that handles game logic, rendering, and touch input
public class savetheblock_GameView extends View {

    private DatabaseHelper dbHelper;
    private String username;

    Bitmap background, ground, chara;       // Bitmaps for background, ground, and character
    Rect rectBackground, rectGround;        // Rectangles for background and ground positioning
    Context context;                        // Context for accessing resources
    Handler handler;                        // Handler for game loop timing
    final long UPDATE_MILLIS = 30;          // Update interval (30ms) for game loop
    Runnable runnable;                      // Runnable for continuous screen updates
    Paint textPaint = new Paint();          // Paint for drawing score text
    Paint healthPaint = new Paint();        // Paint for drawing health bar
    float TEXT_SIZE = 120;                  // Size of score text
    int points = 0;                         // Player's current score
    int life = 3;                           // Player's remaining lives
    static int dWidth, dHeight;             // Screen dimensions
    Random random;                          // Random number generator
    float charaX, charaY;                   // Character's position
    float oldX;                             // Previous touch X position
    float oldCharaX;                        // Previous character X position
    ArrayList<Spike> spikes;                // List of falling spikes
    ArrayList<Explosion> explosions;        // List of explosion effects

    // Constructor initializes game elements
    public savetheblock_GameView(Context context, String username) {
        super(context);
        this.context = context;
        this.username = username;
        // Load game assets
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background_id7);
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground_id7);
        chara = BitmapFactory.decodeResource(getResources(), R.drawable.chara_id7);
        // Get screen dimensions
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        // Set up background and ground rectangles
        rectBackground = new Rect(0, 0, dWidth, dHeight);
        rectGround = new Rect(0, dHeight - ground.getHeight(), dWidth, dHeight);
        // Initialize game loop
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();  // Trigger redraw
            }
        };
        // Configure text and health bar appearance
        textPaint.setColor(Color.rgb(255, 165, 0));  // Orange score text
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthPaint.setColor(Color.GREEN);           // Green health bar
        random = new Random();
        // Set initial character position (center bottom)
        charaX = dWidth / 2 - chara.getWidth() / 2;
        charaY = dHeight - ground.getHeight() - chara.getHeight();
        // Initialize spikes and explosions lists
        spikes = new ArrayList<>();
        explosions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {  // Create 3 initial spikes
            Spike spike = new Spike(context);
            spikes.add(spike);
        }
    }

    // Draws all game elements on the canvas
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw background and ground
        canvas.drawBitmap(background, null, rectBackground, null);
        canvas.drawBitmap(ground, null, rectGround, null);
        canvas.drawBitmap(chara, charaX, charaY, null);  // Draw character
        // Update and draw spikes
        for (int i = 0; i < spikes.size(); i++) {
            canvas.drawBitmap(spikes.get(i).getSpike(spikes.get(i).spikeFrame), spikes.get(i).spikeX, spikes.get(i).spikeY, null);
            spikes.get(i).spikeFrame++;  // Animate spike
            if (spikes.get(i).spikeFrame > 2) {
                spikes.get(i).spikeFrame = 0;
            }
            spikes.get(i).spikeY += spikes.get(i).spikeVelocity;  // Move spike down
            // If spike hits ground, add points and explosion
            if (spikes.get(i).spikeY + spikes.get(i).getSpikeHeight() >= dHeight - ground.getHeight()) {
                points += 10;
                Explosion explosion = new Explosion(context);
                explosion.explosionX = spikes.get(i).spikeX;
                explosion.explosionY = spikes.get(i).spikeY;
                explosions.add(explosion);
                spikes.get(i).resetPosition();  // Reset spike position
            }
        }

        // Check for collisions between character and spikes
        for (int i = 0; i < spikes.size(); i++) {
            if (spikes.get(i).spikeX + spikes.get(i).getSpikeWidth() >= charaX
                    && spikes.get(i).spikeX <= charaX + chara.getWidth()
                    && spikes.get(i).spikeY + spikes.get(i).getSpikeWidth() >= charaY
                    && spikes.get(i).spikeY + spikes.get(i).getSpikeWidth() <= charaY + chara.getHeight()) {
                life--;  // Lose a life on collision
                spikes.get(i).resetPosition();
                if (life == 0) {  // Game over if no lives left
                    Intent intent = new Intent(context, savetheblock_GameOver.class);
                    intent.putExtra("username", username);
                    intent.putExtra("points", points);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        }

        // Draw explosions
        for (int i = 0; i < explosions.size(); i++) {
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame),
                    explosions.get(i).explosionX, explosions.get(i).explosionY, null);
            explosions.get(i).explosionFrame++;  // Animate explosion
            if (explosions.get(i).explosionFrame > 3) {
                explosions.remove(i);  // Remove finished explosion
            }
        }

        // Update health bar color based on remaining lives
        if (life == 2) {
            healthPaint.setColor(Color.YELLOW);
        } else if (life == 1) {
            healthPaint.setColor(Color.RED);
        }

        // Draw health bar and score
        canvas.drawRect(dWidth - 200, 30, dWidth - 200 + 60 * life, 80, healthPaint);
        canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);
        // Schedule next frame
        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    // Handles touch input to move the character
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (touchY >= charaY) {  // Only move if touch is below character
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {  // Start of touch
                oldX = event.getX();
                oldCharaX = charaX;
            }
            if (action == MotionEvent.ACTION_MOVE) {  // Dragging
                float shift = oldX - touchX;
                float newCharaX = oldCharaX - shift;
                // Keep character within screen bounds
                if (newCharaX <= 0)
                    charaX = 0;
                else if (newCharaX >= dWidth - chara.getWidth())
                    charaX = dWidth - chara.getWidth();
                else
                    charaX = newCharaX;
            }
        }
        return true;  // Event handled
    }
}