package org.tark;

/**
 * Created by conno on 27/10/2016.
 */
public class SudokuCell {

    private int digit;
    private boolean initial;

    public SudokuCell(int digit, boolean initial){
        this.digit = digit;
        this.initial = initial;
    }

    public boolean setDigit(int digit){
        if (this.initial)
            return false;
        this.digit = digit;
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
        return String.format("Digit: %d\n Initial: %d", this.digit, this.initial);
    }
}
