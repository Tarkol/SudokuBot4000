package org.tark.sudoku;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Tarkol on 25/10/2016.
 * This class is used to represent a complete Sudoku puzzle and includes functions to solve it.
 */
public class SudokuPuzzle {
    private int boardSize;
    private int blockSize;
    private SudokuCell[][] board;

    /**
     * Makes an empty board with a given size for the cell blocks.
     * Only makes square blocks.
     * @param blockSize The blocks in the puzzle will be squares of this size.
     */
    public SudokuPuzzle(int blockSize) {
        this.blockSize = blockSize;
        this.boardSize = blockSize * blockSize;
        this.board = new SudokuCell[boardSize][boardSize];
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                this.board[x][y] = new SudokuCell(0, false);
            }
        }
    }

    /**
     * Creates a board from an existing square grid.
     * Blocks are assumed to be square with size equal to the root of the board size.
     * @param board A 2D array with the initial state of the sudoku puzzle.
     */
    //TODO: Can go wrong if the board is not square
    public SudokuPuzzle(int[][] board) {
        this.blockSize = (int)Math.sqrt(board.length);
        this.boardSize = board.length;
        this.board = new SudokuCell[boardSize][boardSize];
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                this.board[x][y] = new SudokuCell(board[x][y], board[x][y] != 0);
            }
        }
    }

    public int getBoardSize() { return boardSize; }

    public int getBlockSize() { return blockSize; }

    public SudokuCell[][] getBoard() { return board; }

    public SudokuCell getCell(int x, int y){
        return board[x][y];
    }

    /**
     * Resets the puzzle back to a blank state.
     */
    public void reset(){
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                board[x][y].reset();
            }
        }
    }

    public boolean cellHasValueConflict(SudokuCell cell, int cellX, int cellY){

        //Check cell row
        for (int x = 0; x < boardSize; x++) {
            SudokuCell cellInBoard = board[x][cellY];
            if (cellInBoard.getDigit() > 0 && cellInBoard.getDigit() == cell.getDigit() && cellInBoard != cell) {
                    return true;
            }
        }

        //Check cell column
        for (int y = 0; y < boardSize; y++) {
            SudokuCell cellInBoard = board[cellX][y];
            if (cellInBoard.getDigit() > 0 && cellInBoard.getDigit() == cell.getDigit() && cellInBoard != cell) {
                return true;
            }
        }

        //Check cell block
        int blockX = cellX / blockSize;
        int blockY = cellY / blockSize;
        for (int x = 0; x < blockSize; x ++){
            for (int y = 0; y < blockSize; y++) {
                int trueX = x + (blockX * blockSize);
                int trueY = y + (blockY * blockSize);
                SudokuCell cellInBoard = board[trueX][trueY];
                if (cellInBoard.getDigit() > 0 && cellInBoard.getDigit() == cell.getDigit() && cellInBoard != cell) {
                    return true;
                }
            }
        }

        return false;
    }

    //region String functions to print the board to command line
    @Override
    public String toString() {
        return getBoardString(false);
    }

    /**
     * Return the sudoku board as a string.
     * @param getInitial Returns the initial board state if true.
     * @return A string containing the sudoku board.
     */
    public String getBoardString(boolean getInitial){
        String s = (getInitial) ? "Initial Sudoku board state:\n" : "Current Sudoku board state:\n";

        char[] divchars = new char[((boardSize + blockSize) * 2) + 1];
        Arrays.fill(divchars, '-');
        String lineDivider = new String(divchars);
        lineDivider += "\n";
        s += lineDivider;

        //[NOTE] This is messy!
        for (int y = 0; y < boardSize; y++) {
            s += "| ";
            for (int x = 0; x < boardSize; x++) {
                SudokuCell XY = board[x][y];
                if (getInitial){
                    if (XY.isInitial()){ s += XY; }
                    else               { s += " "; }
                }
                else { s += XY; }
                s += (x % blockSize == blockSize - 1) ? " | " : " ";
            }
            s += "\n";
            if (y % blockSize == blockSize - 1) { s += lineDivider; }
        }
        return s;
    }
    //endregion
}