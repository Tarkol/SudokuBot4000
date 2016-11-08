package org.tark.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Tarkol on 08/11/2016.
 */
class Menu extends JPanel {

    private JButton btnNew;
    private JButton btnSolve;

    public Menu(ActionListener l){
        btnNew = new JButton("New Puzzle");
        btnNew.setActionCommand("new");
        btnNew.addActionListener(l);
        this.add(btnNew);

        btnSolve = new JButton("Solve Puzzle");
        btnSolve.setActionCommand("solve");
        btnSolve.addActionListener(l);
        this.add(btnSolve);
    }
}
