/**
 * 
 */
package streams.tikz;

/**
 * @author chris
 *
 */
public class Range {

    final double start;
    final double end;

    public Range(double from, double to) {
        if (from < to) {
            start = from;
            end = to;
        } else {
            start = to;
            end = from;
        }
    }

    public boolean contains(double val) {
        return start <= val && val <= end;
    }
}