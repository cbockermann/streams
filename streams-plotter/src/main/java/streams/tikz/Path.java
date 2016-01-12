/**
 * 
 */
package streams.tikz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Path extends ArrayList<Point> {
	private static final long serialVersionUID = 7353097414676068517L;

	public String color = "blue";
	public final Map<String, String> opts = new HashMap<String, String>();

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

		for (Point pt : this) {
			p.add(new Point(x * pt.x, y * pt.y));
		}
		return p;
	}

	public Path shift(double x, double y) {
		Path p = new Path();
		p.color = this.color;
		p.opts.putAll(this.opts);

		for (Point pt : this) {
			p.add(new Point(x + pt.x, y + pt.y));
		}
		return p;
	}
}