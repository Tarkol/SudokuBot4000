package org.tark;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tarkol on 25/10/2016.
 * This class is used to represent a complete Sudoku puzzle and includes functions to solve it.
 */
public class SudokuPuzzle {
    private final int boardSize = 9;
    private final int blockSize = 3;
    private SudokuCell[][] board;

    public SudokuPuzzle(int[][] board) {
        this.board = new SudokuCell[boardSize][boardSize];
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                this.board[x][y] = new SudokuCell(board[x][y], board[x][y] != 0);
            }
        }
    }

    public int getBoardSize() { return boardSize; }

    public String calcDIMACSString(){
        int numClauses = 0;
        String clauses = "";
        String subs;

        //Clauses for individual cells
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                subs = "";
                for (int i = 1; i <= boardSize; i++){
                    clauses += String.format("%d%d%d ", x, y, i);
                    for (int i2 = 1; i2 <= boardSize; i2++){
                        if (i != i2 && i2 > i) {
                            subs += String.format("-%d%d%d ",    x, y, i);
                            subs += String.format("-%d%d%d 0\n", x, y, i2);
                            numClauses++;
                        }
                    }
                }
                clauses += "0\n";
                numClauses++;
                clauses += subs;
            }
        }

        //Clauses for rows
        for (int x = 0; x < boardSize; x++){
            for (int i = 1; i <= boardSize; i++){
                subs = "";
                for (int y = 0; y < boardSize; y ++){
                    clauses += String.format("%d%d%d ", x, y, i);
                    numClauses++;
                    for (int y2 = 0; y2 < boardSize; y2++){
                        if (y != y2 && y2 > y){
                            subs += String.format("-%d%d%d ",    x, y,  i);
                            subs += String.format("-%d%d%d 0\n", x, y2, i);
                            numClauses++;
                        }
                    }
                }
                clauses += "0\n";
                numClauses++;
                clauses += subs;
            }
        }

        //Clauses for columns
        for (int y = 0; y < boardSize; y++){
            for (int i = 1; i <= boardSize; i++){
                subs = "";
                for (int x = 0; x < boardSize; x ++){
                    clauses += String.format("%d%d%d ", x, y, i);
                    for (int x2 = 0; x2 < boardSize; x2++){
                        if (x != x2 && x2 > x){
                            subs += String.format("-%d%d%d ",    x,  y, i);
                            subs += String.format("-%d%d%d 0\n", x2, y, i);
                            numClauses++;
                        }
                    }
                }
                clauses += "0\n";
                numClauses++;
                clauses += subs;
            }
        }
        //Clauses for blocks --Get a better way to do this.

        for (int blockY = 0; blockY < blockSize; blockY++){

            for (int blockX = 0; blockX < blockSize; blockX++) {
                for (int i = 1; i <= boardSize; i++) {
                    subs = "";
                    for (int x = 0; x < blockSize; x ++){
                        for (int y = 0; y < blockSize; y++) {
                            clauses += String.format("%d%d%d ", x + (blockX * blockSize),
                                    y + (blockY * blockSize), i);
                            for (int y2 = 0; y2 < blockSize; y2++) {
                                if (y != y2 && y2 > y){
                                    subs += String.format("-%d%d%d ",    x +  (blockX * blockSize),
                                            y +  (blockY * blockSize),  i);
                                    subs += String.format("-%d%d%d 0\n", x +  (blockX * blockSize),
                                            y2 + (blockY * blockSize),  i);
                                    numClauses++;
                                }
                            }
                        }

                    }
                    clauses += "0\n";
                    numClauses++;
                    clauses += subs;
                }

            }

        }

        //Clauses for fixed cells
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                if (board[x][y].getDigit() > 0){
                    clauses += String.format("%d%d%d 0\n", x, y, board[x][y].getDigit());
                    numClauses++;
                }
            }
        }
        int numVars = boardSize * boardSize * boardSize;
        String header = String.format("p cnf %d %d\n", numVars, numClauses);
        return header + clauses;
    }

    private ArrayList<ArrayList<Integer>> calcDIMACSInteger(){
        ArrayList<ArrayList<Integer>> allClauses = new ArrayList<>();
        ArrayList<Integer> clause;
        ArrayList<Integer> subclause;

        //Clauses for individual cells
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                clause = new ArrayList<>();
                for (int i = 1; i <= boardSize; i++){
                    clause.add((x * 100) + (y * 10) + i);
                    for (int i2 = 1; i2 <= boardSize; i2++){
                        if (i != i2 && i2 > i) {
                            subclause = new ArrayList<>();
                            subclause.add(-1 * ((100 * x)  + (10 * y)  + i));
                            subclause.add(-1 * ((100 * x)  + (10 * y)  + i2));
                            allClauses.add(subclause);
                        }
                    }
                }
                allClauses.add(clause);
            }
        }

        //Clauses for rows
        for (int x = 0; x < boardSize; x++){
            for (int i = 1; i <= boardSize; i++){
                clause = new ArrayList<>();
                for (int y = 0; y < boardSize; y ++){
                    clause.add((x * 100) + (y * 10) + i);
                    for (int y2 = 0; y2 < boardSize; y2++){
                        if (y != y2 && y2 > y){
                            subclause = new ArrayList<>();
                            subclause.add(-1 * ((100 * x)  + (10 * y)  + i));
                            subclause.add(-1 * ((100 * x)  + (10 * y2) + i));
                            allClauses.add(subclause);
                        }
                    }
                }
                allClauses.add(clause);
            }
        }

        //Clauses for columns
        for (int y = 0; y < boardSize; y++){
            for (int i = 1; i <= boardSize; i++){
                clause = new ArrayList<>();
                for (int x = 0; x < boardSize; x ++){
                    clause.add((x * 100) + (y * 10) + i);
                    for (int x2 = 0; x2 < boardSize; x2++){
                        if (x != x2 && x2 > x){
                            subclause = new ArrayList<>();
                            subclause.add(-1 * ((100 * x)  + (10 * y) + i));
                            subclause.add(-1 * ((100 * x2) + (10 * y) + i));
                            allClauses.add(subclause);
                        }
                    }
                }
                allClauses.add(clause);
            }
        }

        //Clauses for blocks --Get a better way to do this.
        for (int blockY = 0; blockY < blockSize; blockY++){
            for (int blockX = 0; blockX < blockSize; blockX++) {
                for (int i = 1; i <= boardSize; i++) {
                    clause = new ArrayList<>();
                    for (int x = 0; x < blockSize; x ++){
                        for (int y = 0; y < blockSize; y++) {
                            clause.add((100 * (x + (blockX * blockSize))) + (10 * (y + (blockY * blockSize))) + i);
                            for (int y2 = 0; y2 < blockSize; y2++) {
                                if (y != y2 && y2 > y){
                                    subclause = new ArrayList<>();
                                    subclause.add(-1 * ((100 * (x + (blockX * blockSize))) + (10 * (y  + (blockY * blockSize))) + i));
                                    subclause.add(-1 * ((100 * (x + (blockX * blockSize))) + (10 * (y2 + (blockY * blockSize))) + i));
                                    allClauses.add(subclause);
                                }
                            }
                        }
                    }
                    allClauses.add(clause);
                }
            }
        }

        //Clauses for fixed cells
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                if (board[x][y].getDigit() > 0){
                    subclause = new ArrayList<>();
                    subclause.add((100 * x)  + (10 * y)  + board[x][y].getDigit());
                    allClauses.add(subclause);
                }
            }
        }

        return allClauses;
    }

    private void setCellsFromDIMACS(int[] vars, boolean verbose) {
        for (int var : vars) {
            if (var > 0) {
                int x = (var / 100) % 10;
                int y = (var / 10) % 10;
                int num = var % 10;
                if (board[x][y].setDigit(num)) {
                    if (verbose)
                        System.out.printf("Assigning value %d to (%d, %d).\n", num, x, y);
                } else {
                    if (verbose)
                        System.out.printf("Not assigning value %d to (%d, %d), would overwrite initial value %d.\n", num, x, y, board[x][y].getDigit());
                }
            }
        }
    }

    public boolean solve(boolean verbose){
        long startTime = System.currentTimeMillis();
        long parseTime = 0;
        long SATTime = 0;
        try {
            ArrayList<ArrayList<Integer>> allClauses = this.calcDIMACSInteger();
            ISolver solver = SolverFactory.newDefault();
            solver.newVar(889);
            solver.setExpectedNumberOfClauses(allClauses.size());
            for (ArrayList<Integer> clause : allClauses) {
                int[] clauseArray = clause.stream().mapToInt(i -> i).toArray();
                solver.addClause(new VecInt(clauseArray));
            }
            parseTime = System.currentTimeMillis() - startTime;
            startTime = System.currentTimeMillis();

            IProblem problem = solver;
            if (problem.isSatisfiable()) {
                SATTime = System.currentTimeMillis() - startTime;
                int[] solution = problem.model();
                this.setCellsFromDIMACS(solution, verbose);
                if (verbose){
                    System.out.println("Satisfiable!");
                    System.out.printf("Statistics:!\n");
                    System.out.printf("Parse time: %dms\n", parseTime);
                    System.out.printf("SAT time: %dms\n", SATTime);
                    System.out.printf("Number of variables: %d\n", solver.nVars());
                    System.out.printf("Number of constraints: %d\n", solver.nConstraints());
                    System.out.println(solver.getStat());
                }
            }
            else{
                if (verbose) { System.out.println("Unsatisfiable!"); }
                return false;
            }
        }
        catch (ContradictionException e){
            System.out.println("CNF Contradiction! " + e);
            return false;
            }
        catch (TimeoutException e){
            System.out.println("The problem has timed out. " + e);
            return false;
        }
        return true;
    }

    public String getBoardString(int boardType){
        String s = (boardType == 0) ? "Initial Sudoku board state:\n" : "Current Sudoku board state:\n";

        char[] divchars = new char[(boardSize * 2) + 1];
        Arrays.fill(divchars, '-');
        String lineDivider = new String(divchars);
        lineDivider += "\n";
        s += lineDivider;

        for (int y = 0; y < boardSize; y++) {
            s += "|";
            for (int x = 0; x < boardSize; x++) {
                int XY = (boardType == 0 && board[x][y].isInitial()) ? 0 : board[x][y].getDigit();
                s += (XY == 0 ? " " : XY) + "|";
            }
            s += "\n";
            s += lineDivider;
        }
        return s;
    }

    @Override
    public String toString() {
        return getBoardString(1);
    }
}