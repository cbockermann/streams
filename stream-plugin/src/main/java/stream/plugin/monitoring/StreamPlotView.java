/**
 * 
 */
package stream.plugin.monitoring;

import java.awt.BorderLayout;

import javax.swing.border.EmptyBorder;

import stream.data.Data;
import stream.data.DataListener;
import stream.data.stats.Statistics;
import stream.data.stats.StatisticsListener;
import stream.plotter.StreamPlotPanel;

/**
 * @author chris
 * 
 */
public class StreamPlotView extends AbstractMonitorView implements
		DataListener, StatisticsListener {

	/** The unique class ID */
	private static final long serialVersionUID = -4365922853856318209L;
	StreamPlotPanel plotPanel;

	public StreamPlotView() {
		super("stream.monitor");

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(4, 4, 4, 4));

		plotPanel = new StreamPlotPanel();
		add(plotPanel, BorderLayout.CENTER);
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
	 * @see stream.data.stats.StatisticsListener#dataArrived(stream.data.stats.Statistics)
	 */
	@Override
	public synchronized void dataArrived(Statistics item) {
		plotPanel.dataArrived(item);
	}
}