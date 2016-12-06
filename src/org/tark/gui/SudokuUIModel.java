package org.tark.gui;

import org.tark.sudoku.SudokuCell;
import org.tark.sudoku.SudokuPuzzle;

/**
 * Acts as a separator between the UI and the Sudoku classes.
 * Needs a reference to the actual board I think.
 * Created by conno on 11/11/2016.
 */
class SudokuUIModel {

    private SudokuPuzzle puzzle;

    public SudokuUIModel(){
        puzzle = SudokuPuzzle.generatePuzzle(3); //Using a default value for now;
    }

    void loadPuzzle(SudokuPuzzle puzzle){
        this.puzzle = puzzle;
    }

    int getBoardSize(){
        return puzzle.getBoardSize();
    }

    int getBlockSize(){
        return puzzle.getBlockSize();
    }

    SudokuCell getCell(int x, int y){
        return puzzle.getCell(x, y);
    }

    public void updateCell(){}

    public void generatePuzzle(){}

    public void solvePuzzle(){
        puzzle.solve(false);
    }

    public void getHints(){}

}
