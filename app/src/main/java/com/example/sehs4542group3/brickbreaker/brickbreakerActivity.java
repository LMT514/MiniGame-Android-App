package com.example.sehs4542group3.brickbreaker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;
import com.example.sehs4542group3.ScoreActivity;

public class brickbreakerActivity extends AppCompatActivity{

    private DatabaseHelper dbHelper;
    private String username;

    ImageButton tohighscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_brickbreaker);
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        tohighscore = findViewById(R.id.tohighscore);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tohighscore.setOnClickListener(v -> {
            Intent intent = new Intent(brickbreakerActivity.this, ScoreActivity.class);
            intent.putExtra("username", username); // Now username is available
            intent.putExtra("gameId", 6);
            startActivity(intent);
        });
    }

    public void startGame(View view) {
        brickbreaker_GameView gameView = new brickbreaker_GameView(this, username);
        setContentView(gameView);
    }

}