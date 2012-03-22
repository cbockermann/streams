/**
 * 
 */
package stream.plotter;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.ConditionedDataProcessor;
import stream.data.Data;
import stream.data.DataImpl;
import stream.util.ParameterUtils;

/**
 * @author chris
 * 
 */
public class LiveStreamPlotter extends ConditionedDataProcessor {

	static Logger log = LoggerFactory.getLogger(LiveStreamPlotter.class);
	JFrame frame;
	StreamPlotPanel plotPanel;

	String[] keys = null;
	Integer width = 1024;
	Integer height = 400;
	boolean keepOpen = false;

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
	public void setKeys(String keys) {
		this.keys = ParameterUtils.split(keys);
	}

	/**
	 * @return the width
	 */
	public Integer getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(Integer width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public Integer getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(Integer height) {
		this.height = height;
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
	 * @see stream.data.Processor#init()
	 */
	@Override
	public void init() throws Exception {
		frame = new JFrame();
		plotPanel = new StreamPlotPanel();

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(plotPanel, BorderLayout.CENTER);
		frame.setSize(1024, 400);
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