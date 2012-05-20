package stream.quantiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.quantiles.impl.AbstractQuantileLearner;

/**
 * This is a simple implementation to determine exact quantiles. It's just for
 * testing/validating purpose so it won't be the most efficient algorithm
 * neither in respect to space consumption nor in respect to time consumption.
 * And of course it is not an online algorithm!
 * 
 * @author Markus Kokott, Christian Bockermann
 * 
 */
public class ExactQuantiles extends AbstractQuantileLearner {

	static Logger log = LoggerFactory.getLogger(ExactQuantiles.class);
	final List<Double> data = new ArrayList<Double>();

	/**
	 * @see stream.quantiles.impl.QuantileLearner#getQuantile(java.lang.Double)
	 */
	public Double getQuantile(Double phi) {
		Double rank = Math.floor(phi * data.size());
		log.debug("Computed rank for {}-quantile is {}", phi, rank.intValue());
		int idx = rank.intValue();

		synchronized (data) {
			Collections.sort(data);

			if (idx >= 0 && idx < data.size()) {
				Double lower = data.get(rank.intValue());
				return lower;
			} else {
				log.error("Invalid index '{}'", idx);
				return Double.NaN;
			}
		}
	}

	/**
	 * @see edu.udo.cs.pg542.util.DataStreamProcessor#process(java.lang.Object)
	 */
	public void learn(Double item) {
		synchronized (data) {
			data.add(item);
		}
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		data.clear();
	}
}