package org.tark.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by conno on 02/11/2016.
 */
//TODO: Actually make a gui.
public class GUIMain extends JFrame {

    public GUIMain(){
        setTitle("Sudoku Bot 400000");
        Container contents = getContentPane();
        contents.add(new PuzzleGrid(9));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    public static void main(String[] args){
        GUIMain gui = new GUIMain();
        gui.setVisible(true);
    }

}
