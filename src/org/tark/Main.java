package org.tark;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.TimeoutException;

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
        ArrayList<ArrayList<Integer>> allClauses = puzzle.getDIMACSInteger();

        ISolver solver = SolverFactory.newDefault();
        solver.newVar(889);
        solver.setExpectedNumberOfClauses(allClauses.size());

        long startTime = System.currentTimeMillis();
        for (ArrayList<Integer> clause: allClauses) {
            int[] clauseArray = clause.stream().mapToInt(i->i).toArray();
            try {
                solver.addClause(new VecInt(clauseArray));
            }
            catch (ContradictionException e){
                System.out.println("CNF Contradiction! " + e);
            }
        }
        long parseTime = System.currentTimeMillis() - startTime;

        IProblem problem = solver;

        startTime = System.currentTimeMillis();
        long SATTime = 0;
        try {
            if (problem.isSatisfiable()) {
                SATTime = System.currentTimeMillis() - startTime;
                System.out.println("Satisfiable !");
                int[] solution = problem.model();
                puzzle.setCellsFromDIMACS(solution);
            }
            else{
                System.out.println("Unsatisfiable !");
            }
        }
        catch (TimeoutException e){
            System.out.println("The problem has timed out. " + e);
        }

        System.out.print(puzzle.getDIMACSString());

        System.out.print(puzzle.getBoardString(0));
        System.out.print(puzzle.getBoardString(1));

        System.out.printf("Parse time: %dms\n", parseTime);
        System.out.printf("SAT time: %dms\n", SATTime);
        System.out.println(solver.getStat());



    }
}
