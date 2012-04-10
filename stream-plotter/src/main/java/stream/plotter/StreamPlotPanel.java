/**
 * 
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
import stream.data.DataListener;
import stream.data.stats.Statistics;
import stream.data.stats.StatisticsListener;

/**
 * @author chris
 * 
 */
public class StreamPlotPanel extends JPanel implements DataListener,
		StatisticsListener {

	/** The unique class ID */
	private static final long serialVersionUID = -4365922853856318209L;

	static Logger log = LoggerFactory.getLogger(StreamPlotPanel.class);
	final JTextField valueField = new JTextField(10);
	final XYPlot plot;
	XYSeriesCollection series = new XYSeriesCollection();
	List<ValueListener> listener = new ArrayList<ValueListener>();

	final Map<String, XYSeries> seriesMap = new LinkedHashMap<String, XYSeries>();

	final JSlider stepSlider = new JSlider(5, 1000, 100);
	final JTextField stepField = new JTextField(4);
	String pivotKey = null;
	Double pivotValue = 0.0d;
	Long lastUpdate = 0L;

	protected final JFreeChart chart;

	public StreamPlotPanel() {
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
	@Override
	public void dataArrived(Data item) {

		Statistics stats = new Statistics("");
		for (String key : item.keySet()) {
			try {
				Serializable val = item.get(key);
				if (val instanceof Double) {
					stats.add(key, (Double) val);
				}

				// Double val = new Double("" + item.get(key));
				// stats.add(key, val);
			} catch (Exception e) {
			}
		}

		dataArrived(stats);
	}

	/**
	 * @see stream.data.stats.StatisticsListener#dataArrived(stream.data.stats.Statistics)
	 */
	@Override
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

		// if (System.currentTimeMillis() - lastUpdate >= 1000) {
		updateChart();
		// }
	}

	public void updateChart() {
		// synchronized (plot) {
		plot.datasetChanged(new DatasetChangeEvent(this, series));
		lastUpdate = System.currentTimeMillis();
		// }
	}
}