package com.example.sehs4542group3.savetheblock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;
import com.example.sehs4542group3.ScoreActivity;

// Activity for displaying game over screen with score and options
public class savetheblock_GameOver extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String username;
    TextView tvPoints;              // TextView for current points
    TextView tvHighest;             // TextView for highest score
    ImageView ivNewHighest;         // ImageView for new high score indicator
    ImageButton tohighscore;

    // Called when the activity is created
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savetheblock_game_over);  // Sets game over layout
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        // Initialize UI elements
        tvPoints = findViewById(R.id.tvPoints);
        tvHighest = findViewById(R.id.tvHighest);
        ivNewHighest = findViewById(R.id.ivNewHighest);
        tohighscore = findViewById(R.id.tohighscore1);

        // Get points from game and display
        int points = getIntent().getExtras().getInt("points");
        tvPoints.setText("" + points);

        int currentHigh = dbHelper.getHighScore(username, 7);
        if (points > currentHigh) {
            ivNewHighest.setVisibility(View.VISIBLE);
            dbHelper.saveScore(username, 7, points);
        } else if (points == currentHigh){
            dbHelper.saveScore(username, 7, points);
        } else if (points == 0) {
            dbHelper.saveScore(username, 7, points);
        } else {
            dbHelper.saveScore(username, 7, points);
        }
        tvHighest.setText("" + currentHigh);

        tohighscore.setOnClickListener(v -> {
            Intent intent = new Intent(savetheblock_GameOver.this, ScoreActivity.class);
            intent.putExtra("username", username); // Now username is available
            intent.putExtra("gameId", 7);
            startActivity(intent);
            finish();
        });
    }

    // Restarts the game when restart button is clicked
    public void restart(View view) {
        Intent intent = new Intent(savetheblock_GameOver.this, savetheblockActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);  // Go back to main screen
        finish();              // Close game over screen
    }

    // Exits the game when exit button is clicked
    public void exit(View view) {
        finish();  // Close the activity and exit
    }
}