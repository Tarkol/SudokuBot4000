package org.tark.sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by conno on 14/03/2017.
 */
public class SudokuGenerator {

    /**
     * Generates a sudoku puzzle with a unique solution.
     * @param puzzle The board to generate a new puzzle on.
     * @return Nothing, but the puzzle input will be updated.
     */
    //TODO: Reuse the same solver instead of making a new one for every number.
    public static void generatePuzzle(SudokuPuzzle puzzle){
        //To generate a puzzle we will start by generating a filled board state,
        //then remove numbers from the board to create the initial puzzle.

        //First reset the input puzzle.
        puzzle.reset();
        SudokuSolver solver = new SudokuSolver(puzzle);

        //Then we get all the cells and randomize the order we iterate through them.
        ArrayList<int[]> boardLocations = makeCoordinateList(puzzle.getBoardSize());

        //Then randomly assign values to each cell. If this results in an invalid puzzle, try a different number.
        Random rng = new Random();
        while (!boardLocations.isEmpty()){
            Collections.shuffle(boardLocations);
            int[] loc = boardLocations.get(0);
            SudokuCell cell = puzzle.getCell(loc[0], loc[1]);
            cell.setDigit(rng.nextInt(puzzle.getBoardSize()) + 1, true);
            if (!puzzle.cellHasValueConflict(cell, loc[0], loc[1]) && solver.hasSolution()) {
                boardLocations.remove(loc);
            }
            else{
                cell.reset();
            }
        }

        //Now we have created a puzzle solution. The next step is to remove some numbers to make a playable puzzle.
        //This is done by removing values from the solution while checking that only one solution to the puzzle exists.

        //For every cell, try setting it to 0. If the solution is still unique after this, the puzzle is still valid.
        //If it's not unique, restore the cell's previous value.
        ArrayList<SudokuCell>cells = makeCellList(puzzle.getBoard(), puzzle.getBoardSize());
        Collections.shuffle(cells);
        for (SudokuCell cell:cells){
            int lastCellValue = cell.getDigit();
            cell.reset();
            if (!solver.hasUniqueSolution()) { cell.setDigit(lastCellValue, true); }
        }
    }

    private static ArrayList<int[]> makeCoordinateList(int boardSize){
        ArrayList<int[]> coords = new ArrayList<>();
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                coords.add(new int[] {x, y});
            }
        }
        return coords;
    }

    private static ArrayList<SudokuCell> makeCellList(SudokuCell[][] board, int boardSize){
        ArrayList<SudokuCell> cells = new ArrayList<>();
        for (int y = 0; y < boardSize; y++)
            for (int x = 0; x < boardSize; x++)
                cells.add(board[x][y]);
        return cells;
    }
}
