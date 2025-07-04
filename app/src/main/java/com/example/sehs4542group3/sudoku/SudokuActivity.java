package com.example.sehs4542group3.sudoku;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;
import com.example.sehs4542group3.ScoreActivity;
import com.example.sehs4542group3.snakegame.SnakeGameActivity;

import org.w3c.dom.Text;

public class SudokuActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHelper dbHelper;
    private String username;
    private SudokuBoard gameBoard;
    private Solver gameBoardSolver;
    private PlayerSolver playerBoardSolver;
    private Button solveBTN;
    private TextView tv_score;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private Button btn8;
    private Button btn9;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sudoku);
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gameBoard = findViewById(R.id.SudokuBoard);
        gameBoardSolver = gameBoard.getSolver();
        playerBoardSolver = gameBoard.getPlayer();

        solveBTN = findViewById(R.id.solveButton);

        tv_score = findViewById(R.id.tv_score);

        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        btn5 = findViewById(R.id.button5);
        btn6 = findViewById(R.id.button6);
        btn7 = findViewById(R.id.button7);
        btn8 = findViewById(R.id.button8);
        btn9 = findViewById(R.id.button9);


        solveBTN.setOnClickListener((View.OnClickListener) this);
        btn1.setOnClickListener((View.OnClickListener) this);
        btn2.setOnClickListener((View.OnClickListener) this);
        btn3.setOnClickListener((View.OnClickListener) this);
        btn4.setOnClickListener((View.OnClickListener) this);
        btn5.setOnClickListener((View.OnClickListener) this);
        btn6.setOnClickListener((View.OnClickListener) this);
        btn7.setOnClickListener((View.OnClickListener) this);
        btn8.setOnClickListener((View.OnClickListener) this);
        btn9.setOnClickListener((View.OnClickListener) this);

        gameBoardSolver.addNumbersRandomly(36);
        gameBoard.invalidate();

        SolveBoardThread solveBoardThread = new SolveBoardThread();
        new Thread(solveBoardThread).start();
        gameBoard.invalidate();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button1) {
            playerBoardSolver.setNumberPos(1);
            gameBoard.invalidate();
            tv_score.setText("Score: " + gameBoard.getScoreCondition());
            checkCondition();

        } else if (id == R.id.button2) {
            playerBoardSolver.setNumberPos(2);
            gameBoard.invalidate();
            tv_score.setText("Score: " + gameBoard.getScoreCondition());
            checkCondition();

        } else if (id == R.id.button3) {
            playerBoardSolver.setNumberPos(3);
            gameBoard.invalidate();
            tv_score.setText("Score: " + gameBoard.getScoreCondition());
            checkCondition();

        } else if (id == R.id.button4) {
            playerBoardSolver.setNumberPos(4);
            gameBoard.invalidate();
            tv_score.setText("Score: " + gameBoard.getScoreCondition());
            checkCondition();

        } else if (id == R.id.button5) {
            playerBoardSolver.setNumberPos(5);
            gameBoard.invalidate();
            tv_score.setText("Score: " + gameBoard.getScoreCondition());
            checkCondition();

        } else if (id == R.id.button6) {
            playerBoardSolver.setNumberPos(6);
            gameBoard.invalidate();
            tv_score.setText("Score: " + gameBoard.getScoreCondition());
            checkCondition();

        } else if (id == R.id.button7) {
            playerBoardSolver.setNumberPos(7);
            gameBoard.invalidate();
            tv_score.setText("Score: " + gameBoard.getScoreCondition());
            checkCondition();

        } else if (id == R.id.button8) {
            playerBoardSolver.setNumberPos(8);
            gameBoard.invalidate();
            tv_score.setText("Score: " + gameBoard.getScoreCondition());
            checkCondition();

        } else if (id == R.id.button9) {
            playerBoardSolver.setNumberPos(9);
            gameBoard.invalidate();
            tv_score.setText("Score: " + gameBoard.getScoreCondition());
            checkCondition();

        } else if (id == R.id.solveButton) {
            resetGame();
        }
    }

    public void resetGame() {
        gameBoardSolver.resetBoard();
        playerBoardSolver.resetBoard();

        gameBoardSolver.getEmptyBoxIndexes();
        tv_score.setText("Score: " + gameBoard.getScoreCondition());

        gameBoardSolver.addNumbersRandomly(36);
        SolveBoardThread solveBoardThread = new SolveBoardThread();
        new Thread(solveBoardThread).start();
        gameBoard.invalidate();

        btn1.setEnabled(true);
        btn2.setEnabled(true);
        btn3.setEnabled(true);
        btn4.setEnabled(true);
        btn5.setEnabled(true);
        btn6.setEnabled(true);
        btn7.setEnabled(true);
        btn8.setEnabled(true);
        btn9.setEnabled(true);
    }

    public void checkCondition() {
        if(gameBoard.checkCondition()) {
            Log.i(TAG, "WON");

            int score_local = gameBoard.getScoreCondition();

            // game_id 3 = sudoku
            dbHelper.saveScore(username, 3, score_local);

            Toast.makeText(getApplicationContext(), "You have won!", Toast.LENGTH_LONG).show();

            btn1.setEnabled(false);
            btn2.setEnabled(false);
            btn3.setEnabled(false);
            btn4.setEnabled(false);
            btn5.setEnabled(false);
            btn6.setEnabled(false);
            btn7.setEnabled(false);
            btn8.setEnabled(false);
            btn9.setEnabled(false);

            // Show game over dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(SudokuActivity.this);
            builder.setMessage("Your Score: " + gameBoard.getScoreCondition());
            builder.setTitle("You have won!");
            builder.setCancelable(false);
            builder.setNegativeButton("Menu", (dialog, which) -> finish());
            builder.setPositiveButton("Start again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Restart the game / re-init data
                    resetGame();
                }
            });
            builder.setNeutralButton("High Scores", (dialog, which) -> {
                Intent intent = new Intent(SudokuActivity.this, ScoreActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("gameId", 3); // sudoku game ID
                startActivity(intent);
            });
            builder.show();
        } else {
            Log.i(TAG, "NOT WON");
        }
    }

    class SolveBoardThread implements Runnable {
        @Override
        public void run () {
            gameBoardSolver.solve(gameBoard);
            gameBoard.generateRandomCells(8);
        }
    }

}