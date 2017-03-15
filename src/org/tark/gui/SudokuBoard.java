package org.tark.gui;

import org.tark.sudoku.SudokuCell;
import org.tark.util.IntPair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/** GUI component for displaying a Sudoku puzzle board. Scalable for puzzles of (hopefully) any size!
 * Relies on a UI controller to set cell states and handle user actions involving the puzzle data.
 * Created by conno on 02/11/2016.
 */
class SudokuBoard extends JPanel {

    private SudokuController controller;
    private int boardSize;
    private int blockSize;
    private JTextField[][] cells;
    private JPanel[][] blocks;

    private final Color TEXT_COLOUR_INITIAL = Color.LIGHT_GRAY;
    private final Color TEXT_COLOUR_NORMAL = Color.BLACK;
    private final Color BACK_COLOUR_NORMAL = Color.WHITE;
    private final Color BACK_COLOUR_HIGHLIGHT = Color.ORANGE;
    private final Color BACK_COLOUR_SELECT = Color.MAGENTA;
    private final Color BACK_COLOUR_GOOD = new Color(170, 255, 150);
    private final Color BACK_COLOUR_CONFLICT = new Color(255, 100, 100);

    SudokuBoard(int blockSize) {
        this.boardSize = blockSize * blockSize;
        this.blockSize = blockSize;
        makeBoard();
    }

    /**
     * Change the block size of the board.
     * This will clear the current puzzle progress and make a new board of the given size.
     * @param blockSize The block size of the new puzzle board.
     */
    void changeBlockSize(int blockSize) {
        clearBoard();
        this.blockSize = blockSize;
        boardSize = blockSize * blockSize;
        makeBoard();
        revalidate();
    }

    /**
     * Removes all cells and blocks from the board.
     * Used when recreating a board, eg for a new board size.
     */
    private void clearBoard() {
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                this.remove(cells[x][y]);
            }
        }
        for (int y = 0; y < blockSize; y++) {
            for (int x = 0; x < blockSize; x++) {
                this.remove(blocks[x][y]);
            }
        }
    }

    /**
     * Instantiates all of the cells of the board and sorts them into appropriate blocks.
     */
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
                cell.setFont(new Font("Sans Serif", Font.PLAIN, 20));
                cell.setPreferredSize(new Dimension(30, 30));
                cell.setDocument(new SudokuCellLimit(boardSize));
                cell.addKeyListener(new CellKeyAdapter(x, y));
                cell.addFocusListener(new CellFocusListener(x, y));
                cell.addMouseListener(new CellMouseAdapter(x, y));
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

    //

    /**
     * Gets all cells in the given row.
     * Might be useful for ex row highlighting?
     * @param y The row id to retrieve
     * @return The text box objects for each cell in the row.
     */
    private JTextField[] getCellsInRow(int y) {
        JTextField[] cellsInRow = new JTextField[boardSize];
        for (int x = 0; x < boardSize; x++)
            cellsInRow[x] = cells[x][y];

        return cellsInRow;
    }

    //As above but columns.
    /**
     * Gets all cells in the given column.
     * Might be useful for ex column highlighting?
     * @param x The column id to retrieve
     * @return The text box objects for each cell in the column.
     */
    private JTextField[] getCellsInColumn(int x) {
        JTextField[] cellsInColumn = new JTextField[boardSize];
        for (int y = 0; y < boardSize; y++)
            cellsInColumn[x] = cells[x][y];

        return cellsInColumn;
    }

    /**
     * Gets all cells in the given block.
     * @param blockX The X block location to retrieve.
     * @param blockY The X blocY location to retrieve
     * @return The text box objects for each cell in the block.
     */
    private JTextField[] getCellsInBlock(int blockX, int blockY) {
        JTextField[] cellsInBlock = new JTextField[boardSize];
        for (int y = 0; y < blockSize; y++)
            for (int x = 0; x < blockSize; x++)
                cellsInBlock[(y * blockSize) + x] = cells[x + (blockX * blockSize)][y + (blockY * blockSize)];

        return cellsInBlock;
    }

    /**
     * Sets the value of a text box from a Cell object.
     * @param x X location of the cell.
     * @param y Y location of the cell.
     * @param cell The cell to display on the board in the given location.
     */
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

    /**
     * Gets the int value of a cell from the board.
     * @param x X location of the cell.
     * @param y Y location of the cell.
     * @return The integer value of the cell.
     */
    private int getCellValue(int x, int y) {
        try {
            return Integer.parseInt(cells[x][y].getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Sets a cell to have a highlight colour. Highlight colour differs based on situation.
     * @param x X location of the cell to highlight.
     * @param y Y location of the cell to highlight.
     */
    private void highlightCell(int x, int y) {
        if (controller.getHintStatus())
            cells[x][y].setBackground(BACK_COLOUR_SELECT);
        else
            cells[x][y].setBackground(BACK_COLOUR_HIGHLIGHT);
    }

    /**
     * Uhighlights a cell, estoring the normal background colour.
     * @param x X location of the cell to unhighlight.
     * @param y Y location of the cell to unhighlight.
     */
    private void unhighlightCell(int x, int y) {
        cells[x][y].setBackground(BACK_COLOUR_NORMAL);
    }

    /**
     * Shows that no conflicts exist by highlighting all valid cell locations.
     * //TODO more flexible highlight rules
     */
    void showNoConflicts(){
        for (int y = 0; y < boardSize; y++){
            for (int x = 0; x < boardSize; x++){
                if (!cells[x][y].getText().equals(""))
                  cells[x][y].setBackground(BACK_COLOUR_GOOD);
            }
        }
    }

    /**
     * Shows that conflicts exist by highlighting cells with conflicting values.
     * @param conflicts A list of cell locations that have conflicting values.
     */
    void showConflicts(ArrayList<IntPair> conflicts){
        for (IntPair cell : conflicts){
            cells[cell.getX()][cell.getY()].setBackground(BACK_COLOUR_CONFLICT);
        }
    }

    //Might have use in highlighting blocks, unused right now.
    private JPanel getBlockFromXY(int x, int y) {
        int blockX = x / blockSize;
        int blockY = y / blockSize;
        return blocks[blockX][blockY];
    }

    /**
     * Sets the controller that manages this view. Control calls from event listeners will be made to this object.
     * @param controller The Sudoku controller that manages this view.
     */
    public void setController(SudokuController controller){
        this.controller = controller;
    }

    /*
        Listeners for interacting with cells on the board.
     */

    /**
     * Key adapter to request puzzle model update on user input.
     */
    private class CellKeyAdapter extends KeyAdapter {

        private final int x;
        private final int y;

        CellKeyAdapter(int x, int y){
            this.x = x;
            this.y = y;
        }

        public void keyReleased(KeyEvent e) {
            controller.setCell(getCellValue(x, y),x,y);
        }
    }

    /**
     * Focus listener used to highlight the currently focused cell.
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

    /**
     * Mouse listener to set cell focus on mouseover.
     */
    private class CellMouseAdapter extends MouseAdapter {

        private final int x;
        private final int y;

        CellMouseAdapter(int x, int y){
            this.x = x;
            this.y = y;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            cells[x][y].requestFocus();
        }

        @Override
        public void mouseClicked(MouseEvent e){
            if (controller.getHintStatus()){
                controller.getHintForCell(x, y);
            }
        }
    }
}