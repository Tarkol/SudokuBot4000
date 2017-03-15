package org.tark.sudoku;

import org.tark.util.IntPair;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tarkol on 25/10/2016.
 * This class is used to represent a complete Sudoku puzzle and includes functions to solve it.
 */
public class SudokuPuzzle {
    private int boardSize;
    private int blockSize;
    private SudokuCell[][] board;
    private ArrayList<IntPair> conflictingCells;

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
        try {
            for (int y = 0; y < boardSize; y++) {
                for (int x = 0; x < boardSize; x++) {
                    this.board[x][y] = new SudokuCell(board[x][y], board[x][y] != 0);
                }
            }
        }
        catch (Exception e){
            System.err.println("The board input was not recognised." + e.getMessage());
        }
    }

    /**
     * Creates a Sudoku puzzle from an existing grid of SudokuCells
     * @param cellBoard The grid of SudokuCells to convert into a full puzzle.
     */
    public SudokuPuzzle(SudokuCell[][] cellBoard) {
        this.blockSize = (int)Math.sqrt(cellBoard.length);
        this.boardSize = cellBoard.length;
        this.board = new SudokuCell[boardSize][boardSize];
        try {
            for (int y = 0; y < boardSize; y++) {
                for (int x = 0; x < boardSize; x++) {
                    SudokuCell currCell = cellBoard[x][y];
                    this.board[x][y] = new SudokuCell(currCell.getDigit(), currCell.isInitial());
                }
            }
        }
        catch (Exception e){
            System.err.println("The board input was not recognised." + e.getMessage());
        }
    }

    public int getBoardSize() { return boardSize; }

    public int getBlockSize() { return blockSize; }

    SudokuCell[][] getBoard() { return board; }

    public SudokuCell getCell(int x, int y){
        return board[x][y];
    }

    public void setCell(int x, int y, int value){
        board[x][y].setDigit(value, false);
    }

    /**
     * Resets the puzzle back to a blank state.
     */
    void reset(){
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                board[x][y].reset();
            }
        }
    }

    public boolean checkPuzzleForConflicts(){
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                if (checkCellForConflicts(getCell(x, y), x, y)){
                    return true;
                }
            }
        }
        return false;
    }

    boolean checkCellForConflicts(SudokuCell cell, int cellX, int cellY){
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

    //TODO stop duplicates not a big deal?
    public ArrayList<IntPair> getPuzzleConflicts(){
        ArrayList<IntPair> conflicts = new ArrayList<>();
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                conflicts.addAll(getCellConflicts(getCell(x, y), x, y));
            }
        }
        return conflicts;
    }

    private ArrayList<IntPair> getCellConflicts(SudokuCell cell, int cellX, int cellY){
        ArrayList<IntPair> conflicts = new ArrayList<>();

        //Check cell row
        for (int x = 0; x < boardSize; x++) {
            SudokuCell cellInBoard = board[x][cellY];
            if (cellInBoard.getDigit() > 0 && cellInBoard.getDigit() == cell.getDigit() && cellInBoard != cell) {
                conflicts.add(new IntPair(x, cellY));
            }
        }

        //Check cell column
        for (int y = 0; y < boardSize; y++) {
            SudokuCell cellInBoard = board[cellX][y];
            if (cellInBoard.getDigit() > 0 && cellInBoard.getDigit() == cell.getDigit() && cellInBoard != cell) {
                conflicts.add(new IntPair(cellX, y));
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
                    conflicts.add(new IntPair(trueX, trueY));
                }
            }
        }
        return conflicts;
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
        int digitLength = String.valueOf(boardSize).length();
        String digitFormat = String.format("%%%s.%ss", digitLength, digitLength); // %x.xs

        char[] divchars = new char[((boardSize + blockSize) * (digitLength + 1)) + 1];
        Arrays.fill(divchars, '-');
        String lineDivider = new String(divchars);
        lineDivider += "\n";
        s += lineDivider;

        //[NOTE] This is messy!
        for (int y = 0; y < boardSize; y++) {
            s += "| ";
            for (int x = 0; x < boardSize; x++) {
                SudokuCell XY = board[x][y];
                if (!getInitial || XY.isInitial()){
                    s += String.format(digitFormat, XY);
                }
                else {
                    s += String.format(digitFormat, "");
                }
                s += (x % blockSize == blockSize - 1) ? " | " : " ";
            }
            s += "\n";
            if (y % blockSize == blockSize - 1) { s += lineDivider; }
        }
        return s;
    }
    //endregion

    public SudokuPuzzle copy(){
        return new SudokuPuzzle(board);
    }
}