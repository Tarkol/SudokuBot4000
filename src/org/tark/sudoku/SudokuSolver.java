package org.tark.sudoku;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.ArrayList;

/**
 * Contains methods to solve a sudoku puzzle using a SAT solver.
 * Has methods to solve, check puzzle validity and generate DIMACs strings.
 * Created by conno on 14/03/2017.
 */
public class SudokuSolver {

    private SudokuPuzzle puzzle;
    private final ArrayList<ArrayList<Integer>> clausesBase;
    private boolean verbose = false;

    /**
     * A SudokuPuzzle needs to be given as a parameter, this is the puzzle that the solver will act on.
     * @param puzzle The puzzle to be solved.
     */
    public SudokuSolver(SudokuPuzzle puzzle){
        this.puzzle = puzzle;
        clausesBase = calcClausesBase();
    }

    /**
     * Controls the console output of the solver.
     * @param verbose True if solver progress should be output to console.
     */
    public void setVerbosity (boolean verbose) { this.verbose = verbose; }

    /**
     * Attempts to solve the puzzle. Can solve from the current or initial puzzle state.
     * @param saveSolution True if the solution should be saved onto the puzzle.
     * @param fromInitial True if the solver ignores current puzzle progress and uses the initial puzzle values.
     * @return True if the puzzle has been solved successfully.
     */
    public boolean solve(boolean saveSolution, boolean fromInitial){
        ArrayList<ArrayList<Integer>> clausesVariables = calcClausesVariables(fromInitial);
        int[] solution = getSolution(clausesVariables);
        if (solution.length > 0) {
            if (saveSolution) { setCellsFromDIMACS(solution); }
            return true;
        }
        else { return false; }
    }

    /**
     * Gets the DIMACs output showing the solution for for the current puzzle.
     * Uses the currently calculated base clauses combined with the clause input.
     * @param clausesVariables DIMACs clauses describing the current state of variables in the puzzle.
     * @return An array with all the DIMACs variables to form the solution.
     */
    private int[] getSolution(ArrayList<ArrayList<Integer>> clausesVariables){
        long startTime = System.currentTimeMillis();
        long parseTime;
        long SATTime;
        int[] solution = {};
        try {
            ISolver solver = SolverFactory.newDefault();
            //solver.newVar();
            solver.setExpectedNumberOfClauses(clausesBase.size() + clausesVariables.size());
            for (ArrayList<Integer> clause : clausesBase) {
                int[] clauseArray = clause.stream().mapToInt(i -> i).toArray();
                solver.addClause(new VecInt(clauseArray));
            }
            for (ArrayList<Integer> clause : clausesVariables) {
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
        //If an error occurs, report it and return an empty solution.
        catch (ContradictionException e){
            System.out.println("The CNF input contained contradictions. " + e.getMessage());
            return new int[]{};
        }
        catch (TimeoutException e){
            System.out.println("The problem has timed out. " + e);
            return new int[]{};
        }
    }

    /**
     * Checks if the puzzle has a single unique solution. That is, each cell has only one correct value.
     * @return True if there is only one unique solution.
     */
    public boolean hasUniqueSolution(){
        ArrayList<ArrayList<Integer>> clausesVariables = calcClausesVariables(true);

        //Maybe don't need to calc this twice.
        // If puzzle is solved once we already know one solution that won't change.
        // Why not use the same solution as a negation each time? Will halve number of solver calls.

        //First, get the solution for the current puzzle state.
        //If a solution does not exist then there is not a unique solution.
        int[] solution = getSolution(clausesVariables);
        if (solution.length == 0) { return false; }

        //Add a negation clause using the solution.
        //This means that the solver cannot use the same combination of cell values to solve the puzzle.
        ArrayList<Integer> negationClause = new ArrayList<>();
        for (int clause:solution)
            if (clause > 0) { negationClause.add(clause * -1); }
        clausesVariables.add(negationClause);

        //Try to find another solution using this additional constraint.
        //If no other solution is found then the puzzle solution is unique.
        solution = getSolution(clausesVariables);
        return (solution.length == 0);
    }

    @Override
    public String toString(){
        int boardSize = puzzle.getBoardSize();
        int numVars = boardSize * boardSize * boardSize;
        String clauseString = "";
        int numClauses = 0;
        ArrayList<ArrayList<Integer>> allClauses = new ArrayList<>();
        allClauses.addAll(clausesBase);
        allClauses.addAll(calcClausesVariables(false));
        for (ArrayList<Integer> subClauses : allClauses) {
            for (Integer clause:subClauses){
                clauseString += clause + " ";
            }
            clauseString += "\n";
            numClauses++;
        }
        String header = String.format("p cnf %d %d\n", numVars, numClauses);
        return header + clauseString;
    }

    /**
     * Calculates the clauses required to solve an empty puzzle.
     * Because this is always the same for a puzzle of a given size, it is calculated only once, separately
     * from the clauses describing the state of the puzzle.
     * @return A huge list of DIMACS clauses describing how to solve an empty Sudoku with the same size as the puzzle
     * attached to the solver..
     */
    private ArrayList<ArrayList<Integer>> calcClausesBase(){
        int boardSize = puzzle.getBoardSize();
        int blockSize = puzzle.getBlockSize();
        int rowLength = boardSize * boardSize;
        ArrayList<ArrayList<Integer>> baseClauses = new ArrayList<>();
        ArrayList<Integer> clause;
        ArrayList<Integer> subclause;

        //Clauses for individual cells
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                clause = new ArrayList<>();
                for (int i = 1; i <= boardSize; i++){
                    clause.add((x * boardSize) + (y * rowLength) + i);
                    for (int i2 = 1; i2 <= boardSize; i2++){
                        if (i != i2 && i2 > i) {
                            subclause = new ArrayList<>();
                            subclause.add(-1 * ((x * boardSize) + (y * rowLength)  + i));
                            subclause.add(-1 * ((x * boardSize) + (y * rowLength)  + i2));
                            baseClauses.add(subclause);
                        }
                    }
                }
                baseClauses.add(clause);
            }
        }

        //Clauses for rows
        for (int x = 0; x < boardSize; x++){
            for (int i = 1; i <= boardSize; i++){
                clause = new ArrayList<>();
                for (int y = 0; y < boardSize; y ++){
                    clause.add((x * boardSize) + (y * rowLength) + i);
                    for (int y2 = 0; y2 < boardSize; y2++){
                        if (y != y2 && y2 > y){
                            subclause = new ArrayList<>();
                            subclause.add(-1 * ((x * boardSize) + (y * rowLength)  + i));
                            subclause.add(-1 * ((x * boardSize) + (y2 * rowLength) + i));
                            baseClauses.add(subclause);
                        }
                    }
                }
                baseClauses.add(clause);
            }
        }

        //Clauses for columns
        for (int y = 0; y < boardSize; y++){
            for (int i = 1; i <= boardSize; i++){
                clause = new ArrayList<>();
                for (int x = 0; x < boardSize; x ++){
                    clause.add((x * boardSize) + (y * rowLength) + i);
                    for (int x2 = 0; x2 < boardSize; x2++){
                        if (x != x2 && x2 > x){
                            subclause = new ArrayList<>();
                            subclause.add(-1 * ((x  * boardSize) + (y * rowLength) + i));
                            subclause.add(-1 * ((x2 * boardSize) + (y * rowLength) + i));
                            baseClauses.add(subclause);
                        }
                    }
                }

                baseClauses.add(clause);
            }
        }

        //Clauses for blocks --Get a better way to do this.
        for (int blockY = 0; blockY < blockSize; blockY++){
            for (int blockX = 0; blockX < blockSize; blockX++) {
                for (int i = 1; i <= boardSize; i++) {
                    clause = new ArrayList<>();
                    for (int x = 0; x < blockSize; x ++){
                        for (int y = 0; y < blockSize; y++) {
                            clause.add(((x + (blockX * blockSize)) * boardSize) + ((y + (blockY * blockSize)) * rowLength) + i);
                            for (int y2 = 0; y2 < blockSize; y2++) {
                                if (y != y2 && y2 > y){
                                    subclause = new ArrayList<>();
                                    subclause.add(-1 * (((x + (blockX * blockSize)) * boardSize) + ((y   + (blockY * blockSize)) * rowLength) + i));
                                    subclause.add(-1 * (((x + (blockX * blockSize)) * boardSize) + ((y2  + (blockY * blockSize)) * rowLength) + i));
                                    baseClauses.add(subclause);
                                }
                            }
                        }
                    }
                    baseClauses.add(clause);
                }
            }
        }
        return baseClauses;
    }

    /**
     * Calculates the DIMACs clauses that describe the current state of the Sudoku board.
     * @param onlyInitial True if only the initial state of the puzzle should be solved.
     * @return A list of DIMACs clauses describing the current state of the puzzle.
     */
    private ArrayList<ArrayList<Integer>> calcClausesVariables(boolean onlyInitial){
        int boardSize = puzzle.getBoardSize();
        int rowLength = boardSize * boardSize;
        SudokuCell[][] board = puzzle.getBoard();

        ArrayList<ArrayList<Integer>> variableClauses = new ArrayList<>();
        ArrayList<Integer> subclause;

        //Clauses for fixed cells
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                if (board[x][y].getDigit() > 0){
                    if ((!onlyInitial || board[x][y].isInitial())){
                        int fixedClause = (x * boardSize) + (y * rowLength)  + board[x][y].getDigit();
                        subclause = new ArrayList<>();
                        subclause.add(fixedClause);
                        variableClauses.add(subclause);
                    }
                }
            }
        }
        return variableClauses;
    }

    /**
     * Updates the attached Sudoku puzzle using DIMACs output from the solver.
     * @param vars An array of DIMACs variables describing the solved state of the puzzle.
     */
    private void setCellsFromDIMACS(int[] vars) {
        int boardSize = puzzle.getBoardSize();
        int rowLength = boardSize * boardSize;
        SudokuCell[][] board = puzzle.getBoard();
        for (int var : vars) {
            if (var > 0) {
                int x = ((var - 1) % rowLength) / boardSize;
                int y = (var - 1) / rowLength;
                int num = ((var - 1) % boardSize) + 1;
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
}
