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

    public String name() {
        return name;
    }

    public Series setName(String s) {
        this.name = s;
        return this;
    }

    public double min() {
        if (isEmpty()) {
            return Double.NaN;
        }
        Double min = null;
        for (int i = 0; i < size(); i++) {
            Double v = get(i);
            if (Double.isNaN(v)) {
                continue;
            }

            if (min == null || v < min) {
                min = v;
            }
        }
        return min;
    }

    public double max() {
        if (isEmpty()) {
            return Double.NaN;
        }
        Double max = null;
        for (int i = 0; i < size(); i++) {
            Double v = get(i);
            if (Double.isNaN(v)) {
                continue;
            }
            if (max == null || v > max) {
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
