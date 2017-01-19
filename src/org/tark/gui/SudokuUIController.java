package org.tark.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller for Sudoku UI, listens for changes in text boxes and passes the information to the model.
 * Created by Tarkol on 06/12/2016.
 */
public class SudokuUIController{

    private SudokuUIBoard view;
    private SudokuUIModel model;

    public SudokuUIController(SudokuUIBoard sudokuView, SudokuUIModel sudokuModel){
        this.view = sudokuView;
        this.model = sudokuModel;

        //bad? fixed size
        for (int y = 0; y <9; y++) {
            for (int x = 0; x < 9; x++) {
                this.view.addCellListener(x, y, new CellListener(x, y));
            }
        }
    }

    private class CellListener implements ActionListener{

        private int row;
        private int col;

        public CellListener(int row, int col){
            super();
            this.row = row;
            this.col = col;
        }

        public void actionPerformed(ActionEvent e) {
            model.setCell(row, col, view.getCellValue(row, col));
        }
    }
}
