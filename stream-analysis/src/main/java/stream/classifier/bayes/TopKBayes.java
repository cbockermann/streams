/**
 * 
 */
package stream.classifier.bayes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.classifier.NaiveBayes;
import stream.counter.SimpleTopKCounting;
import stream.distribution.NominalDistribution;

/**
 * @author chris
 * 
 */
public class TopKBayes extends NaiveBayes {

	/** The unique class ID */
	private static final long serialVersionUID = 6909521190510452944L;

	static Logger log = LoggerFactory.getLogger(TopKBayes.class);

	Integer k;

	/**
	 * @return the k
	 */
	public Integer getK() {
		return k;
	}

	/**
	 * @param k
	 *            the k to set
	 */
	public void setK(Integer k) {
		this.k = k;
		this.classDistribution = createNominalDistribution();
	}

	/**
	 * @see stream.classifier.NaiveBayes#createNominalDistribution()
	 */
	@Override
	public NominalDistribution<String> createNominalDistribution() {
		log.debug("Creating new nominal distribution...");
		if (getK() == null)
			k = 100;

		NominalDistribution<String> sd = new NominalDistribution<String>(
				new SimpleTopKCounting(getK()));
		return sd;
	}
}