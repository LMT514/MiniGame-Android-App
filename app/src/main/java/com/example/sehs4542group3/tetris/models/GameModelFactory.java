package com.example.sehs4542group3.tetris.models;

import com.example.sehs4542group3.tetris.presenters.GameModel;
import com.example.sehs4542group3.tetris.views.GameViewFactory;

public class GameModelFactory {
    private GameModelFactory() {

    }

    public static GameModel newGameModel (GameType type) {
        switch (type) {
            case TETRIS:
                return new TetrisGameModel();
            default:
                return null;
        }
    }
}
