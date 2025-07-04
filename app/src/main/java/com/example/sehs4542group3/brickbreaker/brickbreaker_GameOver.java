package com.example.sehs4542group3.brickbreaker;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.HomeActivity;
import com.example.sehs4542group3.R;
import com.example.sehs4542group3.ScoreActivity;
import com.example.sehs4542group3.snakegame.SnakeGameActivity;
import com.example.sehs4542group3.tetris.TetrisActivity;

public class brickbreaker_GameOver extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String username;
    TextView tvPoints;
    TextView tvHighest;
    ImageView ivNewHighest;
    ImageButton tohighscore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brickbreaker_game_over);
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        ivNewHighest = findViewById(R.id.ivNewHighest);
        tvPoints = findViewById(R.id.tvPoints);
        tvHighest = findViewById(R.id.tvHighest);
        tohighscore = findViewById(R.id.tohighscore1);

        int points = getIntent().getExtras().getInt("points");
        tvPoints.setText("" + points);

        int currentHigh = dbHelper.getHighScore(username, 6);
        if (points > currentHigh) {
            ivNewHighest.setVisibility(View.VISIBLE);
            dbHelper.saveScore(username, 6, points);
        } else if (points == currentHigh){
            dbHelper.saveScore(username, 6, points);
        } else if (points == 0) {
            dbHelper.saveScore(username, 6, points);
        } else {
            dbHelper.saveScore(username, 6, points);
        }
        tvHighest.setText("" + currentHigh);

        tohighscore.setOnClickListener(v -> {
            Intent intent = new Intent(brickbreaker_GameOver.this, ScoreActivity.class);
            intent.putExtra("username", username); // Now username is available
            intent.putExtra("gameId", 6);
            startActivity(intent);
            finish();
        });

    }

    public void restart (View view) {
        Intent intent = new Intent(brickbreaker_GameOver.this, brickbreakerActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    public void exit (View view) {
        finish();
    }
}
