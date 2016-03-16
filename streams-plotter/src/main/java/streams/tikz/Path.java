/**
 * 
 */
package streams.tikz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Path implements Component {

    public String color = "blue";
    public final Map<String, String> opts = new HashMap<String, String>();

    final List<Point> points = new ArrayList<Point>();

    final String cmd;
    Point offset = new Point(0, 0);

    public Path() {
        this(false);
    }

    public Path(boolean fill) {
        if (fill) {
            cmd = "\\fill";
        } else {
            cmd = "\\draw";
        }
    }

    public List<Point> points() {
        return Collections.unmodifiableList(points);
    }

    public void addAll(Collection<Point> pts) {
        Iterator<Point> pt = pts.iterator();
        while (pt.hasNext()) {
            Point p = pt.next();
            add(p);
        }
    }

    public Path reverse() {
        Collections.reverse(points);
        return this;
    }

    public Path set(String key, String val) {

        if (val == null) {
            opts.remove(key.trim());
        } else {
            opts.put(key.trim(), val.trim());
        }
        return this;
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        if (size() < 1) {
            s.append("% empty path.");
            return s.toString();
        }

        s.append("%\n");
        s.append("% path: \n");
        s.append(cmd + "[");

        if (!opts.containsKey("color")) {
            opts.put("color", color);
        }

        Iterator<String> oi = opts.keySet().iterator();
        while (oi.hasNext()) {
            String key = oi.next();
            String val = opts.get(key);
            if (val.trim().isEmpty()) {
                s.append(key);
            } else {
                s.append(key + "=" + val);
            }
            if (oi.hasNext()) {
                s.append(",");
            }
        }
        s.append("]");

        Iterator<Point> it = iterator();
        while (it.hasNext()) {
            Point p = it.next();
            s.append(p.toString());
            if (it.hasNext()) {
                s.append(" -- ");
            }
        }
        s.append(";\n");
        return s.toString();
    }

    public Path scale(double x, double y) {
        Path p = new Path();
        p.color = this.color;
        p.opts.putAll(this.opts);

        for (Point pt : points) {
            p.add(new Point(x * pt.x, y * pt.y));
        }
        return p;
    }

    public Path shift(double x, double y) {
        Path p = new Path();
        p.color = this.color;
        p.opts.putAll(this.opts);

        for (Point pt : points) {
            p.add(new Point(x + pt.x, y + pt.y));
        }
        return p;
    }

    public Path add(Point p) {
        points.add(p);
        return this;
    }

    public Iterator<Point> iterator() {
        return points.listIterator();
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public int size() {
        return points.size();
    }

    /**
     * @see streams.tikz.Component#toTikzString()
     */
    @Override
    public String toTikzString() {
        return toString();
    }

    /**
     * @see streams.tikz.Component#bounds()
     */
    @Override
    public Rectangle bounds() {

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