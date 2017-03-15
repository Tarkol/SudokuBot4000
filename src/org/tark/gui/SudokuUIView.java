package org.tark.gui;

import org.tark.sudoku.SudokuPuzzle;

import javax.swing.*;
import java.awt.*;

/**
 * Created by conno on 15/03/2017.
 */
public class SudokuUIView {

    private JFrame base;
    private SudokuController controller;
    private JPanel main = new JPanel();
    private SudokuBoard board;
    private GUIMenu menu;

    public SudokuUIView(SudokuPuzzle puzzle, JFrame base){
        this.base = base;
        main.setLayout(new FlowLayout());
        board = new SudokuBoard(puzzle.getBlockSize());
        menu = new GUIMenu(this);
        main.add(board);
        main.add(menu);
    }

    public void setController(SudokuController controller){
        this.controller = controller;
        board.setController(controller);
        menu.setController(controller);
    }

    public JComponent getView(){
        return main;
    }

    public SudokuBoard getBoard(){
        return board;
    }

    public void resizeWindow(){
        base.pack();
    }
}
