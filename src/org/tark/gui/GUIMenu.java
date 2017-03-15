package org.tark.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The options pane to the side of the Sudoku board.
 * Created by conno on 15/03/2017.
 */
public class GUIMenu extends JPanel{

    private SudokuController controller;

    GUIMenu(SudokuUIView parent){
        this.setLayout(new GridLayout(10, 1));
        JButton btnNew = new JButton("New Puzzle");
        btnNew.addActionListener(e -> controller.generatePuzzle());

        JButton btnSolve = new JButton("Solve Puzzle");
        btnSolve.addActionListener(e -> controller.solve(false));

        JButton btnCheck = new JButton("Check Solution");
        btnCheck.addActionListener(e -> controller.checkSolution());

        JButton btnShow = new JButton("Show Solution");
        btnShow.addActionListener(e -> controller.solve(true) );

        this.add(btnNew);
        this.add(btnSolve);
        this.add(btnCheck);
        this.add(btnShow);

        SpinnerNumberModel modelBlockSizeX = new SpinnerNumberModel(3, 1, 6, 1);
        JSpinner spinSizeX = new JSpinner(modelBlockSizeX);

        spinSizeX.addChangeListener(e -> {
            int newBlockSize = (Integer)spinSizeX.getModel().getValue();
            controller.changeBlockSize(newBlockSize);
            parent.resizeWindow();
        });

        this.add(new JLabel("Board Size"));
        this.add(spinSizeX);

        // SpinnerNumberModel modelBlockSizeY = new SpinnerNumberModel(3, 1, 6, 1);
        // JSpinner spinSizeY = new JSpinner(modelBlockSizeY);
    }

    public void setController(SudokuController controller){
        this.controller = controller;
    }
}
