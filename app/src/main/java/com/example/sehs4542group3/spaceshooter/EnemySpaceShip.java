package com.example.sehs4542group3.spaceshooter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.sehs4542group3.R;

import java.util.Random;

public class EnemySpaceShip {
    Context context;
    Bitmap enemySpaceship;
    int ex, ey;
    int enemyVelocity;
    Random random;

    public EnemySpaceShip(Context context) {
        this.context = context;
        enemySpaceship = BitmapFactory.decodeResource(context.getResources(), R.drawable.rocket2_id10);
        random = new Random();
        resetEnemySpaceship();
    }

    public Bitmap getEnemySpaceship() {
        return enemySpaceship;
    }

    int getEnemySpaceshipWidth() {
        return enemySpaceship.getWidth();
    }

    int getEnemySpaceshipHeight() {
        return enemySpaceship.getHeight();
    }

    private void resetEnemySpaceship() {
        ex = 200 + random.nextInt(400);
        ey = 0;
        enemyVelocity = 14 + random.nextInt(10);
    }
}
