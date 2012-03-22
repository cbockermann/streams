/**
 * 
 */
package stream.plotter;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.util.Parameter;
import stream.util.ParameterUtils;

/**
 * @author chris
 * 
 */
public class LiveStreamPlotter extends DataVisualizer {

	static Logger log = LoggerFactory.getLogger(LiveStreamPlotter.class);
	JFrame frame;
	StreamPlotPanel plotPanel;

	Integer history = 1000;
	String[] keys = null;
	boolean keepOpen = false;

	String xaxis = null;

	Double ymin = null;
	Double ymax = null;

	String yrange = "";

	/**
	 * @return the yrange
	 */
	public String getYrange() {
		return yrange;
	}

	/**
	 * @param yrange
	 *            the yrange to set
	 */
	@Parameter(required = false, description = "The range of the Y-axis to be plotted. Format is  'min;max' (without quotes, min/max need to be numbers)")
	public void setYrange(String yrange) {
		this.yrange = yrange;
	}

	/**
	 * @return the keys
	 */
	public String getKeys() {
		return ParameterUtils.join(keys);
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	@Parameter(required = false, description = "The attributes/features to be plotted (non-numerical features will be ignored)")
	public void setKeys(String keys) {
		this.keys = ParameterUtils.split(keys);
	}

	/**
	 * @return the keepOpen
	 */
	public boolean isKeepOpen() {
		return keepOpen;
	}

	/**
	 * @param keepOpen
	 *            the keepOpen to set
	 */
	public void setKeepOpen(boolean keepOpen) {
		this.keepOpen = keepOpen;
	}

	/**
	 * @return the history
	 */
	public Integer getHistory() {
		return history;
	}

	/**
	 * @param history
	 *            the history to set
	 */
	@Parameter(required = false, description = "The number of samples displayed in the plot (i.e. the 'window size' of the plot)")
	public void setHistory(Integer history) {
		this.history = history;
	}

	/**
	 * @see stream.data.Processor#init()
	 */
	@Override
	public void init() throws Exception {
		frame = new JFrame();

		if (yrange != null && !"".equals(yrange.trim())) {
			String[] range = yrange.split("(:|,|;)");
			ymin = new Double(range[0]);
			ymax = new Double(range[1]);
		}

		plotPanel = new StreamPlotPanel();
		plotPanel.setSteps(Math.max(5, history));

		if (ymin != null && ymax != null) {
			plotPanel.plot.getRangeAxis().setRange(ymin, ymax);
		}

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(plotPanel, BorderLayout.CENTER);
		frame.setSize(width, height);
		frame.setVisible(true);
	}

	/**
	 * @see stream.data.Processor#finish()
	 */
	@Override
	public void finish() throws Exception {
		if (!keepOpen) {
			log.debug("Closing plot frame");
			frame.setVisible(false);
			frame.dispose();
			frame = null;
		} else {
			log.debug("Keeping plot frame visible...");
		}
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {

		if (keys == null) {
			plotPanel.dataArrived(data);
			return data;
		}

		Data stats = new DataImpl();
		for (String key : keys) {
			if (data.containsKey(key)) {
				stats.put(key, data.get(key));
			} else {
				stats.put(key, 0.0d);
			}
		}

		plotPanel.dataArrived(stats);
		return data;
	}
}