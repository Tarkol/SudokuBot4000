package org.tark.gui;

import org.tark.sudoku.SudokuCell;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.EventListener;

/** GUI component for displaying a Sudoku puzzle. Scalable for puzzles of (hopefully) any size!
 * Relies on a UI controller to set cell states.
 * Created by conno on 02/11/2016.
 */
class SudokuUIBoard extends JPanel {

    private int boardSize;
    private int blockSize;
    private JTextField[][] cells;
    private JPanel[][] blocks;

    private final Color TEXT_COLOUR_INITIAL = Color.LIGHT_GRAY;
    private final Color TEXT_COLOUR_NORMAL = Color.BLACK;
    private final Color BACK_COLOUR_NORMAL = Color.WHITE;
    private final Color BACK_COLOUR_HIGHLIGHT = Color.ORANGE;

    SudokuUIBoard(int blockSize)
    {
        this.boardSize = blockSize * blockSize;
        this.blockSize = blockSize;
        makeBoard();
    }

    private void makeBoard(){
        GridLayout boardLayout = new GridLayout(blockSize, blockSize, 5, 5);
        this.setLayout(boardLayout);
        this.setBorder(BorderFactory.createEtchedBorder(1));
        cells = new JTextField[boardSize][boardSize];
        blocks = new JPanel[blockSize][blockSize];

        //Make each individual cell
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

        //Put the cells into blocks, add the blocks to the board.
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

    //Might be useful for ex row highlighting?
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
            for (int y = 0; y < blockSize; y ++)
                for (int x = 0; x < blockSize; x++)
                    cellsInBlock[(y * blockSize) + x] = cells[x + (blockX * blockSize)][y + (blockY * blockSize)];

        return cellsInBlock;
    }


    public void setCellValue(int x, int y, SudokuCell cell){
        cells[x][y].setText(cell.toString());
        if (cell.isInitial()) {
            cells[x][y].setForeground(TEXT_COLOUR_INITIAL);
            cells[x][y].setEditable(false);
        }
        else {
            cells[x][y].setForeground(TEXT_COLOUR_NORMAL);
            cells[x][y].setEditable(true);
        }
    }

    //Used to read user input when a cell is filled.
    int getCellValue(int x, int y){
        try {
            return Integer.parseInt(cells[x][y].getText());
        }
        catch (NumberFormatException e){
            return 0;
        }
    }

    void highlightCell(int x, int y){
        cells[x][y].setBackground(BACK_COLOUR_HIGHLIGHT);
    }

    void unhighlightCell(int x, int y){
        cells[x][y].setBackground(BACK_COLOUR_NORMAL);
    }

    //why
    private JPanel getBlockFromXY(int x, int y){
        int blockX = x / blockSize;
        int blockY = y / blockSize;
        return blocks[blockX][blockY];
    }

    void addCellListener(int row, int col, FocusListener cellListener){
        //cells[row][col].addActionListener(cellListener);
        cells[row][col].addFocusListener(cellListener);
    }

}