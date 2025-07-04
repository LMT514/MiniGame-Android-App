package com.example.sehs4542group3.sudoku;

import static android.content.ContentValues.TAG;

import android.util.Log;
import java.util.Arrays;

public class PlayerSolver {

    int[] [] board;

    int [] [] PresetBoxIndex;

    int selected_row;
    int selected_column;

    PlayerSolver() {
        selected_row = -1;
        selected_column = -1;

        board = new int[9][9];
        PresetBoxIndex = new int[9][9];
        for (int r=0; r<9;r++) {
            for (int c=0; c<9;c++) {
                board[r][c] = 0;
            }
        }


    }

    public void setNumberPos(int num) {
        Log.i(TAG, "setNumberPos called");
        if (this.selected_row != -1 && this.selected_column != -1) {
            if (this.board[this.selected_row-1][this.selected_column-1] == num) {
                this.board[this.selected_row-1][this.selected_column-1] = 0;
            } else {
                this.board[this.selected_row-1][this.selected_column-1] = num;
            }
        }
    }

    public void resetBoard() {
        for (int r=0; r<9;r++) {
            for (int c=0; c<9;c++) {
                board[r][c] = 0;
            }
        }
        PresetBoxIndex = new int[9][9];
    }

    public int [][] getBoard() {
        return this.board;
    }

    public int getSelectedRow() {
        return selected_row;
    }

    public int getSelectedColumn() {
        return selected_column;
    }

    public void setSelectedRow(int r) {
        selected_row = r;
    }

    public void setSelectedColumn(int c) {
        selected_column = c;
    }

    public void setPresetBox(int[][] arrayClone) {
        if (arrayClone == null) {
            PresetBoxIndex = null;
            return;
        }

        PresetBoxIndex = new int[arrayClone.length][];
        for (int i = 0; i < arrayClone.length; i++) {
            PresetBoxIndex[i] = Arrays.copyOf(arrayClone[i], arrayClone[i].length);
        }
    }

    public int [][] getPresetBox() {
        return PresetBoxIndex;
    }

}
