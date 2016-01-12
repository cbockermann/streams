/**
 * 
 */
package streams.tikz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chris
 * 
 */
public class Table implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 8341967116425677691L;

	final ArrayList<String> cols = new ArrayList<String>();
	final ArrayList<String> rows = new ArrayList<String>();
	LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();

	public final Map<String, String> opts = new LinkedHashMap<String, String>();

	public Table set(String key, String value) {
		if (value == null) {
			opts.remove(key);
		} else {
			opts.put(key, value);
		}
		return this;
	}

	public String option(String key, String defaultValue) {
		String opt = opts.get(key);
		if (opt == null) {
			return defaultValue;
		}
		return opt;
	}

	public Double dopt(String key, Double val) {
		try {
			Double d = new Double(opts.get(key));
			return d;
		} catch (Exception e) {
			return val;
		}
	}

	public List<String> columns() {
		return cols;
	}

	public List<String> rows() {
		return rows;
	}

	public void addColumn(String col) {
		cols.add(col);
	}

	public void addRow(String row) {
		rows.add(row);
	}

	public void add(String row, String col, String value) {
		if (!rows.contains(row)) {
			rows.add(row);
		}

		if (!cols.contains(col)) {
			cols.add(col);
		}

		data.put(row + ":" + col, value);
	}

	public String value(String row, String col) {
		String val = data.get(row + ":" + col);
		if (val == null) {
			return "";
		}
		return val;
	}

	public String toString() {
		StringWriter w = new StringWriter();
		PrintWriter p = new PrintWriter(w);
		// p.println("\\begin{tikzpicture}");

		if (!"false".equals(opts.get("columns.sort"))) {
			Collections.sort(cols);
		}

		double colWidth = dopt("column.width", 2.0);
		double rowLabelWidth = dopt("label.column.width", colWidth + 0.5);
		double rowHeight = dopt("row.height", 1.0);

		double rowY = 0.0;
		double colX = 0.0;
		double x0 = colX;
		double y0 = rowY;
		double y = rowY;

		double maxX = cols.size() * colWidth;
		double maxY = rows.size() * rowHeight;

		p.println("% header:");
		p.println("%");
		for (int i = -1; i <= cols.size(); i++) {
			if (i >= 0 && i < cols.size()) {
				String col = cols.get(i);
				if ("false".equals(opts.get("header.rotate"))) {
					p.println("\\node[] at " + new Point(x0 + (i * colWidth) + colWidth / 2.0, y0 + 0.5) + "{"
							+ columnLabel(col) + "};");
				} else {
					p.println("\\node[anchor=west,rotate=" + Tikz.format(dopt("column.label.rotate", 35.0)) + "] at "
							+ new Point(x0 + (i * colWidth) + colWidth / 2.0, y0 + 0.25) + "{" + columnLabel(col)
							+ "};");
				}
				p.println("\\draw[black!40] " + new Point(x0 + (i * colWidth), y0) + " -- "
						+ new Point(x0 + (i * colWidth), -maxY) + ";");
			}
		}

		p.println("%");
		p.println("%");
		p.println("% data:");
		p.println("%");

		for (int r = 0; r < rows.size(); r++) {
			String row = rows.get(r);
			p.println("\\node[minimum height=0.75,anchor=west] at "
					+ new Point(x0 - rowLabelWidth, y - (rowHeight / 2.0)) + " {" + rowLabel(row) + "};");

			double x = 0.0;
			p.println("\\draw[black!40] " + new Point(x0 - rowLabelWidth, y) + " -- " + new Point(maxX, y) + ";");

			for (int c = 0; c < cols.size(); c++) {
				String col = cols.get(c);

				Double cwidth = colWidth;
				try {
					cwidth = new Double(opts.get("column." + col + ".with"));
				} catch (Exception e) {
					cwidth = colWidth;
				}

				p.println("\\node[minimum height=0.75] at "
						+ new Point(x + (c * cwidth) + cwidth / 2.0, y - (rowHeight / 2.0)) + "{\\textsf{"
						+ formatValue(value(row, col)) + "}};");
			}

			x += colWidth;
			y += -rowHeight;
		}
		p.println("\\draw[black!40] " + new Point(x0 - rowLabelWidth, y) + " -- " + new Point(maxX, y) + ";");

		// p.println("\\end{tikzpicture}");
		p.flush();
		p.close();
		return w.toString();
	}

	public String formatValue(String cellValue) {
		return cellValue;
	}

	public String rowLabel(String label) {
		return "\\textsf{" + label + "}";
	}

	public String columnLabel(String label) {
		return "\\color{gruen1}{\\textsf{{" + label + "}}}";
	}

	public void toString(File file) {
		try {
			System.out.println("Writing table to " + file.getAbsolutePath());
			PrintStream p = new PrintStream(new FileOutputStream(file));
			p.println(this.toString());
			p.flush();
			p.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		Table table = new Table();
		table.addColumn("add stream");
		table.addColumn("new process");
		table.addRow("accuracy");

		table.add("accuracy", "add stream", "0.85");

		File out = new File("tex-src/chapter4/figures/results-moa-table.tex");
		table.toString(out);
	}
}
