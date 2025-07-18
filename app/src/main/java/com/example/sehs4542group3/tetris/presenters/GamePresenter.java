package com.example.sehs4542group3.tetris.presenters;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.sehs4542group3.tetris.presenters.GameModel;
import com.example.sehs4542group3.tetris.presenters.GameStatus;
import com.example.sehs4542group3.tetris.presenters.GameTurn;
import com.example.sehs4542group3.tetris.presenters.GameView;

public class GamePresenter {
    private GameModel mGameModel;
    private GameView mGameView;
    private GameStatus mStatus;

    public void setGameModel(GameModel gameModel) {
        mGameModel = gameModel;
    }

    public void setGameView(GameView gameView) {
        mGameView = gameView;
    }

    public void init() {
        mGameModel.init();
        mGameView.init(mGameModel.getGameSize());
        mGameModel.setGameOverListener(() -> setStatus(GameStatus.OVER));
        mGameModel.setScoreUpdatedListener(mGameView::setScore);
        setStatus(GameStatus.START);
    }

    public void turn(GameTurn turn) {
        mGameModel.turn(turn);
    }

    public void changeStatus() {
        if (mStatus == GameStatus.PLAYING) {
            pauseGame();
        } else {
            startGame();
        }
    }

    private void pauseGame() {
        setStatus(GameStatus.PAUSED);
        mGameModel.pauseGame();
    }

    private void startGame() {
        setStatus(GameStatus.PLAYING);
        mGameModel.startGame(mGameView::draw);
    }

    private void setStatus(GameStatus status) {
        if (mStatus == GameStatus.OVER || status == GameStatus.START) {
            mGameModel.newGame();
        }
        mStatus = status;
        mGameView.setStatus(status);

    }
}
