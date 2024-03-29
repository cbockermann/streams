/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.plotter;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.Keys;
import stream.ProcessContext;
import stream.annotations.Parameter;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class Plotter extends DataVisualizer {

	static Logger log = LoggerFactory.getLogger(Plotter.class);
	JFrame frame;
	final PlotPanel plotPanel = new PlotPanel();

	Integer history = 1000;
	Keys keys;
	boolean keepOpen = false;


	Double ymin = null;
	Double ymax = null;

	String title = "";
	String yrange = "";
	Long processed = 0L;

	Thread updateThread = new Thread() {
		public void run() {

		}
	};

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	@Parameter(required = false, description = "The title of the plot-window.")
	public void setTitle(String title) {
		this.title = title;
	}

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
	public Keys getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	@Parameter(required = false, description = "The attributes/features to be plotted (non-numerical features will be ignored)")
	public void setKeys(Keys keys) {
		this.keys = keys;
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
	 * @see stream.StatefulProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		frame = new JFrame();

		if (yrange != null && !"".equals(yrange.trim())) {
			String[] range = yrange.split("(:|,|;)");
			ymin = new Double(range[0]);
			ymax = new Double(range[1]);
		}

		if (ymin != null && ymax != null) {
			plotPanel.plot.getRangeAxis().setRange(ymin, ymax);
		}

		if (title != null) {
			frame.setTitle(title);
		}
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(plotPanel, BorderLayout.CENTER);
		frame.setSize(width, height);
		frame.setVisible(true);

		log.info("History is {} steps", history);
		plotPanel.setHistory(Math.max(5, history));
	}

	/**
	 * @see stream.StatefulProcessor#finish()
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
	 * @see stream.ConditionedProcessor#processMatchingData(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {

		processed++;
		if (keys == null) {
			plotPanel.dataArrived(data);
		} else {
			Data stats = DataFactory.create();

			Set<String> selected = keys.select(data);
			for (String key : selected) {
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