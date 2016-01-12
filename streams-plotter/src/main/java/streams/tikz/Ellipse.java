/**
 * 
 */
package streams.tikz;

public class Ellipse {
	public String id = null;
	public final Double x;
	public final Double y;

	public final Double width;
	public final Double height;

	public double rotate = 0;
	public String opts = "";

	public Ellipse(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public String toString() {
		StringBuffer s = new StringBuffer();

		s.append("\\begin{scope}[shift={" + Tikz.point(x, y) + "}]\n");
		s.append("\\draw[red,rotate=" + Tikz.format(rotate) + "] " + Tikz.point(0, 0) + " ellipse ("
				+ Tikz.format(width) + "cm and " + Tikz.format(height) + "cm);\n");
		s.append("\\draw[,rotate=" + Tikz.format(rotate) + "] " + Tikz.point(0 - width, 0) + " -- "
				+ Tikz.point(0 + width, 0) + ";\n");
		s.append("\\draw[,rotate=" + Tikz.format(rotate) + "] " + Tikz.point(0, 0 - height) + " -- "
				+ Tikz.point(0, 0 + height) + ";\n");

		if (id != null) {
			s.append("\\node[] (" + id + ") at " + Tikz.point(0, 0) + " {};\n");
		}

		s.append("\\end{scope}\n");

		return s.toString();
	}
}