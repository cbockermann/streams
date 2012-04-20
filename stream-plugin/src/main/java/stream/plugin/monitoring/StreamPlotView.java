/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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