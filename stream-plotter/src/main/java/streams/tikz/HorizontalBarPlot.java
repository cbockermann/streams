/**
 * 
 */
package streams.tikz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author chris
 * 
 */
public class HorizontalBarPlot {

	final Properties colors = new Properties();

	Map<String, Double> data = new LinkedHashMap<String, Double>();
	Map<String, Double> counts = new LinkedHashMap<String, Double>();

	public double scale = 0.000125;

	public void add(String series, Double data) {
		this.data.put(series, data);
		Double cnt = counts.get(series);
		if (cnt == null) {
			cnt = new Double(0.0);
		}
		cnt += 1.0;
		counts.put(series, cnt);
	}

	public String toString() {
		StringWriter w = new StringWriter();
		PrintWriter p = new PrintWriter(w);

		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("0.0", dfs);

		Double max = 0.0;

		Double y = 0.0;
		for (String series : data.keySet()) {

			Double orig = data.get(series) / counts.get(series);
			Double value = orig * scale;
			max = Math.max(max, value);
			String c = colors.getProperty(series, "red");

			p.println("\\fill[" + c + "] " + Tikz.point(0.0, y + 0.1) + " -- " + Tikz.point(value, y + 0.1) + " -- "
					+ Tikz.point(value, y + 0.9) + " -- " + Tikz.point(0.0, y + 0.9) + " -- " + Tikz.point(0.0, y + 0.1)
					+ ";");

			p.println("\\node[anchor=east] at " + Tikz.point(-0.25, y + 0.5) + " {\\color{black!40}\\textsf{" + series
					+ "}};");
			p.println("\\node[anchor=west] at " + Tikz.point(value + 0.3, y + 0.5) + "{\\color{black!40}\\textsf{"
					+ df.format(orig) + " evts/sec}};");

			y += 1.0;
		}

		Double xm = Math.ceil(max);
		p.println("\\draw[black!40,very thick,->] " + Tikz.point(0, 0) + " -- " + Tikz.point(xm, 0) + ";");
		p.println("\\draw[black!40,very thick,->] " + Tikz.point(0, 0) + " -- "
				+ Tikz.point(0, data.size() * 1.0 + 0.25) + ";");
		p.close();

		p.close();
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

	public static void main(String[] args) throws Exception {

	}
}