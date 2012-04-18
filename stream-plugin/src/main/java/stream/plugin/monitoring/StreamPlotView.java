/**
 * 
 */
package stream.plugin.monitoring;

import java.awt.BorderLayout;

import javax.swing.border.EmptyBorder;

import stream.data.Data;
import stream.data.DataListener;
import stream.data.Statistics;
import stream.plotter.PlotPanel;

/**
 * @author chris
 * 
 */
public class StreamPlotView extends AbstractMonitorView implements DataListener {

	/** The unique class ID */
	private static final long serialVersionUID = -4365922853856318209L;
	PlotPanel plotPanel;

	public StreamPlotView() {
		super("stream.monitor");

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(4, 4, 4, 4));

		plotPanel = new PlotPanel();
		add(plotPanel, BorderLayout.CENTER);

		/*
		 * Thread t = new Thread() { public void run() {
		 * plotPanel.updateChart(); try { Thread.sleep(1000); } catch (Exception
		 * e) { e.printStackTrace(); } } }; t.setDaemon(true); t.start();
		 */
	}

	public void reset() {
		plotPanel.reset();
	}

	/**
	 * @see stream.data.DataListener#dataArrived(stream.data.Data)
	 */
	@Override
	public void dataArrived(Data item) {

		Statistics stats = new Statistics("");
		for (String key : item.keySet()) {
			try {
				Double val = new Double("" + item.get(key));
				stats.add(key, val);
			} catch (Exception e) {
			}
		}

		dataArrived(stats);
	}

	/**
	 */
	public synchronized void dataArrived(Statistics item) {
		plotPanel.dataArrived(item);
	}
}