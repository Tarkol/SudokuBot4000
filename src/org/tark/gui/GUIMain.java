package org.tark.gui;

import org.tark.sudoku.SudokuPuzzle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main GUI frame for the program. Connects the options menu and the puzzle grid into one window.
 * Created by conno on 02/11/2016.
 */
public class GUIMain {

    private static void makeUI(){
        JFrame baseFrame = new JFrame("SudokuBot 4000");

        SudokuPuzzle sudokuModel = new SudokuPuzzle(3); //Default puzzle size is 3
        SudokuUIView sudokuView = new SudokuUIView(sudokuModel, baseFrame);
        SudokuController sudokuController = new SudokuController(sudokuView, sudokuModel);
        sudokuView.setController(sudokuController);

        baseFrame.getContentPane().add(sudokuView.getView());
        baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        baseFrame.pack();
        baseFrame.setLocationRelativeTo(null);
        baseFrame.setVisible(true);
    }

    public static void main(String[] args){
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                makeUI();
            }
        });
    }
}
