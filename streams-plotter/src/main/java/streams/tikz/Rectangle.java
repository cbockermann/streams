/**
 * 
 */
package streams.tikz;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chris
 *
 */
public class Rectangle implements Component {

    final Point topLeft;
    final Point bottomRight;

    public Rectangle(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    /**
     * @see streams.tikz.Component#toTikzString()
     */
    @Override
    public String toTikzString() {

        StringBuffer s = new StringBuffer();
        s.append("\\draw ");
        s.append(Tikz.format(topLeft));
        s.append(" rectangle ");
        s.append(Tikz.format(bottomRight));
        s.append(";");

        return s.toString();
    }

    public double width() {
        return Math.abs(bottomRight.x - topLeft.x);
    }

    public double height() {
        return Math.abs(bottomRight.y - topLeft.y);
    }

    public Rectangle bounds() {
        return this;
    }

    public static Rectangle boundsOf(List<Component> cs) {

        if (cs.isEmpty()) {
            return new Rectangle(new Point(0, 0), new Point(100, 100));
        }

        List<Point> points = new ArrayList<Point>();
        for (Component c : cs) {
            Rectangle bounds = c.bounds();
            points.add(bounds.topLeft);
            points.add(bounds.bottomRight);
        }

        Double minX = points.get(0).x;
        Double minY = points.get(0).y;
        Double maxX = minX;
        Double maxY = minY;

        for (Point p : points) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        return new Rectangle(new Point(minX, maxY), new Point(maxX, minY));
    }
}
