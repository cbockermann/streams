/**
 * 
 */
package stream.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a simple interval and can be instantiated from a string
 * in the format &quot;x,y&quot; where <code>x</code> and <code>y</code> are
 * numbers. By default, the boundaries are included in the interval, i.e. for
 * the interval <code>30,40</code> a value of <code>30.0</code> in included.
 * 
 * The interval can additionally be specified with inclusion/exclusion brackets.
 * A left-open interval is specified as <code>(30,40]</code> or
 * <code>]30,40]</code>, whereas the strings <code>]30,40[</code> and
 * <code>(30,40)</code> both specify a fully open interval that does <i>not</i>
 * include both its boundary values.
 * 
 * @author Christian Bockermann
 *
 */
public class Interval {

    static Logger log = LoggerFactory.getLogger(Interval.class);

    boolean leftInclude = true;
    boolean rightInclude = true;

    public final Double start;
    public final Double end;

    public Interval(String str) {
        String s = str;

        if (s.startsWith("[")) {
            leftInclude = true;
            s = s.substring(1);
        }

        if (s.startsWith("(") || s.startsWith("]")) {
            leftInclude = false;
            s = s.substring(1);
        }

        if (s.endsWith("]")) {
            rightInclude = true;
            s = s.substring(0, s.length() - 1);
        }

        if (s.endsWith(")") || s.endsWith("[")) {
            rightInclude = false;
            s = s.substring(0, s.length() - 1);
        }

        String[] t = s.split("(,|;)");
        start = new Double(t[0]);
        end = new Double(t[1]);
    }

    public Interval(double start, double end) {
        this.start = start;
        this.end = end;
    }

    public Interval(int start, int end) {
        this.start = Double.valueOf(start);
        this.end = Double.valueOf(end);
    }

    public Double start() {
        return start;
    }

    public Double end() {
        return end;
    }

    public boolean contains(double val) {
        boolean left = false;
        boolean right = false;

        if (leftInclude) {
            left = val >= start;
        } else {
            left = val > start;
        }

        if (rightInclude) {
            right = val <= end;
        } else {
            right = val < end;
        }

        return left && right;
    }
}