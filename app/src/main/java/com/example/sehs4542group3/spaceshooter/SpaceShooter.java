package com.example.sehs4542group3.spaceshooter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;

import java.util.ArrayList;
import java.util.Random;

public class SpaceShooter extends View {
    private DatabaseHelper dbHelper;
    private String username;
    Context context;
    Bitmap lifeImage;
    Handler handler;
    long UPDATE_MILLIS = 30;
    static int screenWidth, screenHeight;
    int points = 0;
    int life = 3;
    Paint scorePaint;
    int TEXT_SIZE = 80;
    boolean paused = false;
    OurSpaceship ourSpaceship;
    EnemySpaceShip enemySpaceShip;
    Random random;
    ArrayList<Shot> enemyShots, ourShots;
    boolean enemyExplosion = false;
    spaceshooter_Explosion explosion;
    ArrayList<spaceshooter_Explosion> explosions;
    boolean enemyShotAction = false;
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public SpaceShooter (Context context, String username) {
        super(context);
        this.context = context;
        this.username = username;
        random = new Random();
        enemyShots = new ArrayList<>();
        ourShots = new ArrayList<>();
        explosions = new ArrayList<>();
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        ourSpaceship = new OurSpaceship(context);
        enemySpaceShip = new EnemySpaceShip(context);
        lifeImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.life_id10);
        handler = new Handler();
        scorePaint = new Paint();
        scorePaint.setColor(Color.RED);
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.drawText("Score: " + points, 0, TEXT_SIZE, scorePaint);
        for(int i = life; i>=1; i--) {

            canvas.drawBitmap(lifeImage, screenWidth - lifeImage.getWidth() * i, 0, null);

        }
        if (points >= 20) {
            paused = true;
            handler = null;
            Intent intent;
            intent = new Intent(context, spaceshooter_GameOver.class);
            intent.putExtra("username", username);
            intent.putExtra("points", points);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
        if (life == 0) {
            paused = true;
            handler = null;
            Intent intent;
            intent = new Intent(context, spaceshooter_GameOver.class);
            intent.putExtra("username", username);
            intent.putExtra("points", points);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
        enemySpaceShip.ex += enemySpaceShip.enemyVelocity;
        if (enemySpaceShip.ex + enemySpaceShip.getEnemySpaceshipWidth() >= screenWidth) {
            enemySpaceShip.enemyVelocity *= -1;
        }
        if (enemySpaceShip.ex <= 0) {
            enemySpaceShip.enemyVelocity *= -1;
        }
        if ((enemyShotAction == false) && (enemySpaceShip.ex >= 200 + random.nextInt(400))) {
            Shot enemyShot = new Shot (context, enemySpaceShip.ex + enemySpaceShip.getEnemySpaceshipWidth()/2, enemySpaceShip.ey );
            enemyShots.add(enemyShot);
            enemyShotAction = true;
        }
        if (!enemyExplosion) {
            canvas.drawBitmap(enemySpaceShip.getEnemySpaceship(), enemySpaceShip.ex, enemySpaceShip.ey, null);
        }
        if (ourSpaceship.isAlive) {
            if (ourSpaceship.ox > screenWidth - ourSpaceship.getOurSpaceshipWidth()) {
                ourSpaceship.ox = screenWidth - ourSpaceship.getOurSpaceshipWidth();
            } else if (ourSpaceship.ox < 0) {
                ourSpaceship.ox = 0;
            }
            canvas.drawBitmap(ourSpaceship.getOurSpaceship(), ourSpaceship.ox, ourSpaceship.oy, null);
        }
        for (int i =0; i < enemyShots.size(); i++) {
            enemyShots.get(i).shy += 15;
            canvas.drawBitmap(enemyShots.get(i).getShot(), enemyShots.get(i).shx, enemyShots.get(i).shy, null);
            if ((enemyShots.get(i).shx >= ourSpaceship.ox)
            && (enemyShots.get(i).shx <= ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth())
            && (enemyShots.get(i).shy >= ourSpaceship.oy)
            && (enemyShots.get(i).shy <= screenHeight)) {
                life--;
                enemyShots.remove(i);
                explosion = new spaceshooter_Explosion(context, ourSpaceship.ox, ourSpaceship.oy);
                explosions.add(explosion);
            } else if (enemyShots.get(i).shy >= screenHeight) {
                enemyShots.remove(i);
            }
            if (enemyShots.size() == 0) {
                enemyShotAction = false;
            }
        }

        for (int i = 0; i < ourShots.size(); i++) {
            ourShots.get(i).shy -= 15;
            canvas.drawBitmap(ourShots.get(i).getShot(), ourShots.get(i).shx, ourShots.get(i).shy, null);
            if ((ourShots.get(i).shx >= enemySpaceShip.ex)
            && (ourShots.get(i).shx <= enemySpaceShip.ex + enemySpaceShip.getEnemySpaceshipWidth())
            && (ourShots.get(i).shy <= enemySpaceShip.getEnemySpaceshipHeight())
            && (ourShots.get(i).shy >= enemySpaceShip.ey)) {
                points++;
                ourShots.remove(i);
                explosion = new spaceshooter_Explosion(context, enemySpaceShip.ex, enemySpaceShip.ey);
                explosions.add(explosion);
            } else if (ourShots.get(i).shy <= 0) {
                ourShots.remove(i);
            }
        }

        for (int i = 0; i <explosions.size(); i++) {
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame),
                    explosions.get(i).eX,
                    explosions.get(i).eY, null);
            explosions.get(i).explosionFrame++;
            if (explosions.get(i).explosionFrame > 8) {
                explosions.remove(i);
            }
        }

        if (!paused) {
            handler.postDelayed(runnable, UPDATE_MILLIS);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int) event.getX();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (ourShots.size() < 3) {
                Shot ourShot = new Shot(context, ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth()/2, ourSpaceship.oy);
                ourShots.add(ourShot);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ourSpaceship.ox = touchX;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            ourSpaceship.ox = touchX;
        }
        return true;
    }
}
