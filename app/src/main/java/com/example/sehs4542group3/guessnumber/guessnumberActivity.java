package com.example.sehs4542group3.guessnumber;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;
import com.example.sehs4542group3.ScoreActivity;

import java.util.Random;

public class guessnumberActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private String username;
    private TextView resultText, attemptsText;
    private EditText guessInput;
    private Button guessButton;
    private int randomNumber;
    private int attemptsLeft = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guessnumber);
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        resultText = findViewById(R.id.resultText);
        attemptsText = findViewById(R.id.attemptsText);
        guessInput = findViewById(R.id.guessInput);
        guessButton = findViewById(R.id.guessButton);

        generateNewNumber();

        // Set initial attempts display
        attemptsText.setText("Attempts left: " + attemptsLeft);

        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess();
            }
        });
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }
    private void generateNewNumber() {
        Random random = new Random();
        randomNumber = random.nextInt(10) + 1; // change the random number *set at 1-10
    }

    private void checkGuess() {
        String input = guessInput.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
            return;
        }

        int userGuess = Integer.parseInt(input);

        if (userGuess < 1 || userGuess > 100) {
            Toast.makeText(this, "Please enter between 1-100", Toast.LENGTH_SHORT).show();
            return;
        }

        attemptsLeft--;
        attemptsText.setText("Attempts left: " + attemptsLeft);

        if (userGuess == randomNumber) {
            resultText.setText("Correct!");
            int score = calculateScore();
            showGameOverDialog(score);
            dbHelper.saveScore(username, 9, score);
        } else if (userGuess < randomNumber) {
            resultText.setText("Too low!");
        } else {
            resultText.setText("Too high!");
        }

        guessInput.setText("");

        if (attemptsLeft <= 0 && userGuess!=randomNumber) {
            resultText.setText("No attempts left! Number was " + randomNumber);
            guessButton.setEnabled(false);
            showGameOverDialog(0);
        }
    }

    private int calculateScore() {
        // Calculate score based on remaining attempts
        return (attemptsLeft * 10) + 5;
    }

    private void showGameOverDialog(int score) {
        if(score == 0 ){
            runOnUiThread(() -> {
                new AlertDialog.Builder(this)
                        .setTitle("Opps, you are unlucky!")
                        .setMessage("You lose!\nScore: " + score)
                        .setPositiveButton("Retry", (dialog, which) -> resetGame())
                        .setNeutralButton("High Scores", (dialog, which) -> {
                            Intent intent = new Intent(guessnumberActivity.this, ScoreActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("gameId", 9);
                            startActivity(intent);
                        })
                        .setNegativeButton("Menu", (dialog, which) -> finish())
                        .show();
            });
        }else {
            runOnUiThread(() -> {
                new AlertDialog.Builder(this)
                        .setTitle("Congratulations!")
                        .setMessage("You won!\nScore: " + score)
                        .setPositiveButton("Retry", (dialog, which) -> resetGame())
                        .setNeutralButton("High Scores", (dialog, which) -> {
                            Intent intent = new Intent(guessnumberActivity.this, ScoreActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("gameId", 9);
                            startActivity(intent);
                        })
                        .setNegativeButton("Menu", (dialog, which) -> finish())
                        .show();
            });
        }

    }

    private void resetGame() {
        attemptsLeft = 5;
        attemptsText.setText("Attempts left: " + attemptsLeft);
        resultText.setText("");
        guessInput.setText("");
        guessButton.setEnabled(true);
        generateNewNumber();
    }
}