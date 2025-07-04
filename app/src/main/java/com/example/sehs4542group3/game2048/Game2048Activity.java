package com.example.sehs4542group3.game2048;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;
import com.example.sehs4542group3.ScoreActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game2048Activity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String username;
    private int[][] grid = new int[4][4];
    private GridLayout gridLayout;
    private TextView scoreTextView, highScoreTextView;
    private int score = 0;
    private int highScore = 0;
    private boolean hasWon = false;
    private SharedPreferences prefs;
    private static final String KEY_CURRENT_SCORE = "CurrentScore";
    private static final String KEY_CURRENT_HAS_WON = "CurrentHasWon";
    private static final String KEY_CURRENT_GRID = "CurrentGrid";
    private static final String PREFS_NAME = "Game2048Prefs";
    private static final String KEY_HIGH_SCORE = "HighScore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        highScore = prefs.getInt(KEY_HIGH_SCORE, 0);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        initializeViews();

        if (savedInstanceState != null) {
            // Restore from instance state
            score = savedInstanceState.getInt("score");
            highScore = savedInstanceState.getInt("highScore");
            hasWon = savedInstanceState.getBoolean("hasWon");
            int[] flatGrid = savedInstanceState.getIntArray("grid");
            if (flatGrid != null && flatGrid.length == 16) {
                for (int i = 0; i < 16; i++) {
                    grid[i / 4][i % 4] = flatGrid[i];
                }
            }
        } else {
            // Check for saved game in SharedPreferences
            int savedScore = prefs.getInt(KEY_CURRENT_SCORE, -1);
            if (savedScore != -1) {
                score = savedScore;
                hasWon = prefs.getBoolean(KEY_CURRENT_HAS_WON, false);
                String gridString = prefs.getString(KEY_CURRENT_GRID, "");
                if (!gridString.isEmpty()) {
                    String[] parts = gridString.split(",");
                    if (parts.length == 16) {
                        for (int i = 0; i < 16; i++) {
                            try {
                                grid[i / 4][i % 4] = Integer.parseInt(parts[i]);
                            } catch (NumberFormatException e) {
                                grid[i / 4][i % 4] = 0;
                            }
                        }
                    }
                }
            } else {
                resetGame();
                return;
            }
        }

        updateGridDisplay();
    }

    private void initializeViews() {
        gridLayout = findViewById(R.id.gridLayout);
        scoreTextView = findViewById(R.id.scoreTextView);
        highScoreTextView = findViewById(R.id.highScoreTextView);

        ImageButton upButton = findViewById(R.id.upButton);
        ImageButton downButton = findViewById(R.id.downButton);
        ImageButton leftButton = findViewById(R.id.leftButton);
        ImageButton rightButton = findViewById(R.id.rightButton);
        Button resetButton = findViewById(R.id.resetButton);

        initializeGrid();

        View.OnClickListener buttonListener = v -> {
            if (v.getId() == R.id.resetButton) {
                resetGame();
            } else if (!isGameOver() && !hasWon) {
                boolean moved = false;
                if (v == upButton) moved = moveUp();
                else if (v == downButton) moved = moveDown();
                else if (v == leftButton) moved = moveLeft();
                else if (v == rightButton) moved = moveRight();

                if (moved) {
                    addNewTileWithAnimation();
                    updateGridDisplay();
                    checkVictory();
                    updateHighScore();
                }
                if (isGameOver() && !hasWon) {
                    Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show();
                    gameOver(score); // Call the gameOver with current score
                }
                if (hasWon) {
                    Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show();
                    gameOver(score); // Call the gameOver with current score
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove(KEY_CURRENT_SCORE);
                    editor.remove(KEY_CURRENT_HAS_WON);
                    editor.remove(KEY_CURRENT_GRID);
                    editor.apply();
                }
            }
        };

        upButton.setOnClickListener(buttonListener);
        downButton.setOnClickListener(buttonListener);
        leftButton.setOnClickListener(buttonListener);
        rightButton.setOnClickListener(buttonListener);
        resetButton.setOnClickListener(buttonListener);
    }

    private void initializeGrid() {
        gridLayout.removeAllViews();

        // Convert 4dp margin to pixels
        float density = getResources().getDisplayMetrics().density;
        final int marginPx = (int) (4 * density);

        // Create initial cells with margins
        for (int i = 0; i < 16; i++) {
            TextView cell = new TextView(this);
            cell.setGravity(Gravity.CENTER);
            cell.setTextSize(24);
            cell.setBackgroundColor(Color.parseColor("#CDC1B4"));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.setMargins(marginPx, marginPx, marginPx, marginPx);

            int row = i / 4;
            int col = i % 4;
            params.rowSpec = GridLayout.spec(row);
            params.columnSpec = GridLayout.spec(col);

            cell.setLayoutParams(params);
            gridLayout.addView(cell);
        }

        gridLayout.setBackgroundColor(Color.parseColor("#BBADA0"));

        gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int gridWidth = gridLayout.getWidth();
                int gridHeight = gridLayout.getHeight();
                if (gridWidth <= 0 || gridHeight <= 0) return;

                // Calculate available space after padding and margins
                int gridPaddingH = gridLayout.getPaddingLeft() + gridLayout.getPaddingRight();
                int gridPaddingV = gridLayout.getPaddingTop() + gridLayout.getPaddingBottom();

                int totalHorizontalMargin = 3 * 2 * marginPx; // 3 gaps between 4 columns
                int availableWidth = gridWidth - gridPaddingH - totalHorizontalMargin;

                int totalVerticalMargin = 3 * 2 * marginPx; // 3 gaps between 4 rows
                int availableHeight = gridHeight - gridPaddingV - totalVerticalMargin;

                int cellSize = Math.min(availableWidth / 4, availableHeight / 4);

                for (int i = 0; i < gridLayout.getChildCount(); i++) {
                    View cell = gridLayout.getChildAt(i);
                    GridLayout.LayoutParams params = (GridLayout.LayoutParams) cell.getLayoutParams();
                    params.width = cellSize;
                    params.height = cellSize;
                    cell.setLayoutParams(params);
                }

                gridLayout.requestLayout();
                updateGridDisplay();
            }
        });
    }

    private void updateGridDisplay() {
        for (int i = 0; i < 16; i++) {
            TextView cell = (TextView) gridLayout.getChildAt(i);
            int value = grid[i / 4][i % 4];
            cell.setText(value == 0 ? "" : String.valueOf(value));
            cell.setBackgroundColor(getColorForValue(value));
            cell.setTextColor(value > 4 ? Color.WHITE : Color.BLACK);
        }
        scoreTextView.setText("Score: " + score);
        highScoreTextView.setText("High Score: " + highScore);
    }

    private int getColorForValue(int value) {
        switch (value) {
            case 2:    return Color.parseColor("#EEE4DA");
            case 4:    return Color.parseColor("#EDE0C8");
            case 8:    return Color.parseColor("#F2B179");
            case 16:   return Color.parseColor("#F59563");
            case 32:   return Color.parseColor("#F67C5F");
            case 64:   return Color.parseColor("#F65E3B");
            case 128:  return Color.parseColor("#EDCF72");
            case 256:  return Color.parseColor("#EDCC61");
            case 512:  return Color.parseColor("#EDC850");
            case 1024: return Color.parseColor("#EDC53F");
            case 2048: return Color.parseColor("#EDC22E");
            default:   return Color.LTGRAY;
        }
    }

    private void addNewTileWithAnimation() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (grid[i][j] == 0) emptyCells.add(new int[]{i, j});
            }
        }

        if (!emptyCells.isEmpty()) {
            int[] cell = emptyCells.get(new Random().nextInt(emptyCells.size()));
            grid[cell[0]][cell[1]] = new Random().nextFloat() < 0.9 ? 2 : 4;
            animateTileAppearance(gridLayout.getChildAt(cell[0] * 4 + cell[1]));
        }
    }

    private void animateTileAppearance(View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.5f, 1f);
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            view.setScaleX(value);
            view.setScaleY(value);
        });
        animator.start();
    }

    // Movement methods
    private boolean moveLeft() {
        boolean moved = false;
        for (int row = 0; row < 4; row++) {
            int lastMergePosition = -1;
            for (int col = 1; col < 4; col++) {
                if (grid[row][col] == 0) continue;

                int currentCol = col;
                while (currentCol > 0 && grid[row][currentCol - 1] == 0) {
                    grid[row][currentCol - 1] = grid[row][currentCol];
                    grid[row][currentCol] = 0;
                    currentCol--;
                    moved = true;
                }

                if (currentCol > 0 && grid[row][currentCol - 1] == grid[row][currentCol] &&
                        lastMergePosition != currentCol - 1) {
                    grid[row][currentCol - 1] *= 2;
                    score += grid[row][currentCol - 1];
                    grid[row][currentCol] = 0;
                    lastMergePosition = currentCol - 1;
                    moved = true;
                    animateMergeEffect(gridLayout.getChildAt(row * 4 + currentCol - 1));
                }
            }
        }
        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;
        for (int row = 0; row < 4; row++) {
            int lastMergePosition = 4;
            for (int col = 2; col >= 0; col--) {
                if (grid[row][col] == 0) continue;

                int currentCol = col;
                while (currentCol < 3 && grid[row][currentCol + 1] == 0) {
                    grid[row][currentCol + 1] = grid[row][currentCol];
                    grid[row][currentCol] = 0;
                    currentCol++;
                    moved = true;
                }

                if (currentCol < 3 && grid[row][currentCol + 1] == grid[row][currentCol] &&
                        lastMergePosition != currentCol + 1) {
                    grid[row][currentCol + 1] *= 2;
                    score += grid[row][currentCol + 1];
                    grid[row][currentCol] = 0;
                    lastMergePosition = currentCol + 1;
                    moved = true;
                    animateMergeEffect(gridLayout.getChildAt(row * 4 + currentCol + 1));
                }
            }
        }
        return moved;
    }

    private boolean moveUp() {
        boolean moved = false;
        for (int col = 0; col < 4; col++) {
            int lastMergePosition = -1;
            for (int row = 1; row < 4; row++) {
                if (grid[row][col] == 0) continue;

                int currentRow = row;
                while (currentRow > 0 && grid[currentRow - 1][col] == 0) {
                    grid[currentRow - 1][col] = grid[currentRow][col];
                    grid[currentRow][col] = 0;
                    currentRow--;
                    moved = true;
                }

                if (currentRow > 0 && grid[currentRow - 1][col] == grid[currentRow][col] &&
                        lastMergePosition != currentRow - 1) {
                    grid[currentRow - 1][col] *= 2;
                    score += grid[currentRow - 1][col];
                    grid[currentRow][col] = 0;
                    lastMergePosition = currentRow - 1;
                    moved = true;
                    animateMergeEffect(gridLayout.getChildAt((currentRow - 1) * 4 + col));
                }
            }
        }
        return moved;
    }

    private boolean moveDown() {
        boolean moved = false;
        for (int col = 0; col < 4; col++) {
            int lastMergePosition = 4;
            for (int row = 2; row >= 0; row--) {
                if (grid[row][col] == 0) continue;

                int currentRow = row;
                while (currentRow < 3 && grid[currentRow + 1][col] == 0) {
                    grid[currentRow + 1][col] = grid[currentRow][col];
                    grid[currentRow][col] = 0;
                    currentRow++;
                    moved = true;
                }

                if (currentRow < 3 && grid[currentRow + 1][col] == grid[currentRow][col] &&
                        lastMergePosition != currentRow + 1) {
                    grid[currentRow + 1][col] *= 2;
                    score += grid[currentRow + 1][col];
                    grid[currentRow][col] = 0;
                    lastMergePosition = currentRow + 1;
                    moved = true;
                    animateMergeEffect(gridLayout.getChildAt((currentRow + 1) * 4 + col));
                }
            }
        }
        return moved;
    }

    private void animateMergeEffect(View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 1.2f, 1f);
        animator.setDuration(150);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            view.setScaleX(value);
            view.setScaleY(value);
        });
        animator.start();
    }

    private boolean isGameOver() {
        // Check for empty cells
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (grid[i][j] == 0) return false;
                if (i < 3 && grid[i][j] == grid[i + 1][j]) return false;
                if (j < 3 && grid[i][j] == grid[i][j + 1]) return false;
            }
        }
        return true;
    }

    private void checkVictory() {
        for (int[] row : grid) {
            for (int value : row) {
                if (value == 64) {
                    hasWon = true;
                    Toast.makeText(this, "Congratulations! You reached 2048!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            prefs.edit().putInt(KEY_HIGH_SCORE, highScore).apply();
            highScoreTextView.setText("High Score: " + highScore);
        }
    }

    private void resetGame() {
        grid = new int[4][4];
        score = 0;
        hasWon = false;
        addNewTileWithAnimation();
        addNewTileWithAnimation();
        updateGridDisplay();
        updateHighScore();

        // Clear saved game state
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_CURRENT_SCORE);
        editor.remove(KEY_CURRENT_HAS_WON);
        editor.remove(KEY_CURRENT_GRID);
        editor.apply();
    }

    private void gameOver(int finalScore) {
        // Save score to SQLite
        dbHelper.saveScore(username, 1, finalScore); // 1 = gameId for 2048

        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Score: " + finalScore)
                .setPositiveButton("Retry", (dialog, which) -> resetGame())
                .setNeutralButton("High Scores", (dialog, which) -> {
                    Intent intent = new Intent(Game2048Activity.this, ScoreActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("gameId", 1); // 2048 game ID
                    startActivity(intent);
                })
                .setNegativeButton("Menu", (dialog, which) -> finish())
                .show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("score", score);
        outState.putInt("highScore", highScore);
        outState.putBoolean("hasWon", hasWon);
        int[] flatGrid = new int[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                flatGrid[i * 4 + j] = grid[i][j];
            }
        }
        outState.putIntArray("grid", flatGrid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save current game state to SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CURRENT_SCORE, score);
        editor.putBoolean(KEY_CURRENT_HAS_WON, hasWon);
        StringBuilder gridString = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                gridString.append(grid[i][j]).append(",");
            }
        }
        if (gridString.length() > 0) {
            gridString.deleteCharAt(gridString.length() - 1);
        }
        editor.putString(KEY_CURRENT_GRID, gridString.toString());
        editor.apply();
    }
}