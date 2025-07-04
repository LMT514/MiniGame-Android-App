package com.example.sehs4542group3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sehs4542group3.brickbreaker.brickbreakerActivity;
import com.example.sehs4542group3.catchtheball.catchtheballActivity;
import com.example.sehs4542group3.game2048.Game2048Activity;
import com.example.sehs4542group3.guessnumber.guessnumberActivity;
import com.example.sehs4542group3.savetheblock.savetheblockActivity;
import com.example.sehs4542group3.snakegame.SnakeGameActivity;
import com.example.sehs4542group3.spaceshooter.spaceshooterActivity;
import com.example.sehs4542group3.sudoku.SudokuActivity;
import com.example.sehs4542group3.tetris.TetrisActivity;
import com.example.sehs4542group3.tictactoe.tictactoe_AddPlayers;
import com.example.sehs4542group3.tictactoe.tictactoeActivity;

public class HomeActivity extends AppCompatActivity {
    private String username; // Declare username at class level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get username from Intent extras
        username = getIntent().getStringExtra("username");

        // Validate username exists
        if(username == null || username.isEmpty()) {
            Toast.makeText(this, "User not identified!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up game buttons
        ImageButton btn2048 = findViewById(R.id.btnStartGame2048);
        ImageButton btnSnake = findViewById(R.id.btnStartsnakegame);
        ImageButton btnSudoku = findViewById(R.id.btnStartsudoku);
        ImageButton btnTictactoe = findViewById(R.id.btnStarttictactoe);
        ImageButton btnTetris = findViewById(R.id.btnStarttetris);
        ImageButton btnBrickerbreaker = findViewById(R.id.btnStartbickerbreaker);
        ImageButton btnSavetheblock = findViewById(R.id.btnStartsavetheblock);
        ImageButton btnCatchtheball = findViewById(R.id.btnStartcatchthebball);
        ImageButton btnGuessnumber = findViewById(R.id.btnStartguessnumber);
        ImageButton btnSpaceshooter = findViewById(R.id.btnStartspaceshooter);
        Button btnScores = findViewById(R.id.btnScores);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnExit = findViewById(R.id.btnExit);

        btn2048.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, Game2048Activity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        btnSnake.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SnakeGameActivity.class);
            intent.putExtra("username", username); // Now username is available
            startActivity(intent);
        });

        btnSudoku.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SudokuActivity.class);
            intent.putExtra("username", username); // Now username is available
            startActivity(intent);
        });

        btnTictactoe.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, tictactoe_AddPlayers.class);
            intent.putExtra("username", username); // Now username is available
            startActivity(intent);
        });

        btnTetris.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TetrisActivity.class);
            intent.putExtra("username", username); // Now username is available
            startActivity(intent);
        });

        btnBrickerbreaker.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, brickbreakerActivity.class);
            intent.putExtra("username", username); // Now username is available
            startActivity(intent);
        });

        btnSavetheblock.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, savetheblockActivity.class);
            intent.putExtra("username", username); // Now username is available
            startActivity(intent);
        });

        btnCatchtheball.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, catchtheballActivity.class);
            intent.putExtra("username", username); // Now username is available
            startActivity(intent);
        });

        btnGuessnumber.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, guessnumberActivity.class);
            intent.putExtra("username", username); // Now username is available
            startActivity(intent);
        });

        btnSpaceshooter.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, spaceshooterActivity.class);
            intent.putExtra("username", username); // Now username is available
            startActivity(intent);
        });

        btnScores.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ScoreActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("gameId", 0);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Button btnAccount = findViewById(R.id.btnAccount);
        btnAccount.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
            intent.putExtra("username", username);
            startActivityForResult(intent, 1);
        });

        btnExit.setOnClickListener(v -> {
            finish();
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newUsername = data.getStringExtra("username");
            if (newUsername != null) {
                username = newUsername;
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
                if (isLoggedIn) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", newUsername);
                    editor.apply();
                }
            }
        }
    }
}
