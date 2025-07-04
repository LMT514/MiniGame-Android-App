package com.example.sehs4542group3.sudoku;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sehs4542group3.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SudokuBoard extends View {
    private final int boardColor;
    private final int cellFillColor;
    private final int cellsHighlightColor;

    private final int letterColor;
    private final int letterColorSolve;
    private final int letterColorIncorrect;

    private final Paint boardColorPrint = new Paint();
    private final Paint cellFillColorPrint = new Paint();
    private final Paint cellsHighlightColorPrint = new Paint();
    private final Paint letterPrint = new Paint();
    private final Rect letterPaintBounds = new Rect();
    private int cellSize;

    private final Solver solver = new Solver();
    private final PlayerSolver player = new PlayerSolver();

    public SudokuBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SudokuBoard,
                0, 0);

        try {
            boardColor = a.getInteger(R.styleable.SudokuBoard_boardColor, 0);
            cellFillColor = a.getInteger(R.styleable.SudokuBoard_cellFillColor, 0);
            cellsHighlightColor = a.getInteger(R.styleable.SudokuBoard_cellsHighlightColor, 0);
            letterColor = a.getInteger(R.styleable.SudokuBoard_letterColor, 0);
            letterColorSolve = a.getColor(R.styleable.SudokuBoard_letterColorSolve, 0);
            letterColorIncorrect = a.getColor(R.styleable.SudokuBoard_letterColorIncorrect, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);

        int dimension = Math.min(this.getMeasuredWidth(), this.getMeasuredHeight());
        cellSize = dimension / 9;

        setMeasuredDimension(dimension, dimension);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        boardColorPrint.setStyle(Paint.Style.STROKE);
        boardColorPrint.setStrokeWidth(8);
        boardColorPrint.setColor(boardColor);
        boardColorPrint.setAntiAlias(true);

        cellFillColorPrint.setStyle(Paint.Style.FILL);
        cellFillColorPrint.setAntiAlias(true);
        cellFillColorPrint.setColor(cellFillColor);

        cellsHighlightColorPrint.setStyle(Paint.Style.FILL);
        cellsHighlightColorPrint.setAntiAlias(true);
        cellsHighlightColorPrint.setColor(cellsHighlightColor);

        letterPrint.setStyle(Paint.Style.FILL);
        letterPrint.setAntiAlias(true);
        letterPrint.setColor(letterColor);

        colorCell(canvas, player.getSelectedRow(), player.getSelectedColumn());
        canvas.drawRect(0, 0, getWidth(), getHeight(), boardColorPrint);
        drawBoard(canvas);
        drawNumbers(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent (MotionEvent event) {
        boolean isValid;

        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            player.setSelectedRow((int) Math.ceil(y/cellSize));
            player.setSelectedColumn((int) Math.ceil(x/cellSize));
            isValid = true;
        } else {
            isValid = false;
        }

        return isValid;
    }

    private void drawNumbers(Canvas canvas) {
        letterPrint.setTextSize((float) cellSize / 2);

        for (int r=0; r<9; r++) {
            for (int c=0; c<9; c++) {
                if (player.getBoard() [r][c] != 0) {
                    String text = Integer.toString(player.getBoard()[r][c]);
                    float width, height;

                    letterPrint.getTextBounds(text, 0, text.length(), letterPaintBounds);
                    width = letterPrint.measureText(text);
                    height = letterPaintBounds.height();

                    if(player.getBoard()[r][c] == solver.getBoard()[r][c]) {
                        letterPrint.setColor(letterColorSolve);
                        canvas.drawText(text, (c * cellSize) + ((cellSize - width) / 2),
                                (r * cellSize + cellSize) - ((cellSize - height) / 2),
                                letterPrint);
                    } else {
                        letterPrint.setColor(letterColorIncorrect);
                        canvas.drawText(text, (c * cellSize) + ((cellSize - width) / 2),
                                (r * cellSize + cellSize) - ((cellSize - height) / 2),
                                letterPrint);
                    }

                }
            }
        }
    }

    private void colorCell(Canvas canvas, int r, int c) {
        if (player.getSelectedRow() != -1 && player.getSelectedColumn() != -1) {
            canvas.drawRect( (c-1)*cellSize , 0, c*cellSize, cellSize*9, cellFillColorPrint );

            canvas.drawRect( 0 , (r-1) * cellSize, cellSize * 9, r * cellSize, cellFillColorPrint );

            canvas.drawRect( (c-1)*cellSize , (r-1) * cellSize, c * cellSize, r * cellSize, cellsHighlightColorPrint );
        }

        invalidate();
    }

    private void drawThickLine() {
        boardColorPrint.setStyle(Paint.Style.STROKE);
        boardColorPrint.setStrokeWidth(8);
        boardColorPrint.setColor(boardColor);
    }

    private void drawThinLine() {
        boardColorPrint.setStyle(Paint.Style.STROKE);
        boardColorPrint.setStrokeWidth(4);
        boardColorPrint.setColor(boardColor);
    }

    private void drawBoard(Canvas canvas) {
        for(int c = 0; c < 10; c++) {
            if(c % 3 == 0) {
                drawThickLine();
            } else {
                drawThinLine();
            }
            canvas.drawLine(cellSize * c, 0, cellSize * c, getWidth(), boardColorPrint);
        }

        for(int r = 0; r < 10; r++) {
            if(r % 3 == 0) {
                drawThickLine();
            } else {
                drawThinLine();
            }
            canvas.drawLine(0, cellSize * r, getWidth(), cellSize * r, boardColorPrint);
        }

    }

    public Solver getSolver() {
        return this.solver;
    }

    public PlayerSolver getPlayer() {
        return this.player;
    }

    public void generateRandomCells(int difficulty) {
        Random rand = new Random();

        for (int r = 0; r < 9; r++) {
            Set<Integer> selectedCols = new HashSet<>();

            for (int i = 0; i < difficulty; i++) {
                int col;
                do {
                    col = rand.nextInt(9);
                } while (selectedCols.contains(col)); // Keep generating until a new column is found

                selectedCols.add(col); // Add the selected column to the set
                player.getBoard()[r][col] = solver.getBoard()[r][col];
            }
        }
        player.setPresetBox(player.getBoard());
    }

    public int getScoreCondition() {
        int score = 0;

        for (int r=0; r<9;r++) {
            for (int c=0; c<9;c++) {
                if (player.getBoard()[r][c] == solver.getBoard()[r][c] && player.getBoard()[r][c] != player.getPresetBox()[r][c]) {
                    score += 10;
                }
            }
        }
        return score;
    }

    public boolean checkCondition() {
        return Arrays.deepEquals(player.getBoard(), solver.getBoard());
    }

}
