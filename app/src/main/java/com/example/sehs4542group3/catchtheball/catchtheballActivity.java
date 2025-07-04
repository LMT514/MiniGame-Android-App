package com.example.sehs4542group3.catchtheball;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;
import com.example.sehs4542group3.ScoreActivity;
import com.example.sehs4542group3.savetheblock.savetheblockActivity;

public class catchtheballActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private String username;
    catchtheball_GameView gameView;
    ImageButton tohighscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_catchtheball);
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        tohighscore = findViewById(R.id.tohighscore);

        tohighscore.setOnClickListener(v -> {
            Intent intent = new Intent(catchtheballActivity.this, ScoreActivity.class);
            intent.putExtra("username", username); // Now username is available
            intent.putExtra("gameId", 8);
            startActivity(intent);
        });
    }

    public void startGame(View view) {
        catchtheball_GameView gameView = new catchtheball_GameView(this, username);
        gameView.setBackgroundResource(R.drawable.basketball_court_id8);
        setContentView(gameView);
    }
}