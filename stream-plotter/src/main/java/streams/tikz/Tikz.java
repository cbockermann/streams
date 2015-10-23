/**
 * 
 */
package streams.tikz;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * @author chris
 * 
 */
public class Tikz {

	public static String format(Double d) {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("0.000", dfs);
		return df.format(d);
	}

	public static String point(double x, double y) {
		return "(" + format(x) + "," + format(y) + ")";
	}

	public static String format(Point p) {
		return point(p);
	}

	public static String point(Point p) {
		return "(" + format(p.x) + "," + format(p.y) + ")";
	}
}
