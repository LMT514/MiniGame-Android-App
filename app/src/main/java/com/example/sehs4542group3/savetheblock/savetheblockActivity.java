package com.example.sehs4542group3.savetheblock;

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
import com.example.sehs4542group3.brickbreaker.brickbreakerActivity;

// MainActivity serves as the entry point and launch screen for the game
public class savetheblockActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String username;

    ImageButton tohighscore;

    // Called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the layout to activity_main.xml (start screen)
        setContentView(R.layout.activity_savetheblock);
        // Keeps the screen on while the app is running
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        tohighscore = findViewById(R.id.tohighscore);

        tohighscore.setOnClickListener(v -> {
            Intent intent = new Intent(savetheblockActivity.this, ScoreActivity.class);
            intent.putExtra("username", username); // Now username is available
            intent.putExtra("gameId", 7);
            startActivity(intent);
        });
    }

    // Method triggered when the Start button is clicked
    public void startGame(View view) {
        // Creates a new GameView instance to start the actual game
        savetheblock_GameView gameView = new savetheblock_GameView(this, username);
        // Replaces the current layout with the game view
        setContentView(gameView);
    }
}