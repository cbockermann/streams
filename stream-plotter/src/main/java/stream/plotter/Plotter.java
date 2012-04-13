/**
 * 
 */
package stream.plotter;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Context;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.data.DataImpl;
import stream.runtime.setup.ParameterUtils;

/**
 * @author chris
 * 
 */
public class Plotter extends DataVisualizer {

	static Logger log = LoggerFactory.getLogger(Plotter.class);
	JFrame frame;
	final PlotPanel plotPanel = new PlotPanel();

	Integer history = 1000;
	String[] keys = null;
	boolean keepOpen = false;

	String xaxis = null;

	Double ymin = null;
	Double ymax = null;

	String yrange = "";
	Long processed = 0L;

	Thread updateThread = new Thread() {
		public void run() {

		}
	};

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
		if (plotPanel != null) {
			plotPanel.setSteps(history);
		}
	}

	/**
	 * @see stream.data.Processor#reset()
	 */
	@Override
	public void init(Context ctx) throws Exception {
		frame = new JFrame();

		if (yrange != null && !"".equals(yrange.trim())) {
			String[] range = yrange.split("(:|,|;)");
			ymin = new Double(range[0]);
			ymax = new Double(range[1]);
		}

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

		processed++;

		if (keys == null) {
			plotPanel.dataArrived(data);
		} else {

			Data stats = new DataImpl();
			for (String key : keys) {
				if (data.containsKey(key)) {
					stats.put(key, data.get(key));
				} else {
					stats.put(key, 0.0d);
				}
			}

			// if (this.updateInterval == null || updateInterval % processed ==
			// 0)
			plotPanel.dataArrived(stats);

		}

		return data;
	}
}