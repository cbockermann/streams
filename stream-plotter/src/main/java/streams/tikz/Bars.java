/**
 * 
 */
package streams.tikz;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class Bars extends Shape {

	/** The unique class ID */
	private static final long serialVersionUID = 7976889057103530705L;

	static Logger log = LoggerFactory.getLogger(Bars.class);

	String color = "blue";
	public double width = 0.5;
	public double margin = 0.0;

	public double xOffset = 0.0;
	public double yOffset = 0.0;

	final Series data;

	public Bars(Series data) {
		this.data = data;
		xOffset = width / 2.0;
	}

	/**
	 * @see streams.tikz.Path#toString()
	 */
	@Override
	public String toString() {

		log.info("Plotting Bars with {} data points", data.size());

		StringWriter writer = new StringWriter();
		PrintWriter p = new PrintWriter(writer);

		for (int i = 0; i < data.size(); i++) {

			Double x = new Double(i);
			Double y = data.get(i);
			p.println("%  bar (" + x + "," + y + ")");

			Path bar = new Path(true);

			bar.set("color", "blue");
			bar.opts.putAll(opts);

			double left = x + margin + xOffset;
			double right = x + width - margin + xOffset;

			bar.add(new Point(left, 0.0));
			bar.add(new Point(left, y));
			bar.add(new Point(right, y));
			bar.add(new Point(right, 0.0));

			p.println(bar.toString());
		}

		p.close();
		return writer.toString();
	}

	/**
	 * @see streams.tikz.Path#scale(double, double)
	 */
	@Override
	public Shape scale(double x, double y) {

		Series data = new Series();
		for (int i = 0; i < data.size(); i++) {
			data.add(this.data.get(i) * y);
		}

		Bars scaled = new Bars(data);
		scaled.opts.putAll(opts);
		return scaled;
	}
}