package org.tark.sudoku;

/**
 * Created by Tarkol on 27/10/2016.
 * A single cell on a Sudoku board. Might be unnecessary.
 */
public class SudokuCell {

    private int digit;
    private boolean initial;

    public SudokuCell(int digit, boolean initial){
        this.digit = digit;
        this.initial = initial;
    }

    public boolean setDigit(int digit, boolean initial){
        if (this.initial)
            return false;
        this.digit = digit;
        this.initial = initial;
        return true;
    }

    public int getDigit(){
        return this.digit;
    }

    public boolean isInitial(){
        return this.initial;
    }

    @Override
    public String toString(){
        return (digit == 0) ? " " : Integer.toString(digit);
    }
}
