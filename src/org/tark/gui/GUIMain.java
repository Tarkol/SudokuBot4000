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

    private SudokuUIBoard sudokuView;
    private SudokuUIModel sudokuModel;
    private SudokuUIController sudokuController;
    private Menu menu;

    public GUIMain(){
        setTitle("Sudoku Bot 4000");
        sudokuModel = new SudokuUIModel();
        sudokuView = new SudokuUIBoard();
        sudokuController = new SudokuUIController(sudokuView, sudokuModel);
        menu = new Menu(this);
        this.setLayout(new FlowLayout());
        Container contents = getContentPane();
        contents.add(sudokuView);
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
                sudokuModel.loadPuzzle(SudokuPuzzle.generatePuzzle(3));
                sudokuView.refreshBoard();
                break;
            case "solve":
                sudokuModel.solvePuzzle();
                sudokuView.refreshBoard();
                break;
            default: break;
        }
    }

}
