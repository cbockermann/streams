/**
 * 
 */
package streams.tikz;

import java.util.ArrayList;

/**
 * @author chris
 * 
 */
public class Series extends ArrayList<Double> {

    /** The unique class ID */
    private static final long serialVersionUID = 309999034823329563L;

    String name;

    public Series() {
        this.name = null;
    }

    public Series(String name) {
        this.name = name;
    }

    public double min() {
        if (isEmpty()) {
            return Double.NaN;
        }
        Double min = get(0);
        for (int i = 1; i < size(); i++) {
            Double v = get(i);
            if (v < min) {
                min = v;
            }
        }
        return min;
    }

    public double max() {
        if (isEmpty()) {
            return Double.NaN;
        }
        Double max = get(0);
        for (int i = 1; i < size(); i++) {
            Double v = get(i);
            if (v > max) {
                max = v;
            }
        }
        return max;
    }

    public Path toPath() {
        Path path = new Path();
        for (int idx = 0; idx < size(); idx++) {
            path.add(new Point(idx * 1.0, get(idx)));
        }
        return path;
    }
}
