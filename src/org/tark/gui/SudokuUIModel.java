package org.tark.gui;

import org.tark.sudoku.SudokuCell;
import org.tark.sudoku.SudokuPuzzle;
import org.tark.sudoku.SudokuSolver;
import org.tark.sudoku.SudokuGenerator;

import java.beans.PropertyChangeSupport;

/**
 * Acts as a separator between the UI and the Sudoku classes.
 * Unfinished, remains unused.
 * Created by conno on 11/11/2016.
 */

class SudokuUIModel {

    private SudokuPuzzle puzzle;
    private SudokuSolver solver;
    private static final String STATE_NAME = "STATE";
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private SudokuState state = SudokuState.NONE;

    public SudokuUIModel(){
        puzzle = new SudokuPuzzle(3);
        solver = new SudokuSolver(puzzle);
    }

    public void generatePuzzle(){
        SudokuGenerator.generatePuzzle(puzzle, solver);
        //support.firePropertyChange()
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

    void setCell(int x, int y, int value){
        puzzle.getCell(x, y).setDigit(value, false);
    }

    //stubs
    public void updateCell(){}


   // public void solvePuzzle(){

    //puzzle.solve(false)
  //  }

    public void getHints(){}


}

