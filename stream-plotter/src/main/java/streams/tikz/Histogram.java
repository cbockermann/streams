/**
 * 
 */
package streams.tikz;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Keys;
import stream.data.Statistics;

/**
 * @author chris
 * 
 */
public class Histogram {

	static Logger log = LoggerFactory.getLogger(Histogram.class);

	static Map<String, Statistics> stats = new HashMap<String, Statistics>();

	public final Map<String, String> colors = new HashMap<String, String>();

	Double offx = 0.0d;
	Double offy = 0.0d;

	double yStep = 1.0;
	public double scaleY = 1.0;
	public double scaleX = 1.0d;

	public boolean labels = true;
	public double scaleLabel = 1.0;
	public double barWidth = 1.0;
	public double barSpace = 0.1;
	public Double maxYrange = null;
	public Double maxXrange = null;

	public boolean logScale = false;

	public boolean gridY = false;

	public String labelX = null;
	public String labelY = null;

	public void tikzHistogram(Statistics st) {
		tikzHistogram(st, new Keys(st.keySet()), System.out);
	}

	public void tikzHistogram(Statistics st, Keys ks, OutputStream out) {

		List<String> keyList = new ArrayList<String>(ks.select(st.keySet()));
		tikzHistogram(st, keyList, out);
	}

	public void tikzHistogram(Statistics st, List<String> ks, OutputStream out) {
		log.info("Creating histogram with labels? {}", labels);
		String gridStyle = "black!40";
		final DecimalFormatSymbols sym = new DecimalFormatSymbols();
		sym.setDecimalSeparator('.');
		final DecimalFormat fmt = new DecimalFormat("0.0", sym);

		PrintStream p = new PrintStream(out);

		List<String> keys = ks;

		Double vMax = Double.MIN_VALUE;
		for (String key : keys) {
			if (logScale) {
				vMax = Math.max(vMax, Math.log(st.get(key)));
			} else {
				vMax = Math.max(vMax, st.get(key));
			}
		}

		if (maxYrange != null) {
			vMax = maxYrange;
		}
		log.info("Using yMax = {}", vMax);
		vMax *= scaleY;

		Double offx = this.offx;
		Double offy = this.offy;

		Point zero = new Point(offx, offy);
		Point endX = new Point(scaleX * (offx + (1 + keys.size()) * (barSpace + barWidth) + 1.0), offy);
		Point endY = new Point(scaleX * offx, offy + vMax + (yStep / 4));

		if (gridY) {
			double xs = scaleX * (offx - 0.25);

			for (int b = 0; b <= 5; b++) {
				for (double i = 0; i <= 5; i++) {

					double ys = scaleX * (offy + i);
					// if (logScale) {
					// ys = scaleX * (offy + Math.log10(Math.pow(10, b) + i));
					// }
					p.println("\\draw[very thin,black!20] " + new Point(xs, ys) + " -- " + new Point(endX.x, ys) + ";");
				}
			}
		}

		offx += barSpace + barWidth / 2.0;

		for (String key : keys) {

			Double value = scaleY * st.get(key);
			// p.println();

			if (logScale) {
				value = scaleY * Math.log(st.get(key));
			}

			Point ul = new Point(scaleX * (offx - barWidth / 2.0), offy);
			Point or = new Point(scaleX * (offx + barWidth / 2.0), offy + value);

			String color = colors.get(key);
			if (color == null) {

				color = colors.get(".default");
				if (color == null) {
					color = "gruen1";
				}
			}

			if (new Double(key) < 2.0 || new Double(key) >= 2.5) {
				// color = color + "!20";
			}

			p.print("\\fill[fill=" + color + ",opacity=1.0] " + ul + " rectangle " + or + ";");
			p.println("     % bar for key '" + key + "', value = " + value);

			if (this.labels) {
				Point lp = new Point(offx * scaleX, offy - 0.25);
				p.println("\\node[anchor=west,rotate=-60,scale=1.5] at " + lp + " {\\textsf{" + key + "}};");
			}

			offx += barWidth + barSpace;
		}

		p.println();
		p.println("% x- and y-axis");
		p.println("\\draw[" + gridStyle + "] " + zero + " -- " + endX + " ;");
		double xStep = (barWidth + barSpace);
		double xMax = (barWidth + barSpace) * keys.size();
		if (this.labels) {
			for (Double v = barSpace + barWidth / 2; v < xMax; v += xStep) {
				p.println("\\draw[" + gridStyle + "] (" + fmt.format(scaleX * v) + ",0) -- " + "("
						+ fmt.format(scaleX * v) + ",-0.25);");
			}
		}

		if (labelX != null && !labelX.trim().isEmpty()) {
			p.println("\\node at " + Tikz.point(xMax / 2.0, offy - 0.25) + "{\\textsf{" + labelX + " }};");
		}

		p.println("\\draw[" + gridStyle + "] " + zero + " -- " + endY + " ;");
		if (this.labels) {
			for (Double v = 0.0; v < vMax + (yStep / 4.0); v += yStep) {

				Double y = v;

				String yValue = fmt.format(y);
				if (logScale) {
					yValue = "$10^{" + y.intValue() + "}$";
					if (y.intValue() == 0) {
						yValue = "$0$";
					}
				} else {
					yValue = fmt.format(y / scaleY);
				}

				p.println("\\draw[" + gridStyle + "] (0," + fmt.format(y) + ") -- (" + fmt.format(scaleX * -0.25) + ","
						+ fmt.format(y) + ");");
				// p.println("\\node[anchor=east,scale=" +
				// fmt.format(scaleLabel)
				// + "] at (" + fmt.format(scaleX * -0.5) + ","
				// + fmt.format(y / scaleY) + ") {\\textsf{" + yValue
				// + "}};");
			}
		}

		if (logScale) {
		}

		p.close();
		// System.out.println(out.toString());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Histogram histogram = new Histogram();
		histogram.labels = false;
		histogram.maxYrange = 35.0;
		histogram.scaleY = 0.4;

	}
}
