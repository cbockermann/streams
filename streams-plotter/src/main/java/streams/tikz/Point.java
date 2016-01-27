/**
 * 
 */
package streams.tikz;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Point {
    public final Double x;
    public final Double y;
    public Double energy = 1.0;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Point p) {
        return Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
    }

    public String toString() {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.000", dfs);
        return "(" + df.format(x) + "," + df.format(y) + ")";
    }

    public Point shift(double x, double y) {
        return new Point(this.x + x, this.y + y);
    }
}