/**
 * 
 */
package streams.tikz;

/**
 * @author chris
 *
 */
public class AreaBetween extends Path {

    public AreaBetween(Path first, Path second) {
        super(true);
        add(first.points().get(0));
        addAll(second.points());

        Path back = first.reverse();
        for (Point p : back.points()) {
            add(p);
        }
    }
}
