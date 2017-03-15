package org.tark.util;

/** Simple class for a pair of int values.
 * Created by conno on 15/03/2017.
 */
public class IntPair {

    private final int x;
    private final int y;

    public IntPair(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){ return x; }

    public int getY(){ return y; }
}
