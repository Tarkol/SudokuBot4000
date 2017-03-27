package org.tark.gui;

/**
 * State used by the board model to broadcast changes.
 * //Unused
 * Created by conno on 27/03/2017.
 */
enum SudokuState {
    NONE("None"), CLEAR("Clear board"), CELL("Cell change");

    private String text;

    private SudokuState(String text){
        this.text = text;
    }

    public String getText(){ return text; }

    @Override
    public String toString(){ return text; }
}