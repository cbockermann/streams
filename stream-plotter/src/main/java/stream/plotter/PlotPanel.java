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
package stream.plotter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.Statistics;

/**
 * @author chris
 * 
 */
public class PlotPanel extends JPanel {

	/** The unique class ID */
	private static final long serialVersionUID = -4365922853856318209L;

	static Logger log = LoggerFactory.getLogger(PlotPanel.class);
	final JTextField valueField = new JTextField(10);
	protected final XYPlot plot;
	XYSeriesCollection series = new XYSeriesCollection();
	List<ValueListener> listener = new ArrayList<ValueListener>();

	final Map<String, XYSeries> seriesMap = new LinkedHashMap<String, XYSeries>();

	final JSlider stepSlider = new JSlider(5, 1000, 100);
	final JTextField stepField = new JTextField(4);
	String pivotKey = null;
	Double pivotValue = 0.0d;
	Long lastUpdate = 0L;

	protected final JFreeChart chart;

	public PlotPanel() {
		// super("Stream Monitor");

		setLayout(new BorderLayout());

		JPanel fp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fp.setBorder(null);
		fp.add(new JLabel("Value: "));
		valueField.setEditable(false);
		fp.add(valueField);

		final JLabel stepLabel = new JLabel("History: ");
		stepField.setText("" + getSteps());
		stepField.setEditable(false);
		stepField.setHorizontalAlignment(JTextField.RIGHT);

		stepSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				int steps = stepSlider.getValue();
				setSteps(steps);
				stepField.setText(steps + "");
			}
		});

		fp.add(stepLabel);
		fp.add(stepField);
		fp.add(stepSlider);

		add(fp, BorderLayout.SOUTH);

		// setBorder(BorderFactory.createEtchedBorder());

		ValueAxis range = new NumberAxis("");
		range.setAutoRange(true);
		range.setFixedAutoRange(this.getSteps().doubleValue());
		// range.setAutoTickUnitSelection(true);

		XYItemRenderer render = new StandardXYItemRenderer();
		plot = new XYPlot(series, range, new NumberAxis(""), render);

		chart = new JFreeChart(plot);
		final ChartPanel p = new ChartPanel(chart);
		chart.setBackgroundPaint(this.getBackground());

		p.addChartMouseListener(new ChartMouseListener() {

			@Override
			public void chartMouseClicked(ChartMouseEvent arg0) {
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// log.info( "chartMouseMoved: {}", arg0 );
				MouseEvent me = arg0.getTrigger();
				/*
				 * log.info( "offsets: {}", offsets ); log.info(
				 * "domainAxisEdge: {}", plot.getDomainAxisEdge() ); log.info(
				 * "rangeAxisLocation: {}", plot.getRangeAxisLocation() );
				 * log.info( "domainAxisLocation: {}",
				 * plot.getDomainAxisLocation() ); log.info( "p.insets: {}",
				 * p.getInsets() ); log.info( "axis.offset.left: {}",
				 * plot.getAxisOffset().getLeft() );
				 */
				NumberAxis domain = (NumberAxis) plot.getDomainAxis();
				Rectangle2D chartArea = p.getChartRenderingInfo().getPlotInfo()
						.getDataArea();
				Double xval = domain.java2DToValue((double) me.getPoint().x,
						chartArea, plot.getDomainAxisEdge());
				Double yval = plot.getRangeAxis().java2DToValue(
						(double) me.getPoint().y, chartArea,
						plot.getRangeAxisEdge());

				String val = yval + "";
				if (val.length() > 10) {
					valueField.setText(val.substring(0, 10));
				} else
					valueField.setText(val);

				if (me.isShiftDown()) {
					for (ValueListener v : listener) {
						v.selectedValue(xval, yval);
					}
				}
			}

		});
		p.setBorder(new EmptyBorder(8, 8, 8, 8));
		add(p, BorderLayout.CENTER);

		this.setSteps(stepSlider.getValue());

	}

	public XYPlot getPlot() {
		return plot;
	}

	public JFreeChart getChart() {
		return chart;
	}

	public void reset() {
		series.removeAllSeries();
		seriesMap.clear();
		pivotValue = 0.0d;
		plot.datasetChanged(new DatasetChangeEvent(this, series));
	}

	public void removeKey(String key) {
		for (int i = 0; i < series.getSeriesCount(); i++) {
			if (key.equals(series.getSeries(i).getKey().toString())) {
				series.removeSeries(i);
				return;
			}
		}
	}

	public void setSteps(Integer steps) {
		ValueAxis range = plot.getDomainAxis();
		range.setFixedAutoRange(steps.doubleValue());
		plot.axisChanged(new AxisChangeEvent(range));

		for (int i = 0; i < series.getSeriesCount(); i++) {
			series.getSeries(i).setMaximumItemCount(steps);
		}
		stepField.setText(steps + "");
	}

	public Integer getSteps() {
		return this.stepSlider.getValue();
	}

	public void setYRange(Double ymin, Double ymax) {
		if (ymin != null && ymax != null)
			plot.getRangeAxis().setRange(ymin, ymax);
	}

	public void setTitle(String title) {
		chart.setTitle(title);
		chart.getTitle().setPaint(Color.DARK_GRAY);
	}

	public void addValueListener(ValueListener v) {
		listener.add(v);
	}

	/**
	 * @see stream.data.DataListener#dataArrived(stream.data.Data)
	 */
	public void dataArrived(Data item) {

		Statistics stats = new Statistics("");
		for (String key : item.keySet()) {
			try {
				Serializable val = item.get(key);
				if (val instanceof Number) {
					stats.add(key, new Double(val.toString()));
				}

				// Double val = new Double("" + item.get(key));
				// stats.add(key, val);
			} catch (Exception e) {
			}
		}

		dataArrived(stats);
	}

	/**
	 * @see stream.data.stats.StatisticsListener#dataArrived(stream.data.Statistics)
	 */
	public void dataArrived(Statistics item) {

		if (pivotKey == null)
			pivotValue += 1.0d;
		else {
			Serializable pv = item.get(pivotKey);
			try {
				Double xval = new Double("" + pv);
				pivotValue = xval;
			} catch (Exception e) {
				pivotValue += 1.0d;
			}
		}

		//
		// we need to update all series and create missing
		// series elements as well
		//
		Set<String> keys = new HashSet<String>(item.keySet());
		keys.addAll(seriesMap.keySet());

		for (String key : keys) {

			XYSeries series = seriesMap.get(key);
			if (series == null) {
				series = new XYSeries(key);
				series.setMaximumItemCount(this.getSteps());
				this.series.addSeries(series);
				seriesMap.put(key, series);
			}

			Double value = item.get(key);
			if (value == null)
				value = 0.0d;

			series.add(pivotValue, value);
		}
		if (System.currentTimeMillis() - lastUpdate >= 1000)
			updateChart();
	}

	public void updateChart() {
		// synchronized (plot) {
		plot.datasetChanged(new DatasetChangeEvent(this, series));
		lastUpdate = System.currentTimeMillis();
		// }
	}
}
