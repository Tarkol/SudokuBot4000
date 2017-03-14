package org.tark.gui;

import org.tark.sudoku.SudokuPuzzle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main GUI frame for the program. Connects the options menu and the puzzle grid into one window.
 * Created by conno on 02/11/2016.
 */
public class GUIMain extends JFrame implements ActionListener {

    private SudokuUIController sudokuController;
    private final int DEFAULT_BLOCK_SIZE = 3;

    private GUIMain(){
        setTitle("Sudoku Bot 4000");
        SudokuPuzzle sudokuModel = new SudokuPuzzle(DEFAULT_BLOCK_SIZE); //Default puzzle size is 3
        SudokuUIBoard sudokuView = new SudokuUIBoard(DEFAULT_BLOCK_SIZE);
        sudokuController = new SudokuUIController(sudokuView, sudokuModel);
        Menu menu = new Menu(this);
        this.setLayout(new FlowLayout());
        Container contents = getContentPane();
        contents.add(sudokuView);
        contents.add(menu);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
                sudokuController.generatePuzzle();
                break;
            case "solve":
                sudokuController.solvePuzzle();
                break;
            default: break;
        }
    }

}
