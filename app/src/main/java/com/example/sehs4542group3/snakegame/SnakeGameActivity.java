package com.example.sehs4542group3.snakegame;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import androidx.core.view.GestureDetectorCompat;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;
import com.example.sehs4542group3.ScoreActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SnakeGameActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnTouchListener {
    private DatabaseHelper dbHelper;
    private String username;
    private final List<SnakePoints> snakePointsList = new ArrayList<>();
    private GestureDetectorCompat gestureDetector;
    private SurfaceView surfaceView;
    private TextView scoreTV;
    //getting  surface holder to draw snake on surface's canvas
    private SurfaceHolder surfaceHolder;
    private String movingPosition = "right";
    private int score = 0;
    private static final int pointSize = 28;
    // default snake tale
    private static final int defaultTalePoints = 3;
    // snake color
    private static final int snakeColor = Color.RED;
    // snake moving speed. Value must be 1 - 1000
    private static final int snakeMovingSpeed = 800;
    //random point position coordinates on the surfacesView
    private int positionX, positionY;
    // timer to move snake / change snake position after a specific time (snakeMovingSpeed)
    private Timer timer;
    // canvas to draw snake and show on surface view
    private Canvas canvas = null;
    // point color / single point color of a snake
    private Paint pointColor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_snakegame);
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        surfaceView = findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);
        surfaceView.setOnTouchListener((View.OnTouchListener) this);
        scoreTV = findViewById(R.id.scoreTV);
        gestureDetector = new GestureDetectorCompat(this, new MyGestureListener());


        // getting ImageButtons from xml file
        final AppCompatImageButton topBtn = findViewById(R.id.topBtn);
        final AppCompatImageButton leftBtn = findViewById(R.id.leftBtn);
        final AppCompatImageButton rightBtn = findViewById(R.id.rightBtn);
        final AppCompatImageButton bottomBtn = findViewById(R.id.bottomBtn);

        // adding callback to surfaceView
        surfaceView.getHolder().addCallback(this);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check if previous moving position in not bottom. Snake can't move.
                // For  example if snake moving to bottom then snake can't directly start moving to start
                // snake must take right or left first then top
                if(!movingPosition.equals("bottom")) {
                    movingPosition = "top";
                }
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!movingPosition.equals("right")) {
                    movingPosition = "left";
                }
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!movingPosition.equals("left")) {
                    movingPosition = "right";
                }
            }
        });

        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!movingPosition.equals("top")) {
                    movingPosition = "bottom";
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe Right
                        if (!movingPosition.equals("left")) {
                            movingPosition = "right";
                        }
                    } else {
                        // Swipe Left
                        if (!movingPosition.equals("right")) {
                            movingPosition = "left";
                        }
                    }
                    return true;
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        // Swipe Bottom
                        if (!movingPosition.equals("top")) {
                            movingPosition = "bottom";
                        }
                    } else {
                        // Swipe Top
                        if (!movingPosition.equals("bottom")) {
                            movingPosition = "top";
                        }
                    }
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

        // when surface is created then get surfaceHolder from it and assign to surfaceHolder
        this.surfaceHolder = holder;

        // init data for snake  / surfaceView
        init();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    private void init() {


        // clear snake points / snake length
        snakePointsList.clear();

        // set default score as 0
        scoreTV.setText("0");

        // make score 0
        score = 0;

        // setting default moving position
        movingPosition = "right";

        // default snake starting position on the screen
        int startPositionX = (pointSize) * defaultTalePoints;

        // making snake default length / points
        for (int i = 0; i < defaultTalePoints; i++) {

            // adding points to snake's tale
            SnakePoints snakePoints = new SnakePoints(startPositionX, pointSize);
            snakePointsList.add(snakePoints);

            // increasing value for next point as snake's tale
            startPositionX = startPositionX - (pointSize * 2);
        }

        // add random point on the screen to be eaten by the snake
        addPoint();

        // start moving snake / start game
        moveSnake();
    }


    private void addPoint() {

        // getting surfaceView width and height to add the point on the surface to be eaten by the snake
        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight =  surfaceView.getHeight() - (pointSize * 2);

        int randomXPosition = new Random().nextInt(surfaceWidth / pointSize);
        int randomYPosition = new Random().nextInt(surfaceHeight / pointSize);

        // check if randomXPosition is even or odd value. we need only even number
        if((randomXPosition % 2) != 0) {
            randomXPosition = randomXPosition + 1;
        }

        if((randomYPosition % 2) != 0) {
            randomYPosition = randomYPosition + 1;
        }

        positionX = (pointSize * randomXPosition) + pointSize;
        positionY = (pointSize * randomYPosition) + pointSize;
    }

    private void moveSnake() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @SuppressLint("DiscouragedApi")
            @Override
            public void run() {
                // Getting head position
                int headPositionX = snakePointsList.get(0).getPositionX();
                int headPositionY = snakePointsList.get(0).getPositionY();

                // Check if snake eaten a point
                if (headPositionX == positionX && headPositionY == positionY) {
                    // Grow snake after eating a point
                    growSnake();

                    // Add another random point on screen
                    addPoint();
                }

                // Move snake's body points
                for (int i = snakePointsList.size() - 1; i > 0; i--) {
                    snakePointsList.get(i).setPositionX(snakePointsList.get(i - 1).getPositionX());
                    snakePointsList.get(i).setPositionY(snakePointsList.get(i - 1).getPositionY());
                }

                // Move snake's head
                switch (movingPosition) {
                    case "right":
                        snakePointsList.get(0).setPositionX(headPositionX + (pointSize * 2));
                        break;
                    case "left":
                        snakePointsList.get(0).setPositionX(headPositionX - (pointSize * 2));
                        break;
                    case "top":
                        snakePointsList.get(0).setPositionY(headPositionY - (pointSize * 2));
                        break;
                    case "bottom":
                        snakePointsList.get(0).setPositionY(headPositionY + (pointSize * 2));
                        break;
                }

                headPositionX = snakePointsList.get(0).getPositionX();
                headPositionY = snakePointsList.get(0).getPositionY();

                // Check if game over.  Whether snake touches edges or snake itself
                if (checkGameOver(headPositionX, headPositionY)) {
                    // Stop timer
                    timer.cancel();
                    timer.purge();

                    // Show game over dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(SnakeGameActivity.this);
                    builder.setMessage("Your Score: " + score);
                    builder.setTitle("Game Over");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Menu", (dialog, which) -> finish());
                    builder.setPositiveButton("Start again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Restart the game / re-init data
                            init();
                        }
                    });
                    builder.setNeutralButton("High Scores", (dialog, which) -> {
                        Intent intent = new Intent(SnakeGameActivity.this, ScoreActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("gameId", 2); // snake game ID
                        startActivity(intent);
                    });

                    // Timer runs in background, so we need to show dialog on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    });
                } else {
                    // Lock canvas on surfaceHolder to draw on it
                    canvas = surfaceHolder.lockCanvas();
                    if (canvas != null) {
                        try {
                            // Clear canvas with white color;
                            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

                            // Draw snake points
                            for (SnakePoints point : snakePointsList) {
                                canvas.drawCircle(point.getPositionX(), point.getPositionY(), pointSize, createPaintColor());
                            }

                            // Draw random point circle on the surface to be eaten by the snake
                            canvas.drawCircle(positionX, positionY, pointSize, createPaintColor());
                        } finally {
                            // Unlock canvas to draw on surfaceView
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }, 1000 - snakeMovingSpeed, 1000 - snakeMovingSpeed);
    }

    private void growSnake() {

        // create new snake point
        SnakePoints snakePoints = new SnakePoints(0, 0);

        // add point to the snake's tale
        snakePointsList.add(snakePoints);

        // increase score
        score++;

        // setting score to TextViews
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTV.setText(String.valueOf(score));
            }
        });
    }

    private boolean checkGameOver(int headPositionX, int headPositionY) {
        boolean gameOver = false;
        // Check if snake's head touches edges
        if (snakePointsList.get(0).getPositionX() < 0 ||
                snakePointsList.get(0).getPositionY() < 0 ||
                snakePointsList.get(0).getPositionX() >= surfaceView.getWidth() ||
                snakePointsList.get(0).getPositionY() >= surfaceView.getHeight() )
        {
            gameOver = true;
            dbHelper.saveScore(username, 2, score);
        } else {
            // Check if snake's head touches snake itself
            for (int i = 1; i < snakePointsList.size(); i++) {
                if (headPositionX == snakePointsList.get(i).getPositionX() &&
                        headPositionY == snakePointsList.get(i).getPositionY() )
                {
                    gameOver = true;
                    dbHelper.saveScore(username, 2, score);
                    break;
                }
            }
        }
        return gameOver;
    }

    private Paint createPaintColor() {

        // check if color not defined here
        if(pointColor == null) {

            pointColor = new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true); // smoothness

        }

        return pointColor;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

}