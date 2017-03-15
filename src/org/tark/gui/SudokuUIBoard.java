package org.tark.gui;

import org.tark.sudoku.SudokuCell;
import org.tark.util.IntPair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

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
    private final Color BACK_COLOUR_GOOD = new Color(170, 255, 150);
    private final Color BACK_COLOUR_CONFLICT = new Color(255, 100, 100);

    SudokuUIBoard(int blockSize) {
        this.boardSize = blockSize * blockSize;
        this.blockSize = blockSize;
        makeBoard();
    }

    void changeBlockSize(int blockSize) {
        clearBoard();
        this.blockSize = blockSize;
        boardSize = blockSize * blockSize;
        makeBoard();
        revalidate();
    }

    private void clearBoard() {
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                //this.remove(cells[x][y]);
            }
        }
        for (int y = 0; y < blockSize; y++) {
            for (int x = 0; x < blockSize; x++) {
                this.remove(blocks[x][y]);
            }
        }
    }

    private void makeBoard() {
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
                cell.addFocusListener(new CellFocusListener(x, y));
                cells[x][y] = cell;
            }
        }

        //Put the cells into blocks, add the blocks to the board.
        for (int y = 0; y < blockSize; y++) {
            for (int x = 0; x < blockSize; x++) {
                GridLayout blockLayout = new GridLayout(blockSize, blockSize, 0, 0);
                JPanel block = new JPanel(blockLayout);
                blocks[x][y] = block;
                for (JTextField cell : getCellsInBlock(x, y)) {
                    blocks[x][y].add(cell);
                }
                this.add(blocks[x][y]);
            }
        }
    }

    //Might be useful for ex row highlighting?
    private JTextField[] getCellsInRow(int y) {
        JTextField[] cellsInRow = new JTextField[boardSize];
        for (int x = 0; x < boardSize; x++)
            cellsInRow[x] = cells[x][y];

        return cellsInRow;
    }

    private JTextField[] getCellsInColumn(int x) {
        JTextField[] cellsInColumn = new JTextField[boardSize];
        for (int y = 0; y < boardSize; y++)
            cellsInColumn[x] = cells[x][y];

        return cellsInColumn;
    }

    private JTextField[] getCellsInBlock(int blockX, int blockY) {
        JTextField[] cellsInBlock = new JTextField[boardSize];
        for (int y = 0; y < blockSize; y++)
            for (int x = 0; x < blockSize; x++)
                cellsInBlock[(y * blockSize) + x] = cells[x + (blockX * blockSize)][y + (blockY * blockSize)];

        return cellsInBlock;
    }


    void setCellValue(int x, int y, SudokuCell cell) {
        cells[x][y].setText(cell.toString());
        if (cell.isInitial()) {
            cells[x][y].setForeground(TEXT_COLOUR_INITIAL);
            cells[x][y].setEditable(false);
        } else {
            cells[x][y].setForeground(TEXT_COLOUR_NORMAL);
            cells[x][y].setEditable(true);
        }
    }

    //Used to read user input when a cell is filled.
    int getCellValue(int x, int y) {
        try {
            return Integer.parseInt(cells[x][y].getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void highlightCell(int x, int y) {
        cells[x][y].setBackground(BACK_COLOUR_HIGHLIGHT);
    }

    private void unhighlightCell(int x, int y) {
        cells[x][y].setBackground(BACK_COLOUR_NORMAL);
    }

    void showNoConflicts(){
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                if (!cells[x][y].getText().equals(""))
                  cells[x][y].setBackground(BACK_COLOUR_GOOD);
            }
        }
    }

    void showConflicts(ArrayList<IntPair> conflicts){
        for (IntPair cell : conflicts){
            cells[cell.getX()][cell.getY()].setBackground(BACK_COLOUR_CONFLICT);
        }
    }

    //why
    private JPanel getBlockFromXY(int x, int y) {
        int blockX = x / blockSize;
        int blockY = y / blockSize;
        return blocks[blockX][blockY];
    }

    void addCellKeyListener(int row, int col, KeyListener cellListener) {
        cells[row][col].addKeyListener(cellListener);
    }

    /**
     * Small focus listener used to highlight the currently selected cell.
     */
    private class CellFocusListener implements FocusListener {

        private final int x;
        private final int y;

        CellFocusListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void focusGained(FocusEvent e) {
            for (int y = 0; y < boardSize; y++)
                for (int x = 0; x < boardSize; x++)
                    unhighlightCell(x, y);
            highlightCell(x, y);
        }

        @Override
        public void focusLost(FocusEvent e) {
            unhighlightCell(x, y);
        }
    }
}