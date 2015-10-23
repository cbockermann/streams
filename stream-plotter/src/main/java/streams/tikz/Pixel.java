/**
 * 
 */
package streams.tikz;

import java.util.Properties;

public class Pixel {

	public final Integer id;
	public final Integer chid;
	public final Double x;
	public final Double y;
	public final int geomX;
	public final int geomY;
	public Double value = 0.0;
	public Double radius = 0.75;
	public String color = null;
	public boolean printId = false;

	public final Properties opts = new Properties();

	public Pixel(Integer id, Integer chid, double x, double y) {
		this.id = id;
		this.chid = chid;
		this.geomX = (int) x;
		this.geomY = (int) y;
		this.x = x * (1.5 * radius);
		if (Math.abs(geomX) % 2 == 1) {
			this.y = (y - 0.5) * (1.75 * radius);
		} else {
			this.y = y * (1.75 * radius);
		}

		initOpts();
	}

	public Pixel(Integer id, Integer chid, int x, int y, Point p) {
		this.id = id;
		this.chid = chid;
		this.geomX = x;
		this.geomY = y;
		this.x = p.x;
		this.y = p.y;

		initOpts();
	}

	private void initOpts() {
		opts.put("color", "white");
		opts.put("opacity", "0.5");
		opts.put("border.color", "black!40");
		opts.put("border.opacity", "1.0");
	}

	public String toString() {
		String opts = "very thin,opacity=" + this.opts.getProperty("opacity", "0.5");

		color = this.opts.getProperty("color");
		if (color != null) {
			if (!color.isEmpty()) {
				opts = opts + ",fill=" + color;
			} else {
				opts = opts + ",fill=white";
			}
		} else {
			if (value > 0.0) {
				opts = opts + ",fill=blue";
			}
		}

		StringBuffer s = new StringBuffer();
		s.append("\\begin{scope}[shift={(" + Tikz.format(x) + "," + Tikz.format(y) + ")}]\n");

		//
		// fill the inner part of the pixel
		//
		s.append("\\fill[" + opts + "] (0:" + Tikz.format(radius) + ")");
		for (int i = 1; i < 7; i++) {
			Integer angle = (i * 60) % 360;
			s.append(" -- ");
			s.append("(" + angle + ":" + Tikz.format(radius) + ")");
		}
		s.append(";\n");

		//
		// draw the pixel border
		//
		s.append("\\draw[very thin,draw=" + this.opts.getProperty("border.color", "black!40") + ",opacity="
				+ this.opts.getProperty("border.opacity", "1.0") + " ] (0:" + Tikz.format(radius) + ")");
		for (int i = 1; i < 7; i++) {
			Integer angle = (i * 60) % 360;
			s.append(" -- ");
			s.append("(" + angle + ":" + Tikz.format(radius) + ")");
		}
		s.append(";\n");
		// s.append("\\node[scale=0.25] at (0,0) {\\tiny{" + geomX + "," + geomY
		// + "}};");
		if (printId) {
			s.append("\\node[scale=0.25] at (0,0) {\\tiny{" + id + "}};");
		}
		s.append("\\end{scope}\n");
		return s.toString();
	}

	public Point getPosition() {
		return new Point(x, y);
	}
}