package org.tark.gui;

import org.tark.sudoku.SudokuCell;
import org.tark.sudoku.SudokuGenerator;
import org.tark.sudoku.SudokuPuzzle;
import org.tark.sudoku.SudokuSolver;
import org.tark.util.IntPair;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * Controller for Sudoku UI, listens for changes in the puzzle grid and passes the information to the model.
 * Created by Tarkol on 06/12/2016.
 */
class SudokuUIController{

    private SudokuUIBoard view;
    private SudokuPuzzle model;
    private SudokuSolver solver;

    SudokuUIController(SudokuUIBoard sudokuView, SudokuPuzzle sudokuModel){
        this.view = sudokuView;
        this.model = sudokuModel;

        for (int y = 0; y < model.getBoardSize(); y++) {
            for (int x = 0; x < model.getBoardSize(); x++) {
                this.view.addCellKeyListener(x, y, new CellListener(x, y));
            }
        }
    }

    void changeBoardSize(int blockSize) {
        model = new SudokuPuzzle(blockSize);
        view.changeBlockSize(blockSize);
    }

    void generatePuzzle(){
        SudokuGenerator.generatePuzzle(model);
        solver = new SudokuSolver(model);

        setBoardFromModel();
        System.out.print(model);
    }

    boolean checkSolution(){
        if (!model.checkPuzzleForConflicts()){
            if (solver.solve(false, false))
                view.showNoConflicts();
            else{
                //find conflicting cels, get solution and compare differing cells
            }
        }
        else{
            ArrayList<IntPair> conflicts = model.getPuzzleConflicts();
            view.showConflicts(conflicts);
        }
        return false;
    }

    void solve(boolean fromInitial){
        solver = new SudokuSolver(model);
        solver.solve(true, fromInitial);
        setBoardFromModel();
    }



    private void setBoardFromModel(){
        for (int y = 0; y < model.getBoardSize(); y++) {
            for (int x = 0; x < model.getBoardSize(); x++) {
                SudokuCell currentCell = model.getCell(x, y);
                view.setCellValue(x, y, currentCell);
            }
        }
    }

    private class CellListener implements KeyListener {

        private int row;
        private int col;

        CellListener(int row, int col){
            super();
            this.row = row;
            this.col = col;
        }

        public void keyPressed(KeyEvent e) {

        }

        public void keyReleased(KeyEvent e) {
            model.getCell(row, col).setDigit(view.getCellValue(row, col), false);
        }

        public void keyTyped(KeyEvent e) {

        }
    }
}
