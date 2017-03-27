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

    public GUIMenu(SudokuUIView parent){
        this.setLayout(new GridLayout(10, 1));
        JButton btnNew = new JButton("New Puzzle");         //Generate a new puzzle for the current board size.
        btnNew.addActionListener(e -> controller.generatePuzzle());

        JButton btnSolve = new JButton("Solve Puzzle");     //Tries to solve the puzzle from its current state. Can fail.
        btnSolve.addActionListener(e -> controller.solve(false));

        JButton btnCheck = new JButton("Check Solution");   //Checks cells for rule conflicts, like two matching numbers in a row.
        btnCheck.addActionListener(e -> controller.checkSolution());

        JButton btnShow = new JButton("Show Solution");     //Displays the correct solution for the puzzle by solving from initial state.
        btnShow.addActionListener(e -> controller.solve(true) );

        JButton btnHint = new JButton("Get hint");          //Readies the controller to reveal a hint for the board.
        btnHint.addActionListener(e -> controller.requestHint());

        JButton btnPrint = new JButton("Print Clauses");     //Prints the caluses that represent the puzzle.
        btnPrint.addActionListener(e -> controller.printClauses());

        this.add(btnNew);
        this.add(btnSolve);
        this.add(btnCheck);
        this.add(btnShow);
        this.add(btnHint);
        this.add(btnPrint);

        SpinnerNumberModel modelBlockSizeX = new SpinnerNumberModel(3, 1, 6, 1);
        JSpinner spinSizeX = new JSpinner(modelBlockSizeX);

        spinSizeX.addChangeListener(e -> {
            int newBlockSize = (Integer)spinSizeX.getModel().getValue();
            controller.changeBlockSize(newBlockSize);
            parent.resizeWindow();
        });

        this.add(new JLabel("Board Size"));
        this.add(spinSizeX);

        // TODO Implement non-square blocks.
        // SpinnerNumberModel modelBlockSizeY = new SpinnerNumberModel(3, 1, 6, 1);
        // JSpinner spinSizeY = new JSpinner(modelBlockSizeY);
    }

    /**
     * Sets the controller for this user interface. Control calls from menu components will be made to this controller.
     * @param controller The Sudoku controller that will manage this user interface.
     */
    public void setController(SudokuController controller){
        this.controller = controller;
    }
}
