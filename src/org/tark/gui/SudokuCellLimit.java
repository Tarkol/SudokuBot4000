package org.tark.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Controls what values can be entered into Sudoku Cell text boxes, only integers less than or equal to the limit
 * are allowed, where the limit should be the highest number allowed in the puzzle.
 * Created by Tarkol on 06/12/2016.
 */
class SudokuCellLimit extends PlainDocument {

    private int limit;

    SudokuCellLimit(int limit){
        super();
        this.limit = limit;
    }

    public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
        if (str == null || str.equals("")) return;

        try {
            String currentValue = getText(0, getLength());
            int newValue = Integer.parseInt(currentValue + str);
            if (newValue > 0 && newValue <= limit){
                super.insertString(offset, str, attr);
            }
        }
        catch (Exception e) {
            System.err.println("Attempted to write to an invalid location" + e.getMessage());
        }
    }
}
