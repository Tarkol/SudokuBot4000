package org.tark.gui;

import javax.swing.*;

/**
 * Created by conno on 02/11/2016.
 */
public class um {
    private JPanel panel1;
    private PuzzleGrid puzzleGrid1;

    public static void main(String[] args) {
        JFrame frame = new JFrame("um");
        frame.setContentPane(new um().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        puzzleGrid1 = new PuzzleGrid(9);
    }


}
