package com.example.sehs4542group3.spaceshooter;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;
import com.example.sehs4542group3.ScoreActivity;
import com.example.sehs4542group3.catchtheball.catchtheball_GameOver;

public class spaceshooter_GameOver extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private String username;
    TextView tvPoints;
    TextView tvHighest;
    ImageView ivNewHighest;
    ImageButton tohighscore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spaceshooter_game_over);
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        tvPoints = findViewById(R.id.tvPoints);
        tvHighest = findViewById(R.id.tvHighest);
        ivNewHighest = findViewById(R.id.ivNewHighest);
        tohighscore = findViewById(R.id.tohighscore1);

        int points = getIntent().getExtras().getInt("points");
        tvPoints.setText("" + points);

        int currentHigh = dbHelper.getHighScore(username, 10);
        if (points > currentHigh) {
            ivNewHighest.setVisibility(View.VISIBLE);
            dbHelper.saveScore(username, 10, points);
        } else if (points == currentHigh){
            ivNewHighest.setVisibility(View.VISIBLE);
            dbHelper.saveScore(username, 10, points);
        } else if (points == 0) {
            dbHelper.saveScore(username, 10, points);
        } else {
            dbHelper.saveScore(username, 10, points);
        }
        tvHighest.setText("" + currentHigh);

        tohighscore.setOnClickListener(v -> {
            Intent intent = new Intent(spaceshooter_GameOver.this, ScoreActivity.class);
            intent.putExtra("username", username); // Now username is available
            intent.putExtra("gameId", 10);
            startActivity(intent);
            finish();
        });

    }

    public void restart(View view) {
        Intent intent = new Intent(this, spaceshooterActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    public void exit(View view) {
        finish();
    }
}
