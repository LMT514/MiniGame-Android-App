package com.example.sehs4542group3.brickbreaker;

public class Brick {

    private boolean isVisable;
    public int row, column, width, height;

    public Brick (int row, int column, int width, int height) {
        isVisable = true;
        this.row = row;
        this.column = column;
        this.width = width;
        this.height = height;
    }

    public void setInvisable() {
        this.isVisable = false;
    }

    public boolean getVisability() {
        return isVisable;
    }
}
