package stream.plotter;

import java.awt.Dimension;
import java.util.Date;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * A panel with a table, filters and detail area
 * 
 * @author Christophe Roger
 * 
 */
public class LogPanel extends JPanel {

	protected JTable table;
	protected DefaultTableModel model;
	protected JScrollPane pane;
	Random r;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LogPanel() {
		model = new DefaultTableModel();
		table = new JTable(model);
		pane = new JScrollPane(table);
		r = new Random();
		this.add(pane);
	}

	protected void init() {
		model.addColumn("Zeit");
		model.addColumn("Typ");
		model.addColumn("Nachricht");
		// Disable auto resizing
		// table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// Set the first visible column to 100 pixels wide
		TableColumn col = table.getColumnModel().getColumn(0);
		int width = 220;
		col.setPreferredWidth(width);
		col = table.getColumnModel().getColumn(1);
		width = 70;
		col.setPreferredWidth(width);
		col = table.getColumnModel().getColumn(2);
		width = 300;
		col.setPreferredWidth(width);
		pane.setPreferredSize(new Dimension(400, 100));

		for (int i = 0; i < 1000; i++) {
			model.addRow(new Object[] { new Date(), randomSeverity(), "v2" });
		}

	}

	private String randomSeverity() {
		double v = r.nextDouble();
		if (v > 0 && v < 0.33d)
			return "INFO";
		if (v > 0.33d && v < 0.66d)
			return "WARN";
		return "ERROR";
	}
}