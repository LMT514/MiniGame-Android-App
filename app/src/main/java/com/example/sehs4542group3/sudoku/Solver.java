package com.example.sehs4542group3.sudoku;

import java.util.ArrayList;
import java.util.Random;

public class Solver {

    int[] [] board;
    ArrayList<ArrayList<Object>> emptyBoxIndex;

    int selected_row;
    int selected_column;

    // Board = new int[][] {{ 0, 0, 0, 0, 0, 0, 0, 0, 0},
    //                      { 0, 0, 0, 0, 0, 0, 0, 0, 0},
    //                      { 0, 0, 0, 0, 0, 0, 0, 0, 0},
    //                      { 0, 0, 0, 0, 0, 0, 0, 0, 0},
    //                      { 0, 0, 0, 0, 0, 0, 0, 0, 0},
    //                      { 0, 0, 0, 0, 0, 0, 0, 0, 0},
    //                      { 0, 0, 0, 0, 0, 0, 0, 0, 0},
    //                      { 0, 0, 0, 0, 0, 0, 0, 0, 0},
    //                      { 0, 0, 0, 0, 0, 0, 0, 0, 0}}

    Solver() {
        selected_row = -1;
        selected_column = -1;

        board = new int[9][9];

        for (int r=0; r<9;r++) {
            for (int c=0; c<9;c++) {
                board[r][c] = 0;
            }
        }

        emptyBoxIndex = new ArrayList<>();
    }

    public void getEmptyBoxIndexes() {
        for (int r=0; r<9;r++) {
            for (int c=0; c<9;c++) {
                if (this.board[r][c] == 0) {
                    this.emptyBoxIndex.add(new ArrayList<>());
                    this.emptyBoxIndex.get(this.emptyBoxIndex.size()-1).add(r);
                    this.emptyBoxIndex.get(this.emptyBoxIndex.size()-1).add(c);
                }
            }
        }
    }


    public boolean check(int row, int col) {
        if (this.board[row][col] > 0) {
            for (int i = 0; i < 9; i++) {
                if (this.board[i][col] == this.board[row][col] && row != i) {
                    return false;
                }

                if (this.board[row][i] == this.board[row][col] && col != i) {
                    return false;
                }
            }

            int boxRow = row/3;
            int boxCol = col/3;

            for (int r=boxRow*3; r < boxRow*3 + 3; r++) {
                for (int c=boxCol*3; c < boxCol*3 + 3; c++) {
                    if (this.board[r][c] == this.board[row][col] && row != r && col != c) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


    public boolean solve(SudokuBoard display) {
        int row = -1;
        int col = -1;

        // Find the next empty cell
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (this.board[r][c] == 0) {
                    row = r;
                    col = c;
                    break;
                }
            }
            if (row != -1) break; // Exit outer loop once an empty cell is found
        }

        if (row == -1) return true; // No more empty cells, puzzle is solved

        for (int i = 1; i <= 9; i++) {
            this.board[row][col] = i;
            display.invalidate(); // Optional for visual feedback

            if (check(row, col)) {
                if (solve(display)) {
                    return true; // Recursively solve the rest of the puzzle
                }
            }

            this.board[row][col] = 0; // Reset cell if current number doesn't work
        }

        return false; // No valid number found for this cell
    }

    public void resetBoard() {
        for (int r=0; r<9;r++) {
            for (int c=0; c<9;c++) {
                board[r][c] = 0;
            }
        }

        this.emptyBoxIndex = new ArrayList<>();
    }

    public int [][] getBoard() {
        return this.board;
    }

    public ArrayList<ArrayList<Object>> getEmptyBoxIndex() {
        return this.emptyBoxIndex;
    }

    public void setSelectedRow(int r) {
        selected_row = r;
    }

    public void setSelectedColumn(int c) {
        selected_column = c;
    }


    public void addNumbersRandomly(int maxNumbers) {
        // Initialize an empty board
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                this.board[r][c] = 0;
            }
        }

        addNumbersRandomlyHelper(0, 0, maxNumbers);
    }

    private boolean addNumbersRandomlyHelper(int row, int col, int maxNumbers) {
        if (maxNumbers == 0) return true; // Base case: no more numbers to add

        if (col >= 9) {
            col = 0;
            row++;
        }

        if (row >= 9) return true; // All cells checked

        if (this.board[row][col] != 0) {
            return addNumbersRandomlyHelper(row, col + 1, maxNumbers); // Skip filled cells
        }

        // Generate a random list of numbers to try
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        shuffleArray(numbers); // Shuffle to randomize order

        for (int num : numbers) {
            this.board[row][col] = num;

            if (check(row, col)) { // Check if number is valid
                if (addNumbersRandomlyHelper(row, col + 1, maxNumbers - 1)) {
                    return true; // Recursively add more numbers
                }
            }

            this.board[row][col] = 0; // Reset if number doesn't work
        }

        return false; // No valid number found
    }

    // Helper to shuffle an array
    private void shuffleArray(int[] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

}
