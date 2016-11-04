package org.tark.sudoku;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.minisat.orders.RandomLiteralSelectionStrategy;
import org.sat4j.minisat.orders.RandomWalkDecorator;
import org.sat4j.specs.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Tarkol on 25/10/2016.
 * This class is used to represent a complete Sudoku puzzle and includes functions to solve it.
 */
public class SudokuPuzzle {
    private int boardSize;
    private int blockSize;
    private SudokuCell[][] board;

    /**
     * Makes an empty board with a given size for the cell blocks.
     * Only makes square blocks.
     * @param blockSize The blocks in the puzzle will be squares of this size.
     */
    public SudokuPuzzle(int blockSize) {
        this.blockSize = blockSize;
        this.boardSize = blockSize * blockSize;
        this.board = new SudokuCell[boardSize][boardSize];
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                this.board[x][y] = new SudokuCell(0, false);
            }
        }
    } //temp?

    /**
     * Creates a board from an existing square grid.
     * Blocks are assumed to be square with size equal to the root of the board size.
     * @param board A 2D array with the initial state of the sudoku puzzle.
     */
    public SudokuPuzzle(int[][] board) {
        this.blockSize = (int)Math.sqrt(new Double(board.length));
        this.boardSize = board.length;
        this.board = new SudokuCell[boardSize][boardSize];
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                this.board[x][y] = new SudokuCell(board[x][y], board[x][y] != 0);
            }
        }
    }

    public int getBoardSize() { return boardSize; }

    public String getClausesString(){
        ArrayList<ArrayList<Integer>> allClauses = this.calcClauses();
        int numVars = boardSize * boardSize * boardSize;
        String clauseString = "";
        int numClauses = 0;
        for (ArrayList<Integer> subClauses:allClauses) {
            for (Integer clause:subClauses){
                clauseString += clause + " ";
                numClauses++;
            }
            clauseString += "\n";
        }
        String header = String.format("p cnf %d %d\n", numVars, numClauses);
        return header + clauseString;
    }

    //TODO: Bad, doesn't work for 2 digit co-ordinates.
    private ArrayList<ArrayList<Integer>> calcClauses(){
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
                    int fixedClause = (100 * x)  + (10 * y)  + board[x][y].getDigit();
                    subclause = new ArrayList<>();
                    subclause.add(fixedClause);
                    allClauses.add(subclause);
                }
            }
        }
        return allClauses;
    }

    //TODO: Bad, doesn't work for 2 digit co-ordinates.
    private void setCellsFromDIMACS(int[] vars, boolean verbose) {
        for (int var : vars) {
            if (var > 0) {
                int x = (var / 100) % 10;
                int y = (var / 10) % 10;
                int num = var % 10;
                if (board[x][y].setDigit(num, false)) {
                    if (verbose)
                        System.out.printf("Assigning value %d to (%d, %d).\n", num, x, y);
                } else {
                    if (verbose)
                        System.out.printf("Not assigning value %d to (%d, %d), would overwrite initial value %d.\n", num, x, y, board[x][y].getDigit());
                }
            }
        }
    }

    /**
     * Attempts to solve the puzzle from its current state.
     * @param verbose Outputs what the solver is doing if true.
     * @return True if the puzzle has been solved successfully.
     */
    public boolean solve(boolean verbose){
        int[] solution = getSolution(calcClauses(), verbose);
        setCellsFromDIMACS(solution, verbose);
        return solution.length > 0;
    }

    /**
     * Gets the DIMACs output for the solved puzzle.
     * @param clauses The set of clauses to use when solving. [NOTE] rather not have this really
     * @param verbose Outputs what the solver is doing if true.
     * @return An array with all the DIMACs variables to form the solution.
     * [NOTE] deal with exceptions in a better way than nulls
     */
    //TODO: Maybe actually do exception handling. Maybe doesn't have to be its own method?
    private int[] getSolution(ArrayList<ArrayList<Integer>> clauses, boolean verbose){
        long startTime = System.currentTimeMillis();
        long parseTime;
        long SATTime;
        int[] solution = {};
        try {
            ISolver solver = SolverFactory.newDefault();
            solver.newVar(889);
            solver.setExpectedNumberOfClauses(clauses.size());
            for (ArrayList<Integer> clause : clauses) {
                int[] clauseArray = clause.stream().mapToInt(i -> i).toArray();
                solver.addClause(new VecInt(clauseArray));
            }
            parseTime = System.currentTimeMillis() - startTime;
            startTime = System.currentTimeMillis();

            IProblem problem = solver;
            if (problem.isSatisfiable()) {
                SATTime = System.currentTimeMillis() - startTime;
                solution = problem.model();
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
            }
            return solution;
        }
        catch (ContradictionException e){
            System.out.println("CNF Contradiction! " + e);
            return null;
            }
        catch (TimeoutException e){
            System.out.println("The problem has timed out. " + e);
            return null;
        }
    }

    /**
     * Checks if the puzzle has a single unique solution. That is, each cell has only one correct value.
     * @return True if there is only one unique solution.
     */
    //TODO: Make this not awful. Use same solver again instead of reinstantiating everything.
    private boolean hasUniqueSolution(){
        //First, get the solution for the current puzzle state.
        ArrayList<ArrayList<Integer>> clauses = calcClauses();
        int[] solution = getSolution(clauses, false);

        //Add a negation clause using the solution.
        //This means that the solver cannot use the same combination of cell values to solve the puzzle.
        ArrayList<Integer> negationClause = new ArrayList<>();
        for (int clause:solution)
            if (clause > 0) { negationClause.add(clause * -1); }
        clauses.add(negationClause);

        //Try to find another solution using this additional constraint.
        //If no other solution is found then the puzzle solution is unique.
        solution = getSolution(clauses, false);
        if (solution == null || solution.length == 0) { return true; }
        else { return false; }
    }

    private boolean hasSolution(){
        int[] solution = getSolution(calcClauses(), false);
        if (solution == null || solution.length == 0) { return false; }
        return true;
    }
    /**
     * Generates a sudoku puzzle with a unique solution.
     * @param blockSize The size of the blocks that make up the overall puzzle.
     * @return A SudokuPuzzle object that represents the puzzle.
     */
    //TODO: actually make this work.
    public static SudokuPuzzle generatePuzzle(int blockSize){
        //First create and solve an empty puzzle. This will be the solution to the puzzle we are generating.
        /*
        1) start with an empty board
        2) add a random number at one of the free cells (the cell is chosen randomly,
           and the number is chosen randomly from the list of numbers valid for this cell according to the SuDoKu rules)
        3) Use the backtracking solver to check if the current board has at least one valid solution.
           If not, undo step 2 and repeat with another number and cell.
           Note that this step might produce full valid boards on its own, but those are in no way random.
        4) Repeat until the board is completely filled with numbers
         */
        SudokuPuzzle puzzle = new SudokuPuzzle(blockSize);
        ArrayList<SudokuCell>cells = puzzle.getAllCells();
        Collections.shuffle(cells);
        Random rng = new Random();


        int cellsDone = 0;
        while (cellsDone < puzzle.boardSize * puzzle.boardSize){
            cells.get(cellsDone).setDigit(rng.nextInt(puzzle.boardSize) + 1, false);
            if (puzzle.hasSolution()) { cellsDone++; }
            else {
                cells.get(cellsDone).setDigit(0, false);
            }
        }

        //Get all cells in the puzzle, put them in a random order.
        Collections.shuffle(cells);

        //For every cell, try setting it to 0. If the solution is still unique after this, the puzzle is still valid.
        //If it's not unique, restore the cell's previous value.
        for (SudokuCell cell:cells){
            int lastCellValue = cell.getDigit();
            cell.setDigit(0, false);
            if (!puzzle.hasUniqueSolution()) { cell.setDigit(lastCellValue, true); }
        }
        return puzzle;
    }

    private ArrayList<SudokuCell> getAllCells(){
        ArrayList<SudokuCell> cells = new ArrayList<>();
        for (int y = 0; y < boardSize; y++)
            for (int x = 0; x < boardSize; x++)
                cells.add(board[x][y]);
        return cells;
    }

    @Override
    public String toString() {
        return getBoardString(false);
    }

    /**
     * Return the sudoku board as a string.
     * @param getInitial Returns the initial board state if true.
     * @return A string containing the sudoku board.
     */
    public String getBoardString(boolean getInitial){
        String s = (getInitial) ? "Initial Sudoku board state:\n" : "Current Sudoku board state:\n";

        char[] divchars = new char[((boardSize + blockSize) * 2) + 1];
        Arrays.fill(divchars, '-');
        String lineDivider = new String(divchars);
        lineDivider += "\n";
        s += lineDivider;

        //[NOTE] This is messy!
        for (int y = 0; y < boardSize; y++) {
            s += "| ";
            for (int x = 0; x < boardSize; x++) {
                SudokuCell XY = board[x][y];
                if (getInitial){
                    if (XY.isInitial()){ s += XY; }
                    else               { s += " "; }
                }
                else { s += XY; }
                s += (x % blockSize == blockSize - 1) ? " | " : " ";
            }
            s += "\n";
            if (y % blockSize == blockSize - 1) { s += lineDivider; }
        }
        return s;
    }
}