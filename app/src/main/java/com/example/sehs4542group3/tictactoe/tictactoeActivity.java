package com.example.sehs4542group3.tictactoe;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.HomeActivity;
import com.example.sehs4542group3.R;
import com.example.sehs4542group3.ScoreActivity;
import com.example.sehs4542group3.snakegame.SnakeGameActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class tictactoeActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String username;
    private final List<int[]> combinationsList = new ArrayList<>();
    private int[] boxPositions = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    // Human player is always 1, Computer is always 2
    private final int HUMAN_PLAYER = 1;
    private final int COMPUTER_PLAYER = 2;

    private int totalSelectedBoxes = 0; // Start at 0 to allow for all 9 boxes

    private LinearLayout playerOneLayout, playerTwoLayout;
    private TextView playerOneName, playerTwoName;
    private ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tictactoe);
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        playerOneName = findViewById(R.id.PlayerOneName);
        playerTwoName = findViewById(R.id.PlayerTwoName);

        playerOneLayout = findViewById(R.id.PlayerOneLayout);
        playerTwoLayout = findViewById(R.id.PlayerTwoLayout);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        image6 = findViewById(R.id.image6);
        image7 = findViewById(R.id.image7);
        image8 = findViewById(R.id.image8);
        image9 = findViewById(R.id.image9);

        combinationsList.add(new int[]{0, 1, 2});
        combinationsList.add(new int[]{3, 4, 5});
        combinationsList.add(new int[]{6, 7, 8});
        combinationsList.add(new int[]{0, 3, 6});
        combinationsList.add(new int[]{1, 4, 7});
        combinationsList.add(new int[]{2, 5, 8});
        combinationsList.add(new int[]{2, 4, 6});
        combinationsList.add(new int[]{0, 4, 8});

        final String getPlayerOneName = getIntent().getStringExtra("PlayerOne");
        final String getPlayerTwoName = getIntent().getStringExtra("PlayerTwo");

        playerOneName.setText(getPlayerOneName);
        playerTwoName.setText(getPlayerTwoName);

        // Set OnClickListeners for images 1-9
        setupImageViewClickListener(image1, 0);
        setupImageViewClickListener(image2, 1);
        setupImageViewClickListener(image3, 2);
        setupImageViewClickListener(image4, 3);
        setupImageViewClickListener(image5, 4);
        setupImageViewClickListener(image6, 5);
        setupImageViewClickListener(image7, 6);
        setupImageViewClickListener(image8, 7);
        setupImageViewClickListener(image9, 8);

    }

    private void setupImageViewClickListener(ImageView imageView, int boxPosition) {
        imageView.setOnClickListener(v -> {
            if (isBoxSelectable(boxPosition)) {
                performAction((ImageView) v, boxPosition, HUMAN_PLAYER);
                disableImageClicks();
                if (totalSelectedBoxes < 9 && !checkPlayerWin(HUMAN_PLAYER)) {
                    // Delay the call to CompTurn() to allow the UI to update
                    new Handler(Looper.getMainLooper()).postDelayed(this::CompTurn, 500); // Delay for 500 milliseconds
                }
            }
        });
    }


    private void performAction(ImageView imageView, int selectedBoxPosition, int player) {
        if (boxPositions[selectedBoxPosition] == 0) {
            boxPositions[selectedBoxPosition] = player; // Set the player who made the move

            if (player == HUMAN_PLAYER) {
                imageView.setImageResource(R.drawable.cross_icon_id4);
            } else {
                imageView.setImageResource(R.drawable.circle_icon_id4);
            }

            totalSelectedBoxes++;

            if (checkPlayerWin(player)) { // Check if current player wins
                String message = (player == HUMAN_PLAYER ? playerOneName.getText().toString() : playerTwoName.getText().toString()) + " has won the match";

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(message)
                        .setTitle("Game Over")
                        .setCancelable(false)
                        .setNegativeButton("Menu", (dialog, which) -> {
                            Intent intent = new Intent(this, HomeActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("gameId", 4);
                            startActivity(intent);
                            finish();
                        })
                        .setPositiveButton("Start again", (dialog, which) -> startMatch())
                        .setNeutralButton("High Scores", (dialog, which) -> {
                            Intent intent = new Intent(this, ScoreActivity.class);
                            intent.putExtra("username", username); // Replace with actual username if available
                            intent.putExtra("gameId", 4); // Tic-Tac-Toe game ID
                            startActivity(intent);
                            finish();
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                if (player == HUMAN_PLAYER) {
                    // Existing score update logic
                    dbHelper.saveScore(username, 4, 3);
                }
            } else if (totalSelectedBoxes == 9) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("It's a draw!")
                        .setTitle("Game Over")
                        .setCancelable(false)
                        .setNegativeButton("Menu", (dialog, which) -> {
                            Intent intent = new Intent(this, HomeActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("gameId", 4);
                            startActivity(intent);
                            finish();
                        })
                        .setPositiveButton("Start again", (dialog, which) -> startMatch())
                        .setNeutralButton("High Scores", (dialog, which) -> {
                            Intent intent = new Intent(this, ScoreActivity.class);
                            intent.putExtra("username", username); // Replace with actual username if available
                            intent.putExtra("gameId", 4);
                            startActivity(intent);
                            finish();
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private void CompTurn() {
        if (totalSelectedBoxes == 9 || checkPlayerWin(HUMAN_PLAYER)) return; // Don't move if game is over

        Random random = new Random();
        int randomNumber;

        // Try at most 10 times to find an available box
        int attempts = 0;
        do {
            randomNumber = random.nextInt(9);
            attempts++;
            if (attempts > 10) {
                Log.w(TAG, "CompTurn: Could not find an available box after 10 attempts.");
                return; // Exit if no box is available after 10 attempts
            }
        } while (!isBoxSelectable(randomNumber));

        ImageView randomImageView = getImageViewForPosition(randomNumber);
        if (randomImageView != null) {
            performAction(randomImageView, randomNumber, COMPUTER_PLAYER);
        }
        enableImageClicks();
    }

    private ImageView getImageViewForPosition(int position) {
        switch (position) {
            case 0: return image1;
            case 1: return image2;
            case 2: return image3;
            case 3: return image4;
            case 4: return image5;
            case 5: return image6;
            case 6: return image7;
            case 7: return image8;
            case 8: return image9;
            default: return null;
        }
    }

    private boolean checkPlayerWin(int player) {
        for (int[] combination : combinationsList) {
            if (boxPositions[combination[0]] == player &&
                    boxPositions[combination[1]] == player &&
                    boxPositions[combination[2]] == player) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoxSelectable(int boxPosition) {
        return boxPositions[boxPosition] == 0;
    }

    private void disableImageClicks() {
        image1.setClickable(false);
        image2.setClickable(false);
        image3.setClickable(false);
        image4.setClickable(false);
        image5.setClickable(false);
        image6.setClickable(false);
        image7.setClickable(false);
        image8.setClickable(false);
        image9.setClickable(false);
    }

    private void enableImageClicks() {
        image1.setClickable(true);
        image2.setClickable(true);
        image3.setClickable(true);
        image4.setClickable(true);
        image5.setClickable(true);
        image6.setClickable(true);
        image7.setClickable(true);
        image8.setClickable(true);
        image9.setClickable(true);
    }

    public void startMatch() {
        boxPositions = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};

        totalSelectedBoxes = 0; // Start at 0 to allow for all 9 boxes

        image1.setImageResource(R.drawable.transparent_black_id4);
        image2.setImageResource(R.drawable.transparent_black_id4);
        image3.setImageResource(R.drawable.transparent_black_id4);
        image4.setImageResource(R.drawable.transparent_black_id4);
        image5.setImageResource(R.drawable.transparent_black_id4);
        image6.setImageResource(R.drawable.transparent_black_id4);
        image7.setImageResource(R.drawable.transparent_black_id4);
        image8.setImageResource(R.drawable.transparent_black_id4);
        image9.setImageResource(R.drawable.transparent_black_id4);
        enableImageClicks();
    }

}