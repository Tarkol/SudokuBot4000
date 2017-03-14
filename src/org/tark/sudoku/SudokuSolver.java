package org.tark.sudoku;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.ArrayList;

/**
 * Created by conno on 14/03/2017.
 */
public class SudokuSolver {

    private SudokuPuzzle puzzle;
    private ArrayList<ArrayList<Integer>> clauses;
    private boolean verbose;

    public SudokuSolver(SudokuPuzzle puzzle){
        this.puzzle = puzzle;
        clauses = calcClauses();
        verbose = false;
    }

    public void setVerbosity (boolean verbose) { this.verbose = verbose; }

    /**
     * Attempts to solve the puzzle from its current state.
     *  //TODO remove this
     * @return True if the puzzle has been solved successfully.
     */
    public boolean solve(){
        clauses = calcClauses();
        int[] solution = getSolution();
        if (solution != null && solution.length > 0) {
            setCellsFromDIMACS(solution);
            return true;
        }
        else { return false; }
    }

    /**
     * Gets the DIMACs output for the solved puzzle.
     * @return An array with all the DIMACs variables to form the solution.
     * [NOTE] deal with exceptions in a better way than nulls
     */
    //TODO: Maybe actually do exception handling. Maybe doesn't have to be its own method?
    private int[] getSolution(){
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

    public boolean hasSolution(){
        clauses = calcClauses();
        int[] solution = getSolution();
        if (solution == null || solution.length == 0) { return false; }
        return true;
    }

    /**
     * Checks if the puzzle has a single unique solution. That is, each cell has only one correct value.
     * @return True if there is only one unique solution.
     */
    //TODO: Make this not awful. Use same solver again instead of reinstantiating everything.
    public boolean hasUniqueSolution(){
        clauses = calcClauses(); //TODO I dont like this being here, only good for puzzle generation maybe split clauses into initial + current, then can check if puzzle solvable from vurren tpoint ect
        //First, get the solution for the current puzzle state.
        int[] solution = getSolution();
        if (solution == null || solution.length == 0) { return false; }

        //Add a negation clause using the solution.
        //This means that the solver cannot use the same combination of cell values to solve the puzzle.
        ArrayList<Integer> negationClause = new ArrayList<>();
        for (int clause:solution)
            if (clause > 0) { negationClause.add(clause * -1); }
        clauses.add(negationClause);

        //Try to find another solution using this additional constraint.
        //If no other solution is found then the puzzle solution is unique.
        solution = getSolution();
        if (solution == null || solution.length == 0) { return true; }
        else { return false; }
    }

    @Override
    public String toString(){
        int boardSize = puzzle.getBoardSize();
        ArrayList<ArrayList<Integer>> allClauses = this.calcClauses();
        int numVars = boardSize * boardSize * boardSize;
        String clauseString = "";
        int numClauses = 0;
        for (ArrayList<Integer> subClauses:allClauses) {
            for (Integer clause:subClauses){
                clauseString += clause + " ";
            }
            clauseString += "\n";
            numClauses++;
        }
        String header = String.format("p cnf %d %d\n", numVars, numClauses);
        return header + clauseString;
    }

    //TODO: Bad, doesn't work for 2 digit co-ordinates.
    private ArrayList<ArrayList<Integer>> calcClauses(){
        int boardSize = puzzle.getBoardSize();
        int blockSize = puzzle.getBlockSize();
        SudokuCell[][] board = puzzle.getBoard();
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
                if (board[x][y].getDigit() > 0 && board[x][y].isInitial()){
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
    private void setCellsFromDIMACS(int[] vars) {
        SudokuCell[][] board = puzzle.getBoard();
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
}
