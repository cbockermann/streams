/**
 * 
 */
package stream.learner;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ProcessContext;
import stream.StatefulProcessor;
import stream.annotations.Description;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.mining.Distribution;
import stream.mining.NominalDistributionModel;
import stream.mining.NumericalDistributionModel;

/**
 * <p>
 * This class implements a NaiveBayes classifier. It combines the learning
 * algorithm and the model implementation in one. The implementation provides
 * support for numerical (Double) and nominal (String) attributes.
 * </p>
 * <p>
 * The implementation is a strictly incremental one and supports memory
 * limitation. The memory limitation is carried out by truncating the
 * distribution models built for each of the observed attributes.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
@Description(group = "Data Stream.Mining.Classification", text = "A classifier based on the Bayes theorem")
public class NaiveBayes implements StatefulProcessor, PredictionService,
		Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 628570935563478204L;

	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger(NaiveBayes.class);

	/* The attribute used as label */
	String labelKey = null;

	/* The La-Place correction term */
	Double laplaceCorrection = 0.0001;

	Double confidenceGap = new Double(0.0d);

	Boolean wop = false;

	/* This is the distribution of the different classes observed */
	Distribution<String> classDistribution = null; // createNominalDistribution();

	/* A map providing the distributions of the attributes (nominal,numerical) */
	Map<String, Distribution<?>> distributions = new HashMap<String, Distribution<?>>();

	/**
	 * Create a new NaiveBayes instance. The label attribute is automatically
	 * determined by the learner, if not explicitly set with the
	 * <code>setLabelAttribute</code> method.
	 */
	public NaiveBayes() throws RemoteException {
		classDistribution = createNominalDistribution();
	}

	/**
	 * Create a new NaiveBayes instance which uses the specified attribute as
	 * label.
	 * 
	 * @param labelAttribute
	 */
	public NaiveBayes(String labelAttribute) throws RemoteException {
		this();
		setLabel(labelAttribute);
	}

	/**
	 * @return the labelAttribute
	 */
	public String getLabel() {
		return labelKey;
	}

	/**
	 * @param labelAttribute
	 *            the labelAttribute to set
	 */
	@Parameter(required = true, description = "The label attribute name.")
	public void setLabel(String labelAttribute) {
		this.labelKey = labelAttribute;
	}

	/**
	 * @return the laplaceCorrection
	 */
	public Double getLaplaceCorrection() {
		return laplaceCorrection;
	}

	/**
	 * @param laplaceCorrection
	 *            the laplaceCorrection to set
	 */
	@Parameter(required = false, description = "The laplace correction factor")
	public void setLaplaceCorrection(Double laplaceCorrection) {
		this.laplaceCorrection = laplaceCorrection;
	}

	/**
	 * @return the confidenceGap
	 */
	public Double getConfidenceGap() {
		return confidenceGap;
	}

	/**
	 * @param confidenceGap
	 *            the confidenceGap to set
	 */
	public void setConfidenceGap(Double confidenceGap) {
		this.confidenceGap = confidenceGap;
	}

	/**
	 * @return the wop
	 */
	public Boolean getWop() {
		return wop;
	}

	/**
	 * @param wop
	 *            the wop to set
	 */
	public void setWop(Boolean wop) {
		this.wop = wop;
	}

	/**
	 * @see stream.model.PredictionModel#predict(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Double> vote(Data item) {

		Map<String, Double> classLikeli = new LinkedHashMap<String, Double>();
		log.debug("Predicting one of these classes: {}",
				classDistribution.getElements());

		for (String label : getClassDistribution().getElements()) {
			// 9/14 class likelihoods
			//

			if (wop) {
				classLikeli.put(label, 1.0d);
			} else {
				Double cl = getClassDistribution().getCount(label)
						.doubleValue(); // .getHistogram().get( label );
				log.debug("class likelihood for class '" + label
						+ "' is {} / {}", cl, getClassDistribution().getCount());
				Double p_label = getClassDistribution().getHistogram().get(
						label)
						/ this.getClassDistribution().getCount();
				classLikeli.put(label, p_label);
			}
		}

		//
		// compute the class likelihood for each class:
		//
		Double max = 0.0d;
		String maxClass = null;
		Double totalLikelihood = 0.0d;

		for (String clazz : classLikeli.keySet()) {

			Double likelihood = classLikeli.get(clazz);

			for (String attribute : item.keySet()) {

				if (!this.labelKey.equals(attribute)) {

					Object value = item.get(attribute);
					if (value.getClass().equals(Double.class)) {
						//
						// multiplying probability for double value
						//
						Distribution<Double> dist = (Distribution<Double>) distributions
								.get(clazz);
						likelihood *= dist.prob((Double) value);

					} else {
						//
						// determine likelihood for nominal value
						//
						String feature = this.getNominalCondition(attribute,
								item);

						Double d = ((Distribution<String>) distributions
								.get(clazz)).getCount(feature).doubleValue();
						Double total = this.getClassDistribution()
								.getCount(clazz).doubleValue();

						if (d == null || d == 0.0d) {
							d = laplaceCorrection;
							total += laplaceCorrection;
						}

						log.debug("  likelihood for {}  is  {}  |" + clazz
								+ " ", feature, d / total);
						likelihood *= (d / total);
					}
				}
			}

			classLikeli.put(clazz, likelihood);
			totalLikelihood += likelihood;
		}

		// determine most likely class
		//
		Map<String, Double> probs = new LinkedHashMap<String, Double>();

		for (String clazz : classLikeli.keySet()) {
			Double likelihood = classLikeli.get(clazz) / totalLikelihood;
			probs.put(clazz, likelihood);
			log.debug("probability for {} is {}", clazz, likelihood);
			if (maxClass == null || likelihood > max) {
				maxClass = clazz;
				max = likelihood;
			}
		}

		return probs;
	}

	/**
	 * @see stream.learner.PredictionService#predict(stream.data.Data)
	 */
	@Override
	public Serializable predict(Data item) {
		Map<String, Double> probs = this.vote(item);
		Double max = 0.0d;
		String maxClass = null;
		Double confidence = 0.0d;

		for (String clazz : probs.keySet()) {
			Double likelihood = probs.get(clazz);
			log.debug("probability for {} is {}", clazz, likelihood);
			// item.put( LearnerUtils.hide( "pred(" + clazz + ")" ), likelihood
			// );
			if (maxClass == null || likelihood > max) {
				maxClass = clazz;
				if (max != null)
					confidence = 1.0d - Math.abs(likelihood - max);
				else
					confidence = 1.0d;
				max = likelihood;
			}
		}

		log.info("Predicting class {}, label is: {}, confidence-gap: "
				+ confidence + " wop=" + wop, maxClass, item.get(labelKey));
		return maxClass;
	}

	public String getNominalCondition(String attribute, Data item) {
		return attribute + "='" + item.get(attribute) + "'";
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Data process(Data item) {

		//
		// determine the label attribute, if not already set
		//
		if (labelKey == null || "".equals(labelKey.trim())) {
			for (String name : item.keySet())
				if (name.startsWith("@label")) {
					labelKey = name;
					log.info("Using label '{}'");
					break;
				}
		}

		if (item.get(labelKey) == null) {
			log.warn("Not processing unlabeled data item {}", item);
			return item;
		}

		Serializable cv = item.get(labelKey);
		if (cv == null)
			return item;

		String clazz = cv.toString();
		log.debug("Learning from example with label={}", clazz);
		if (this.classDistribution == null)
			this.classDistribution = new NominalDistributionModel<String>(); // this.createNominalDistribution();

		if (log.isDebugEnabled()) {
			log.debug("Classes: {}", classDistribution.getElements());
			for (String t : classDistribution.getElements())
				log.debug("    {}:  {}", t, classDistribution.getCount(t));
		}
		//
		// For learning we update the distributions of each attribute
		//
		for (String attribute : item.keySet()) {

			if (attribute.equalsIgnoreCase(labelKey)) {
				//
				// adjust the class label distribution
				//
				classDistribution.update(clazz);

			} else {

				Object obj = item.get(attribute);
				if (obj != null) {
					if (obj.getClass().equals(Double.class)) {
						Double value = (Double) obj;
						log.debug(
								"Handling numerical case ({}) with value  {}",
								obj, value);
						//
						// manage the case of an numerical attribute
						//
						Distribution<Double> numDist = (Distribution<Double>) distributions
								.get(attribute);
						if (numDist == null) {
							numDist = this.createNumericalDistribution();
							log.debug(
									"Creating new numerical distribution model for attribute {}",
									attribute);
							distributions.put(attribute, numDist);
						}
						numDist.update(value);

					} else {

						String value = this
								.getNominalCondition(attribute, item);
						log.debug("Handling nominal case for [ {} | {} ]",
								value, "class=" + clazz);

						//
						// adapt the nominal distribution for this attribute
						//
						Distribution<String> nomDist = (Distribution<String>) distributions
								.get(clazz);
						if (nomDist == null) {
							nomDist = this.createNominalDistribution();
							log.debug(
									"Creating new nominal distribution model for attribute {}, {}",
									attribute, "class=" + clazz);
							distributions.put(clazz, nomDist);
						}
						nomDist.update(value);
					}
				}
			}
		}

		return item;
	}

	/**
	 * Returns the class distribution of the current state of the algorithm.
	 * 
	 * @return
	 */
	public Distribution<String> getClassDistribution() {
		if (classDistribution == null)
			classDistribution = createNominalDistribution();

		return this.classDistribution;
	}

	/**
	 * <p>
	 * Returns the set of numerical distributions of this model.
	 * </p>
	 * 
	 * @return The set of numerical distributions, currently known to this
	 *         classifier.
	 */
	@SuppressWarnings("unchecked")
	public List<Distribution<Double>> getNumericalDistributions() {
		List<Distribution<Double>> numDists = new ArrayList<Distribution<Double>>();
		for (Distribution<?> d : distributions.values()) {
			if (d instanceof NumericalDistributionModel)
				numDists.add((Distribution<Double>) d);
		}

		return numDists;
	}

	/**
	 * <p>
	 * This method creates a new distribution model for nominal values. It can
	 * be overwritten by subclasses to make use of a more
	 * sophisticated/space-limited assessment of nominal distributions.
	 * </p>
	 * 
	 * @return A new, empty distribution model.
	 */
	public Distribution<String> createNominalDistribution() {
		return new NominalDistributionModel<String>();
	}

	/**
	 * <p>
	 * This method creates a new distribution model for numerical data.
	 * </p>
	 * 
	 * @return A new, empty distribution model.
	 */
	public Distribution<Double> createNumericalDistribution() {
		return new NumericalDistributionModel(1000, 1.0);
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
	}

	/**
	 * @see stream.StatefulProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext context) throws Exception {
	}

	/**
	 * @see stream.StatefulProcessor#resetState()
	 */
	@Override
	public void resetState() throws Exception {
	}

	/**
	 * @see stream.StatefulProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
	}

	@Override
	public String getName() throws RemoteException {
		return "NaiveBayes";
	}
}