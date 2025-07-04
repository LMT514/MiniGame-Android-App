package com.example.sehs4542group3.spaceshooter;

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
import com.example.sehs4542group3.catchtheball.catchtheballActivity;

public class spaceshooterActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String username;
    SpaceShooter spaceShooter;
    ImageButton tohighscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_spaceshooter);
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        tohighscore = findViewById(R.id.tohighscore);

        tohighscore.setOnClickListener(v -> {
            Intent intent = new Intent(spaceshooterActivity.this, ScoreActivity.class);
            intent.putExtra("username", username); // Now username is available
            intent.putExtra("gameId", 10);
            startActivity(intent);
        });
    }

    public void startGame(View v) {
        spaceShooter = new SpaceShooter(this, username);
        spaceShooter.setBackgroundResource(R.drawable.backgroundview_id10);
        setContentView(spaceShooter);
    }
}