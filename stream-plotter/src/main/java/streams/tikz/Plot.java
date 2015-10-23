/**
 * 
 */
package streams.tikz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author chris
 * 
 */
public class Plot {

	public final static String VERSION = "0.1";

	public String axisColor = "black!60";

	public double scaleX = 1.0;
	public double scaleY = 1.0;

	public double maxX = 10.0;
	public double maxY = 5.0;
	public double minX = 0.0;
	public double minY = 0.0;

	public final Properties opts = new Properties();

	final List<Path> paths = new ArrayList<Path>();
	final List<Shape> shapes = new ArrayList<Shape>();

	final List<Inline> inlines = new ArrayList<Inline>();

	boolean debug = false;

	public Plot() {

	}

	public Double getDoubleOption(String key, Double defaultValue) {
		try {
			return new Double(opts.getProperty(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public void add(Inline inline) {
		inlines.add(inline);
	}

	public void add(Path path) {
		this.paths.add(path);
	}

	public void add(Shape shape) {
		this.shapes.add(shape);
	}

	public String toString() {
		StringWriter w = new StringWriter();
		PrintWriter s = new PrintWriter(w);

		s.println("% Generated with TikzPlot, version " + VERSION);
		s.println("% ");

		s.println("%");
		s.println("% plot has " + inlines.size() + " inlinde code snippets:");
		for (Inline inline : inlines) {
			s.println("% inline code:");
			s.println(inline);
		}

		s.println("% ");
		s.println("% plot has " + paths.size() + " paths.");

		Point origin = new Point(0, 0);
		if (debug) {
			s.println("\\node[circle,minimum size=2pt,inner sep=0pt,fill=red] at " + origin + " {};");
		}
		origin = origin.shift(minX, minY);
		if (debug) {
			s.println("\\node[circle,minimum size=2pt,inner sep=0pt,fill=black] at " + origin + " {};");
		}

		for (Path p : paths) {

			Path shifted = p.shift(0, minY);
			s.println(shifted.toString());
			// Path scaled = shifted.scale(scaleX, scaleY);
			// s.println(scaled.toString());
		}

		for (Shape shape : shapes) {
			s.println(shape.toString());
		}

		s.println("% x-axis:");
		s.println("\\draw[color=" + axisColor + "] " + origin + " -- " + new Point(scaleX * maxX, minY) + ";");
		s.println();

		if (opts.containsKey("x-axis.label")) {
			double midX = 0.5 * (minX + maxX);
			double midY = -0.75;

			String color = opts.getProperty("x-axis.label.color", "black!60");
			s.println("\\node[scale=" + Tikz.format(getDoubleOption("x-axis.label.scale", 0.75)) + "] at "
					+ new Point(midX, midY) + "{ \\color{" + color + "}{\\textsf{ " + opts.getProperty("x-axis.label")
					+ "} } };");
		}

		s.println("% y-axis:");
		s.println("\\draw[color=" + axisColor + "] " + origin + " -- " + Tikz.point(origin.x, scaleY * maxY) + ";");

		if (opts.containsKey("y-axis.label")) {
			double midY = 0.5 * (minY + maxY);
			double midX = -0.75;

			String color = opts.getProperty("x-axis.label.color", "black!60");
			s.println("\\node[scale=" + Tikz.format(getDoubleOption("y-axis.label.scale", 0.75)) + ",rotate=90] at "
					+ new Point(midX, midY) + "{ \\color{" + color + "}{\\textsf{ " + opts.getProperty("y-axis.label")
					+ "} } };");
		}

		s.println();

		s.close();
		return w.toString();
	}

	public void toFile(File file) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(file));
			out.println(this.toString());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
