package com.example.sehs4542group3.tetris.views;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;

//import com.example.tetris.MyDao;
import androidx.appcompat.app.AlertDialog;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.ScoreActivity;
import com.example.sehs4542group3.tetris.TetrisActivity;
import com.example.sehs4542group3.tetris.presenters.GameStatus;
import com.example.sehs4542group3.tetris.presenters.GameView;
import com.example.sehs4542group3.tetris.presenters.Point;

class GameViewImpl implements GameView {
    private DatabaseHelper dbHelper;
    private String username;

    private final GameFrame mGameFrame;
    private final TextView mGameScoreText;
    private final TextView mGameStatusText;
    private final Button mGameCtlBtn;
    private int local_score = 0;

    GameViewImpl(Context context, GameFrame gameFrame, TextView gameScoreText, TextView gameStatusText, Button gameCtlBtn, DatabaseHelper dbHelper, String username) {
        this.dbHelper = dbHelper;
        this.username = username;
        mGameFrame = gameFrame;
        mGameScoreText = gameScoreText;
        mGameStatusText = gameStatusText;
        mGameCtlBtn = gameCtlBtn;
    }

    @Override
    public void init(int gameSize) {
        mGameFrame.init(gameSize);
    }

    @Override
    public void draw(Point[][] points) {
        mGameFrame.setPoints(points);
        mGameFrame.invalidate();
    }

    @Override
    public void setScore(int score) {

        mGameScoreText.setText("Score: " + score);
        local_score = score;
    }

    @Override
    public void setStatus(GameStatus status) {
        mGameStatusText.setText(status.getValue());
        mGameStatusText.setVisibility(status == GameStatus.PLAYING ? View.INVISIBLE : View.VISIBLE);
        mGameCtlBtn.setText(status == GameStatus.PLAYING ? "Pause" : "Start");

        if (status == GameStatus.OVER) {
            Log.i(TAG, "Game Over");

            Log.i(TAG, local_score+"");

            dbHelper.saveScore(username, 5, local_score);

            // Assuming game_id = 5 for Tetris
        }
    }
}