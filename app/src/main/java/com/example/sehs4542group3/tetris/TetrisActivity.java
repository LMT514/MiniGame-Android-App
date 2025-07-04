package com.example.sehs4542group3.tetris;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;
import com.example.sehs4542group3.ScoreActivity;
import com.example.sehs4542group3.tetris.models.GameModelFactory;
import com.example.sehs4542group3.tetris.models.GameType;
import com.example.sehs4542group3.tetris.presenters.GamePresenter;
import com.example.sehs4542group3.tetris.presenters.GameTurn;
import com.example.sehs4542group3.tetris.views.GameFrame;
import com.example.sehs4542group3.tetris.views.GameViewFactory;

public class TetrisActivity extends AppCompatActivity implements View.OnClickListener{

    private DatabaseHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tetris);

        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");

        GameFrame gameFrame = findViewById(R.id.game_container);
        TextView gameScoreText = findViewById(R.id.game_score);
        TextView gameStatusText = findViewById(R.id.game_status);
        Button gameCtlBtn = findViewById(R.id.game_ctl_btn);
        ImageButton gomenu = findViewById(R.id.Menu);

        GamePresenter gamePresenter = new GamePresenter();
        gamePresenter.setGameModel(GameModelFactory.newGameModel(GameType.TETRIS));
        gamePresenter.setGameView(GameViewFactory.newGameView(this, gameFrame, gameScoreText, gameStatusText, gameCtlBtn, dbHelper, username));

        Button upBtn = findViewById(R.id.up_btn);
        Button downBtn = findViewById(R.id.down_btn);
        Button leftBtn = findViewById(R.id.left_btn);
        Button rightBtn = findViewById(R.id.right_btn);
        Button fireBtn = findViewById(R.id.fire_btn);

        upBtn.setOnClickListener(v -> gamePresenter.turn(GameTurn.UP));
        downBtn.setOnClickListener(v -> gamePresenter.turn(GameTurn.DOWN));
        leftBtn.setOnClickListener(v -> gamePresenter.turn(GameTurn.LEFT));
        rightBtn.setOnClickListener(v -> gamePresenter.turn(GameTurn.RIGHT));
        fireBtn.setOnClickListener(v -> gamePresenter.turn(GameTurn.FIRE));

        gameCtlBtn.setOnClickListener(v -> gamePresenter.changeStatus());

        gomenu.setOnClickListener(v -> finish());

        ImageButton goscore = findViewById(R.id.HighTetrisScore);
        goscore.setOnClickListener(this);

        gamePresenter.init();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.HighTetrisScore) {
            Intent i = new Intent(TetrisActivity.this, ScoreActivity.class);
            i.putExtra("username", username);
            i.putExtra("gameId", 5); // snake game ID
            startActivity(i);
        }
    }
}