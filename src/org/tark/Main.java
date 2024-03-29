package org.tark;

import org.tark.sudoku.SudokuGenerator;
import org.tark.sudoku.SudokuPuzzle;
import org.tark.sudoku.SudokuSolver;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<String> fileInput = new ArrayList<>();
        try {
            fileInput = Files.readAllLines(Paths.get("input4x4blank"));
        } catch (java.io.IOException e) {
            System.out.printf("Error: %s", e);
        }
        int boardSize = fileInput.size();
        int[][] board = new int[boardSize][boardSize];
        for (int y = 0; y < boardSize; y++) {
            String currentLine = fileInput.get(y);
            for (int x = 0; x < currentLine.length(); x++) {
                board[x][y] = currentLine.charAt(x) - 48;
            }
        }


        SudokuPuzzle puzzle = new SudokuPuzzle(4);

        SudokuSolver solver = new SudokuSolver(puzzle);
        //SudokuGenerator.generatePuzzle(puzzle, solver);
        //SudokuPuzzle puzzle = new SudokuPuzzle(board);

        //System.out.print(solver);
        //solver.setVerbosity(true);
        solver.useMinimalSet(false);
        solver.solve(true, true);

        System.out.print(puzzle.getBoardString(true));
        System.out.print(puzzle.getBoardString(false));

        System.out.println(solver.getGenerateTime());
        System.out.println(solver.getSolveTime());





        /*
        for (int x = 0; x < 1000; x++){
            SudokuPuzzle puzzleGenerated = SudokuPuzzle.generatePuzzle(2);
            puzzleGenerated.solve(false);
            System.out.print(puzzleGenerated.getBoardString(true));
            System.out.print(puzzleGenerated.getBoardString(false));
            System.out.println("------------------------------------------------");
        }
        */

    }
}
