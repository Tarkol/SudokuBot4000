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
    private boolean waitingForHint;

    public SudokuController(SudokuUIView sudokuView, SudokuPuzzle sudokuModel){
        this.board = sudokuView.getBoard();
        this.model = sudokuModel;
        waitingForHint = false;
    }

    void changeBlockSize(int blockSize) {
        model = new SudokuPuzzle(blockSize);
        board.changeBlockSize(blockSize);
    }

    void generatePuzzle(){

        solver = new SudokuSolver(model);
        SudokuGenerator.generatePuzzle(model, solver);

        setBoardFromModel();
        waitingForHint = false;
    }

    void checkSolution(){
        if (!model.checkPuzzleForConflicts()){
            if (solver.solve(false, false))
                board.showNoConflicts();
            else{
                //find conflicting cells, get solution and compare differing cells
            }
        }
        else{
            ArrayList<IntPair> conflicts = model.getPuzzleConflicts();
            board.showConflicts(conflicts);
        }
    }

    void requestHint(){
        waitingForHint = true;
    }

    boolean getHintStatus(){
        return waitingForHint;
    }

    void getHintForCell(int x, int y){
        SudokuPuzzle solution = model.copy();
        SudokuSolver solutionSolver = new SudokuSolver(solution);
        solutionSolver.solve(true, true);
        model.setCell(x, y, solution.getCell(x, y).getDigit());
        setBoardFromModel();
        waitingForHint = false;
    }

    void solve(boolean fromInitial){
        solver = new SudokuSolver(model);
        solver.solve(true, fromInitial);
        setBoardFromModel();
    }

    void printClauses(){
        if (solver != null){
            System.out.print(solver);
        }
    }

    //Shouldn't be needed, a listener on the board that reacts to model changes would be preferable.
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
