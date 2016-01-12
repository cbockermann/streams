/**
 * 
 */
package streams.tikz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chris
 * 
 */
public class StackedSeries extends ArrayList<Double> {

	/** The unique class ID */
	private static final long serialVersionUID = 7401894906846933603L;

	public String color = "red";
	public final Map<String, String> opts = new HashMap<String, String>();

	public Path toPath() {

		double lastX = 0.0;
		Path p = new Path();
		p.color = color;
		p.opts.putAll(opts);

		if (isEmpty()) {
			return p;
		}
		p.add(new Point(lastX, get(0)));

		for (int i = 0; i < size(); i++) {
			double x = i + 1;
			double y = get(i);

			p.add(new Point(lastX, y));
			p.add(new Point(x, y));
			lastX = x;
		}

		return p;
	}

	public double minimum() {
		double min = 0.0;
		for (Double d : this) {
			min = Math.min(min, d);
		}
		return min;
	}

	public double maximum() {
		double min = 0.0;
		for (Double d : this) {
			min = Math.max(min, d);
		}
		return min;
	}
}
