package com.example.sehs4542group3.tetris.views;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.tetris.presenters.GameView;

public class GameViewFactory {
    private GameViewFactory() {
    }

    public static GameView newGameView(Context context, GameFrame gameFrame, TextView gameScoreText, TextView gameStatusText, Button gameCtlBtn, DatabaseHelper dbHelper, String username) {
        return new GameViewImpl(context, gameFrame, gameScoreText, gameStatusText, gameCtlBtn, dbHelper, username);
    }
}
