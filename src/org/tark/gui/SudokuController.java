package org.tark.gui;

import org.tark.sudoku.SudokuCell;
import org.tark.sudoku.SudokuGenerator;
import org.tark.sudoku.SudokuPuzzle;
import org.tark.sudoku.SudokuSolver;
import org.tark.util.IntPair;

import java.util.ArrayList;

/**
 * Controller for Sudoku UI, listens for changes in the puzzle grid and passes the information to the model.
 * Created by Tarkol on 06/12/2016.
 */
class SudokuController {

    private SudokuBoard board;
    private SudokuPuzzle model;
    private SudokuSolver solver;

    public SudokuController(SudokuUIView sudokuView, SudokuPuzzle sudokuModel){
        this.board = sudokuView.getBoard();
        this.model = sudokuModel;
    }

    void changeBlockSize(int blockSize) {
        model = new SudokuPuzzle(blockSize);
        board.changeBlockSize(blockSize);
    }

    void generatePuzzle(){
        SudokuGenerator.generatePuzzle(model);
        solver = new SudokuSolver(model);

        setBoardFromModel();
        System.out.print(model);
    }

    void checkSolution(){
        if (!model.checkPuzzleForConflicts()){
            if (solver.solve(false, false))
                board.showNoConflicts();
            else{
                //find conflicting cels, get solution and compare differing cells
            }
        }
        else{
            ArrayList<IntPair> conflicts = model.getPuzzleConflicts();
            board.showConflicts(conflicts);
        }
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
                board.setCellValue(x, y, currentCell);
            }
        }
    }

    void setCell(int value, int x, int y){
        model.setCell(x, y, value);
    }

}
