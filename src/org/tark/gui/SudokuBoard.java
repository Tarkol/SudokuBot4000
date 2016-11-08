package org.tark.gui;

import org.tark.sudoku.SudokuCell;
import org.tark.sudoku.SudokuPuzzle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by conno on 02/11/2016.
 */
public class SudokuBoard extends JPanel {

    private JTextField[] cells;
    private SudokuBlock[] blocks;
    private SudokuPuzzle puzzle;

    public SudokuBoard(SudokuPuzzle puzzle){
        this.puzzle = puzzle;
        int blockSize = puzzle.getBlockSize();
        blocks = new SudokuBlock[blockSize * blockSize];
        GridLayout boardLayout = new GridLayout(blockSize, blockSize, 5, 5);
        this.setLayout(boardLayout);
        this.setBorder(BorderFactory.createEtchedBorder(1));

        for (int i = 0; i < blockSize * blockSize; i++) {
            blocks[i] = new SudokuBlock(blockSize);
            this.add(blocks[i]);
        }
        updateCell(3, 3, 2);
    }

    private JPanel makeBlock(){
        int blockSize = puzzle.getBlockSize();
        GridLayout blockLayout = new GridLayout(blockSize, blockSize, 0, 0);
        JPanel block = new JPanel(blockLayout);

        //cells = new JTextField[blockSize * blockSize];
       // for (int i = 0; i < blockSize * blockSize; i++) {
        //    cells[i] = makeSudokuCell();
       //     this.add(cells[i]);
       //}

        return block;
    }

    public void updateCell(int x, int y, int value){
        SudokuCell cell = puzzle.getCell(x, y);
        if (cell.setDigit(value, false)){
            getBlockFromXY(x, y).setCellValue(x, y, value);
            //check??
        }
    }

    public void loadPuzzle(){

    }

    public void solvePuzzle(){
        puzzle.solve(false);
        //update all cells
    }

    public void check(){

    }

    private SudokuBlock getBlockFromXY(int x, int y){
        int boardX = x / puzzle.getBlockSize();
        int boardY = y / puzzle.getBlockSize();
        int blockID = (puzzle.getBlockSize() * boardY) + boardX;
        return blocks[blockID];
    }


}