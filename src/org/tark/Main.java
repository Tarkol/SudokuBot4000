package org.tark;

import org.sat4j.tools.SingleSolutionDetector;
import org.tark.sudoku.SudokuPuzzle;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<String> fileInput = new ArrayList<>();
        try {
            fileInput = Files.readAllLines(Paths.get("input3x3"));
        } catch (java.io.IOException e) {
            System.out.printf("Error: %s", e);
        }
        int boardSize = fileInput.size();
        int[][] board = new int[boardSize][boardSize];
        for (int y = 0; y < boardSize; y++) {
            String currentLine = fileInput.get(y);
            for (int x = 0; x < currentLine.length(); x++) {
                board[x][y] = currentLine.charAt(x)-48;
            }
        }

        /*
        SudokuPuzzle puzzle = new SudokuPuzzle(board);

        System.out.print(puzzle.getClausesString());
        puzzle.solve(true);

        System.out.print(puzzle.getBoardString(true));
        System.out.print(puzzle.getBoardString(false));
        */

        SudokuPuzzle puzzleGenerated = SudokuPuzzle.generatePuzzle(3);
        puzzleGenerated.solve(false);
        System.out.print(puzzleGenerated.getBoardString(true));
        System.out.print(puzzleGenerated.getBoardString(false));

    }
}
