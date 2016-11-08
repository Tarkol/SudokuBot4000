package org.tark.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by Tarkol on 08/11/2016.
 */
public class SudokuBlock extends JPanel {

    private JTextField[] cells;
    private int blockSize;

    public SudokuBlock(int blockSize) {
        this.blockSize = blockSize;
        GridLayout blockLayout = new GridLayout(blockSize, blockSize, 0, 0);
        this.setLayout(blockLayout);
        cells = new JTextField[blockSize * blockSize];


        for (int i = 0; i < blockSize * blockSize; i++) {
            cells[i] = makeSudokuCell();
            this.add(cells[i]);
        }
    }

    private JTextField getCellFromXY(int x, int y){
        int cellX = x % blockSize;
        int cellY = y % blockSize;
        int cellID = (blockSize * cellY) + cellX;
        return cells[cellID];
    }

    public void setCellValue(int x, int y, int value){
        getCellFromXY(x, y).setText(Integer.toString(value));
    }

    private JTextField makeSudokuCell(){
        JTextField cell = new JTextField("0");
        cell.setHorizontalAlignment(SwingConstants.CENTER);
        cell.setBackground(Color.WHITE);
        cell.setBorder(BorderFactory.createEtchedBorder());
        cell.setFont(new Font("Sans Serif", Font.PLAIN, 32));
        cell.setPreferredSize(new Dimension(50, 50));

        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SudokuBoard parent = (SudokuBoard)getParent();
            }
        };
        cell.addActionListener(action);
        return cell;
    }

}
