/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.gui;

/**
 * A struct holding two doubles between 0 and 1, indicating a position in a 2D
 * plane.
 * 
 * @author torsteins
 */
public class DoublePoint {
    public double x;
    public double y;
    
    /**
     * Will set the double to a number between
     * @param x
     * @param y 
     */
    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DoublePoint)) return false;
        DoublePoint that = (DoublePoint) obj;
        return (this.x == that.x && this.y == that.y);
    }
}
