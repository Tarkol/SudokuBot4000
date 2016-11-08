package org.tark.gui;

import org.tark.sudoku.SudokuPuzzle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by conno on 02/11/2016.
 */
//TODO: Actually make a gui.
public class GUIMain extends JFrame implements ActionListener {

    private SudokuUIBoard sudokuUI;
    private Menu menu;

    public GUIMain(){
        setTitle("Sudoku Bot 4000");
        sudokuUI = new SudokuUIBoard(3);
        menu = new Menu(this);
        this.setLayout(new FlowLayout());
        Container contents = getContentPane();
        contents.add(sudokuUI);
        contents.add(menu);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    public static void main(String[] args){
        GUIMain gui = new GUIMain();
        gui.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        String s = e.getActionCommand();

        switch (s){
            case "new":
                sudokuUI.loadPuzzle(SudokuPuzzle.generatePuzzle(3));
                break;
            case "solve":
                sudokuUI.solvePuzzle();
                break;
            default: break;
        }
    }

}
