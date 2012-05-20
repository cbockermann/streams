/**
 * 
 */
package stream.distribution;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import stream.generator.Gaussian;

/**
 * This class implements a numerical distribution, based on a mean and variance
 * observed from data.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class NumericalDistribution implements Distribution<Double> {

	/** The unique class ID */
	private static final long serialVersionUID = 9412636245656217L;
	AtomicInteger n = new AtomicInteger(0);
	Double mean = 0.0d;
	Double m2 = 0.0d;

	/**
	 * @see stream.distribution.Distribution#update(java.io.Serializable)
	 */
	@Override
	public void update(Double x) {
		int i = n.incrementAndGet();
		double delta = x - mean;
		mean = mean + delta / i;
		synchronized (m2) {
			m2 = m2 + delta * (x - mean);
		}
	}

	public Double getMean() {
		return mean;
	}

	public Double getVariance() {
		Double m2copy = m2;
		synchronized (m2copy) {
			m2copy = new Double(m2);
		}
		return m2copy / n.intValue();
	}

	/**
	 * @see stream.distribution.Distribution#getHistogram()
	 */
	@Override
	public Map<Double, Double> getHistogram() {

		Map<Double, Double> map = new LinkedHashMap<Double, Double>();

		for (Double d = -5.0; d <= 5.0; d += 0.1) {
			Double key = new Double(d);
			Double p = this.prob(key);

			map.put(key, p);
		}

		return map;
	}

	/**
	 * @see stream.distribution.Distribution#prob(java.io.Serializable)
	 */
	@Override
	public Double prob(Double value) {
		Gaussian g = new Gaussian(getMean(), Math.sqrt(getVariance()),
				System.currentTimeMillis());
		return g.p(value);
	}
}
