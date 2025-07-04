package com.example.sehs4542group3;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private String username;
    private int gameId;
    private ListView listView;
    private ScoreAdapter adapter;
    private Spinner gameSpinner;

    private Spinner scoreTypeSpinner;
    private boolean showGlobalScores = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        gameId = getIntent().getIntExtra("gameId",1);
        listView = findViewById(R.id.lvScores);
        gameSpinner = findViewById(R.id.gameSpinner);

        TextView title = findViewById(R.id.titleTextView);
        title.setText("Your Game High Scores");

        setupAdapter();
        setupSpinner();
        scoreTypeSpinner = findViewById(R.id.scoreTypeSpinner);
        setupScoreTypeSpinner();
        loadScores();
    }

    private void setupAdapter() {
        adapter = new ScoreAdapter(this, null, showGlobalScores);
        listView.setAdapter(adapter);
    }

    private void loadScores() {
        new Thread(() -> {
            Cursor cursor = showGlobalScores ?
                    dbHelper.getGlobalHighScores(gameId) :
                    dbHelper.getHighScores(username, gameId);

            runOnUiThread(() -> {
                adapter.setGlobalMode(showGlobalScores);
                adapter.swapCursor(cursor);
                updateTitle();
            });
        }).start();
    }

    private void setupScoreTypeSpinner() {
        String[] scoreTypes = new String[]{"Personal", "Global"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, scoreTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scoreTypeSpinner.setAdapter(spinnerAdapter);

        scoreTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showGlobalScores = (position == 1);
                loadScores();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSpinner() {
        String[] gameNames = new String[]{
                "Game2048",
                "Snake Game",
                "Sudoku",
                "Tic Tac Toe",
                "Tetris",
                "Brick Breaker",
                "Save the Block",
                "Catch the Ball",
                "Guess number game",
                "Space shooter"
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, gameNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameSpinner.setAdapter(spinnerAdapter);

        gameSpinner.setSelection(gameId - 1); // Set initial selection

        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gameId = position + 1;
                loadScores();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateTitle() {
        String gameName;
        switch (gameId) {
            //case 0:  gameName = "Your Game";       break;
            case 1:  gameName = "Game2048";       break;
            case 2:  gameName = "Snake Game";     break;
            case 3:  gameName = "Sudoku";         break;
            case 4:  gameName = "Tic Tac Toe";    break;
            case 5:  gameName = "Tetris";         break;
            case 6:  gameName = "Brick Breaker";  break;
            case 7:  gameName = "Save the Block"; break;
            case 8:  gameName = "Catch the Ball"; break;
            case 9:  gameName = "Guess number game"; break;
            case 10: gameName = "Space shooter";  break ;
            default: gameName = "Your Game";   break;
        }

        // Update the TextView
        TextView title = findViewById(R.id.titleTextView);
        title.setText(gameName + " High Scores");
        String scoreType = showGlobalScores ? "Global" : "Personal";
        title.setText(gameName + " " + scoreType + " High Scores");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
    }
}
