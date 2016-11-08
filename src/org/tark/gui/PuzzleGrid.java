package org.tark.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by conno on 02/11/2016.
 */
public class PuzzleGrid extends JPanel {

    public PuzzleGrid(int size){
        GridLayout layout = new GridLayout(size, size, 0,0 ); //fixed size change at some point
        this.setLayout(layout);
        this.setSize(size*size,size*size);
        //JLabel err = new JLabel("hmm");
        //err.setBorder(BorderFactory.createEtchedBorder(1));

        for (int i = 0; i < size * size; i++) {
            JLabel lbl = new JLabel("0");
            lbl.setSize(100, 100);
            lbl.setBorder(BorderFactory.createEtchedBorder(1));
            lbl.setFont(new Font("Sans Serif", Font.PLAIN, 32));
            this.add(lbl);
        }
    }
}
