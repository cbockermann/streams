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
import org.jfree.data.xy.XYDataset;
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
	Double pivotValue = -1.0d;

	public StreamPlotPanel() {
		// super("Stream Monitor");

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(4, 4, 4, 4));

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

		final JFreeChart chart = new JFreeChart(plot);
		final ChartPanel p = new ChartPanel(chart);
		chart.setTitle("Stream Statistics Monitor");
		chart.getTitle().setPaint(Color.DARK_GRAY);
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
		p.setBackground(this.getBackground());
		p.setBorder(null);
		p.setBorder(new EmptyBorder(8, 8, 8, 8));
		this.setBackground(Color.LIGHT_GRAY);
		add(p, BorderLayout.CENTER);

		this.setSteps(stepSlider.getValue());
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
	}

	public Integer getSteps() {
		return this.stepSlider.getValue();
	}

	public void addValueListener(ValueListener v) {
		listener.add(v);
	}

	public void plot(String name, float[] data) {
		clearSeries();
		addSeries(name, data);
		/*
		 * double[] d = new double[data.length]; for( int i = 0; i <
		 * data.length; i++ ){ d[i] = data[i]; } plot( name, d );
		 */
	}

	public void clearSeries() {
		series.removeAllSeries();
	}

	public void plot(String name, double[] data) {
		series.removeAllSeries();
		series.addSeries(createSeries(name, data));
		plot.datasetChanged(null);
	}

	public void addSeries(String name, double[] data) {
		series.addSeries(createSeries(name, data));
	}

	public void addSeries(String name, float[] data) {
		series.addSeries(createSeries(name, data));
	}

	public XYDataset createDataset(String name, double[] data) {
		return new XYSeriesCollection(createSeries(name, data));
	}

	public XYSeries createSeries(String name, float[] data) {
		XYSeries series = new XYSeries(name);
		for (int i = 0; i < data.length; i++) {
			series.add((double) i, data[i]);
		}
		return series;
	}

	public XYSeries createSeries(String name, double[] data) {
		XYSeries series = new XYSeries(name);
		for (int i = 0; i < data.length; i++) {
			series.add((double) i, data[i]);
		}
		return series;
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
				this.series.addSeries(series);
				seriesMap.put(key, series);
			}

			Double value = item.get(key);
			if (value == null)
				value = 0.0d;

			series.add(pivotValue, value);
		}

		this.plot.datasetChanged(new DatasetChangeEvent(this, series));
	}
}