package org.tark.gui;

import org.tark.sudoku.SudokuPuzzle;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main GUI frame for the program. Connects the options menu and the puzzle grid into one window.
 * Created by conno on 02/11/2016.
 */
public class GUIMain extends JFrame implements ActionListener {

    private enum Actions {
        GENERATE_PUZZLE,
        SOLVE_PUZZLE,
        CHECK_SOLUTION,
        SHOW_SOLUTION
    }

    private SudokuUIController sudokuController;
    private int puzzleSize = 3;

    private GUIMain(){
        setTitle("Sudoku Bot 4000");
        SudokuPuzzle sudokuModel = new SudokuPuzzle(puzzleSize); //Default puzzle size is 3
        SudokuUIBoard sudokuView = new SudokuUIBoard(puzzleSize);
        sudokuController = new SudokuUIController(sudokuView, sudokuModel);

        Container contents = getContentPane();
        this.add(sudokuView);

        JPanel menuPane = new JPanel(new GridLayout(10, 1));
        this.add(menuPane);


        JButton btnNew = new JButton("New Puzzle");
        btnNew.setActionCommand(Actions.GENERATE_PUZZLE.name());
        btnNew.addActionListener(this);
        menuPane.add(btnNew);

        JButton btnSolve = new JButton("Solve Puzzle");
        btnSolve.setActionCommand(Actions.SOLVE_PUZZLE.name());
        btnSolve.addActionListener(this);
        menuPane.add(btnSolve);

        JButton btnCheck = new JButton("Check Solution");
        btnCheck.setActionCommand(Actions.CHECK_SOLUTION.name());
        btnCheck.addActionListener((this));
        menuPane.add(btnCheck);

        JButton btnShow = new JButton("Show Solution");
        btnShow.setActionCommand(Actions.SHOW_SOLUTION.name());
        btnShow.addActionListener((this));
        menuPane.add(btnShow);

        SpinnerNumberModel modelBlockSizeX = new SpinnerNumberModel(3, 1, 6, 1);
        // SpinnerNumberModel modelBlockSizeY = new SpinnerNumberModel(3, 1, 6, 1);
        JSpinner spinSizeX = new JSpinner(modelBlockSizeX);
        // JSpinner spinSizeY = new JSpinner(modelBlockSizeY);

        spinSizeX.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                puzzleSize = (Integer)spinSizeX.getModel().getValue();
                sudokuController.changeBoardSize(puzzleSize);
                pack();
            }
        });

        menuPane.add(new JLabel("Board Size"));
        menuPane.add(spinSizeX);

        //this.add(spinSizeY);
        this.setLayout(new FlowLayout());

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

        switch (s) {
            case "GENERATE_PUZZLE":
                sudokuController.generatePuzzle();
                break;
            case "SOLVE_PUZZLE":
                sudokuController.solve(false);
                break;
            case "CHECK_SOLUTION":
                sudokuController.checkSolution();
                break;
            case "SHOW_SOLUTION":
                sudokuController.solve(true);
                break;
        }
    }
}
