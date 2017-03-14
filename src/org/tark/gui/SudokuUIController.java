package org.tark.gui;

import org.tark.sudoku.SudokuCell;
import org.tark.sudoku.SudokuGenerator;
import org.tark.sudoku.SudokuPuzzle;
import org.tark.sudoku.SudokuSolver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        generatePuzzle();
        solver = new SudokuSolver(model);

        for (int y = 0; y < model.getBoardSize(); y++) {
            for (int x = 0; x < model.getBoardSize(); x++) {
                this.view.addCellListener(x, y, new CellListener(x, y));
            }
        }
    }

    void generatePuzzle(){
        SudokuGenerator.generatePuzzle(model);
        setBoardFromModel();
        System.out.print(model);
    }

    void solvePuzzle(){
        solver.solve();
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

    private class CellListener implements ActionListener{

        private int row;
        private int col;

        CellListener(int row, int col){
            super();
            this.row = row;
            this.col = col;
        }

        public void actionPerformed(ActionEvent e) {
            if (!model.getCell(row, col).isInitial())
                model.getCell(row, col).setDigit(view.getCellValue(row, col), false);
        }
    }
}
