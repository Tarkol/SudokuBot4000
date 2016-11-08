package org.tark.gui;

import org.tark.sudoku.SudokuPuzzle;

import javax.swing.*;
import java.awt.*;

/**
 * Created by conno on 02/11/2016.
 */
//TODO: Actually make a gui.
public class GUIMain extends JFrame {

    public GUIMain(){
        setTitle("Sudoku Bot 400000");
        this.setLayout(new FlowLayout());
        Container contents = getContentPane();
        contents.add(new SudokuBoard(new SudokuPuzzle(3)));
        contents.add(new Menu());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    public static void main(String[] args){
        GUIMain gui = new GUIMain();
        gui.setVisible(true);
    }

}
