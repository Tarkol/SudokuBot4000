package org.tark.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Controls what values can be entered into Sudoku Cell text boxes, only integers less than or equal to the limit are allowed.
 * Created by Tarkol on 06/12/2016.
 */
class SudokuCellLimit extends PlainDocument {

    private int limit;

    SudokuCellLimit(int limit){
        super();
        this.limit = limit;
    }

    public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
        if (str == null) return;

        try {
            String currentValue = getText(0, getLength());
            int newValue = Integer.parseInt(currentValue + str);
            if (newValue > 0 && newValue <= limit){
                super.insertString(offset, str, attr);
            }
        }
        catch (Exception e) {
            return;
        }
    }
}
