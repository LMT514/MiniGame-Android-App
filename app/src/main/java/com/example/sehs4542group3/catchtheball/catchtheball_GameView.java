package com.example.sehs4542group3.catchtheball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;

import java.util.Random;

public class catchtheball_GameView extends View {

    private DatabaseHelper dbHelper;
    private String username;
    int dWidth, dHeight;
    Bitmap bucket, hand, basketball;
    Handler handler;
    Runnable runnable;
    long UPDATE_MILLIS = 30;
    int handX, handY;
    int ballX, ballY;
    Random random;
    boolean ballAnimation = false;
    int points = 0;
    float TEXT_SIZE = 120;
    Paint textPaint;
    Paint healthPaint;
    int life = 3;
    Context context;
    int handSpeed;
    int basketballX, basketballY;
    MediaPlayer mpPoint, mpWhoosh, mpPop;

    public catchtheball_GameView(Context context, String username) {
        super(context);
        this.context = context;
        this.username = username;
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        bucket = BitmapFactory.decodeResource(getResources(), R.drawable.bucket_id8);
        hand = BitmapFactory.decodeResource(getResources(), R.drawable.hand_id8);
        basketball = BitmapFactory.decodeResource(getResources(), R.drawable.basketball_id8);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        random = new Random();
        handX = dWidth + random.nextInt(300);
        handY = random.nextInt(600);
        ballX = handX;
        ballY = handY + hand.getHeight() - 90;
        textPaint = new Paint();
        textPaint.setColor(Color.rgb(255, 0, 0));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthPaint = new Paint();
        healthPaint.setColor(Color.GREEN);
        handSpeed = 21 + random.nextInt(15);
        basketballX = dWidth/2 - bucket.getWidth()/2;
        basketballY = dHeight - bucket.getHeight();
        mpPoint = MediaPlayer.create(context, R.raw.point);
        mpWhoosh = MediaPlayer.create(context, R.raw.whoosh);
        mpPop = MediaPlayer.create(context, R.raw.pop);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (ballAnimation == false) {
            handX -= handSpeed;
            ballX -= handSpeed;
        }
        if (handX <= -hand.getWidth()) {
            if (mpWhoosh != null) {
                mpWhoosh.start();
            }
            handX = dWidth + random.nextInt(300);
            ballX = handX;
            handY = random.nextInt(600);
            ballY = handY + hand.getHeight() - 90;
            handSpeed = 21 + random.nextInt(15);
            basketballX = hand.getWidth() + random.nextInt(dWidth - 2*hand.getWidth());
            life--;
            if (life == 0) {
                Intent intent = new Intent(context, catchtheball_GameOver.class);
                intent.putExtra("username", username);
                intent.putExtra("points", points);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        }
        if (ballAnimation) {
            ballY += 40;
        }
        if (ballAnimation
        && (ballX + basketball.getWidth() >= basketballX)
        && (ballX <= basketballX + bucket.getWidth())
        && (ballY + basketball.getHeight() >= (dHeight-bucket.getHeight()))
        && ballY <= dHeight) {
            if (mpPoint != null) {
                mpPoint.start();
            }
            handX = dWidth + random.nextInt(300);
            ballX = handX;
            handY = random.nextInt(600);
            ballY = handY + hand.getHeight() - 90;
            handSpeed = 21 + random.nextInt(15);
            points++;
            basketballX = hand.getWidth() + random.nextInt(dWidth - 2*hand.getWidth());
            ballAnimation = false;
        }
        if (ballAnimation && (ballY + basketball.getHeight()) >= dHeight) {
            if (mpPop != null) {
                mpPop.start();
            }
            life--;
            if (life == 0) {
                Intent intent = new Intent(context, catchtheball_GameOver.class);
                intent.putExtra("username", username);
                intent.putExtra("points", points);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
            handX = dWidth + random.nextInt(300);
            ballX = handX;
            handY = random.nextInt(600);
            ballY = handY + hand.getHeight() - 90;
            basketballX = hand.getWidth() + random.nextInt(dWidth - 2*hand.getWidth());
            ballAnimation = false;
        }
        canvas.drawBitmap(bucket, basketballX, basketballY, null);
        canvas.drawBitmap(hand, handX, handY, null);
        canvas.drawBitmap(basketball, ballX, ballY, null);
        canvas.drawText(""+ points, 20, TEXT_SIZE, textPaint);
        if (life == 2) {
            healthPaint.setColor(Color.YELLOW);
        } else if (life == 1) {
            healthPaint.setColor(Color.RED);
        }
        canvas.drawRect(dWidth-200, 30, dWidth-200+60*life, 80, healthPaint);
        if (life != 0) {
            handler.postDelayed(runnable, UPDATE_MILLIS);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (ballAnimation == false &&
                    (touchX >= handX && touchX <= (handX + hand.getWidth())
                    && touchY >= handY && touchY <= (handY + hand.getHeight()))) {
                ballAnimation = true;
            }
        }
        return true;
    }
}
