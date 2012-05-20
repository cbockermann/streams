/**
 * 
 */
package stream.quantiles.impl;

import java.io.Serializable;
import java.util.LinkedList;

import stream.AbstractProcessor;
import stream.ProcessContext;
import stream.data.Data;
import stream.quantiles.GKQuantiles;

/**
 * <p>
 * This is an implementation of the continuous quantile estimator proposed by
 * Lin et.al. in <i>Continuously Maintaining Quantile Summaries of the Most
 * Recent N Elements over a Data Stream</i>.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class ContinuousQuantiles extends AbstractProcessor implements
		QuantileLearner {

	String key;

	// the list of buckets
	//
	final LinkedList<GKQuantiles> buckets = new LinkedList<GKQuantiles>();

	// the error factor epsilon
	Double eps = 0.05;

	// the maximum number of buckets
	Integer maxBuckets = 10;

	// the number of items processed by this learner
	Integer N = 0;

	/**
	 * This creates a new continuous learner, starting with an empty list of
	 * buckets.
	 * 
	 * @param eps
	 * @param maxBuckets
	 */
	public ContinuousQuantiles(double eps, int maxBuckets) {
		this.eps = eps;
		this.maxBuckets = maxBuckets;
		N = 0;
	}

	/**
	 * @return the eps
	 */
	public Double getEpsilon() {
		return eps;
	}

	/**
	 * @param eps
	 *            the eps to set
	 */
	public void setEpsilon(Double eps) {
		this.eps = eps;
	}

	/**
	 * @return the maxBuckets
	 */
	public Integer getBuckets() {
		return maxBuckets;
	}

	/**
	 * @param maxBuckets
	 *            the maxBuckets to set
	 */
	public void setBuckets(Integer maxBuckets) {
		this.maxBuckets = maxBuckets;
	}

	/**
	 * @return the n
	 */
	public Integer getN() {
		return N;
	}

	/**
	 * @param n
	 *            the n to set
	 */
	public void setN(Integer n) {
		N = n;
	}

	/**
	 * @see edu.udo.cs.pg542.util.learner.Learner#learn(java.lang.Object)
	 */
	public void learn(Double item) {
		GKQuantiles currentBucket = buckets.getFirst();
		if (currentBucket.getCount() >= Math.floor(eps * 0.5 * N.doubleValue())) {
			buckets.removeLast();
			currentBucket = new GKQuantiles(eps * 0.5d);
		}
		currentBucket.learn(item);
		N++;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		if (key != null) {
			Serializable value = input.get(key);
			if (Number.class.isAssignableFrom(value.getClass())) {
				learn(((Number) value).doubleValue());
			}
		}

		return input;
	}

	/**
	 * @see stream.quantiles.impl.QuantileLearner#getKey()
	 */
	@Override
	public String getKey() {
		return key;
	}

	/**
	 * @see stream.quantiles.impl.QuantileLearner#setKey(java.lang.String)
	 */
	@Override
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.quantiles.impl.QuantileLearner#getQuantile(java.lang.Double)
	 */
	@Override
	public Double getQuantile(Double phi) {
		// TODO Auto-generated method stub
		return 0.0d;
	}

	/**
	 * @see stream.StatefulProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext context) throws Exception {
		super.init(context);
		buckets.clear();
	}

	/**
	 * @see stream.StatefulProcessor#resetState()
	 */
	@Override
	public void resetState() throws Exception {
		N = 0;
		buckets.clear();
	}

	/**
	 * @see stream.StatefulProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
	}
}