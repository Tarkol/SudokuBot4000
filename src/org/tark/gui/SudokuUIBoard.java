package org.tark.gui;

import org.tark.sudoku.SudokuCell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by conno on 02/11/2016.
 */
public class SudokuUIBoard extends JPanel {

    //private SudokuUIModel model;
    private int boardSize;
    private int blockSize;
    private JTextField[][] cells;
    private JPanel[][] blocks;

    private final Color COLOUR_INITIAL = Color.LIGHT_GRAY;
    private final Color COLOUR_NORMAL = Color.BLACK;

    SudokuUIBoard(){
        this.model = model;
        this.boardSize = model.getBoardSize();
        this.blockSize = model.getBlockSize();
        GridLayout boardLayout = new GridLayout(blockSize, blockSize, 5, 5);
        this.setLayout(boardLayout);
        this.setBorder(BorderFactory.createEtchedBorder(1));
        makeBoard();
        refreshBoard();
    }

    private void makeBoard(){
        cells = new JTextField[boardSize][boardSize];
        blocks = new JPanel[blockSize][blockSize];
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                JTextField cell = new JTextField("0");
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setBackground(Color.WHITE);
                cell.setBorder(BorderFactory.createEtchedBorder());
                cell.setFont(new Font("Sans Serif", Font.PLAIN, 32));
                cell.setPreferredSize(new Dimension(50, 50));
                cell.setDocument(new SudokuCellLimit(boardSize));
                cells[x][y] = cell;
            }
        }

        for (int y = 0; y < blockSize; y++) {
            for (int x = 0; x < blockSize; x++) {
                GridLayout blockLayout = new GridLayout(blockSize, blockSize, 0, 0);
                JPanel block = new JPanel(blockLayout);
                blocks[x][y] = block;
                for (JTextField cell:getCellsInBlock(x, y)){
                    blocks[x][y].add(cell);
                }
                this.add(blocks[x][y]);
            }
        }
    }

    private JTextField[] getCellsInRow(int y){
        JTextField[] cellsInRow = new JTextField[boardSize];
        for (int x = 0; x < boardSize; x ++)
            cellsInRow[x] = cells[x][y];

        return cellsInRow;
    }

    private JTextField[] getCellsInColumn(int x){
        JTextField[] cellsInColumn = new JTextField[boardSize];
        for (int y = 0; y < boardSize; y++)
            cellsInColumn[x] = cells[x][y];

        return cellsInColumn;
    }

    private JTextField[] getCellsInBlock(int blockX, int blockY){
        JTextField[] cellsInBlock = new JTextField[boardSize];
            for (int x = 0; x < blockSize; x ++)
                for (int y = 0; y < blockSize; y++)
                    cellsInBlock[(x * blockSize) + y] = cells[x + (blockX * blockSize)][y + (blockY * blockSize)];

        return cellsInBlock;
    }

    public int getCellValue(int x, int y){
        return Integer.parseInt(cells[x][y].getText());
    }

    public void setCellValue(int x, int y, int value){
        SudokuCell cell = model.getCell(x, y);
        if (cell.setDigit(value, false)){
            cells[x][y].setText(Integer.toString(cell.getDigit()));
            //check??
        }
    }

    void refreshBoard(){
        for (int y = 0; y < boardSize; y ++) {
            for (int x = 0; x <boardSize; x++) {
                SudokuCell cell = model.getCell(x, y);
                JTextField cellUI = cells[x][y];
                int cellValue = cell.getDigit();
                cellUI.setText(cellValue == 0 ? "" : Integer.toString(cellValue));
                if (cell.isInitial()) {
                    cellUI.setForeground(COLOUR_INITIAL);
                    cellUI.setEditable(false);
                }
                else {
                    cellUI.setForeground(COLOUR_NORMAL);
                    cellUI.setEditable(true);
                }
            }
        }
    }

    void loadPuzzle(){
        //makeBoard();
        refreshBoard();
    }

    public void check(){

    }

    private JPanel getBlockFromXY(int x, int y){
        int blockX = x / blockSize;
        int blockY = y / blockSize;
        return blocks[blockX][blockY];
    }

    public void addCellListener(int row, int col, ActionListener cellListener){
        cells[row][col].addActionListener(cellListener);
    }

}