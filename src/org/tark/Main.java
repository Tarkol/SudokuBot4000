package org.tark;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<String> fileInput = new ArrayList<>();
        try {
            fileInput = Files.readAllLines(Paths.get("input"));
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

        SudokuPuzzle puzzle = new SudokuPuzzle(board);

        System.out.print(puzzle.calcDIMACSString());
        puzzle.solve(true);

        System.out.print(puzzle.getBoardString(0));
        System.out.print(puzzle.getBoardString(1));



    }
}
