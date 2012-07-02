/**
 * 
 */
package stream.moa;

import java.util.Map;

import moa.classifiers.AbstractClassifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Measurable;
import stream.ProcessContext;
import stream.data.Data;
import weka.core.Instance;

/**
 * <p>
 * This class is a generic wrapper for MOA operators.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class MoaProcessor extends AbstractProcessor implements Measurable {

	static Logger log = LoggerFactory.getLogger(MoaProcessor.class);
	final Class<?> moaClass;
	AbstractClassifier classifier;

	protected final DataInstanceFactory instanceWrapper = new DataInstanceFactory();
	String id;

	public MoaProcessor(Class<?> moaClass) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		this.moaClass = moaClass;

		log.debug("Creating new MoaProcessor for class {}", moaClass);
		classifier = (AbstractClassifier) moaClass.newInstance();

		log.debug("MOA object is {}", classifier);
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);

		classifier.prepareForUse();
	}

	public void setParameters(Map<String, String> params) throws Exception {
		MoaParameterFinder.injectParams(params, classifier);

		if (params.containsKey("id")) {
			setId(params.get("id"));
		}
	}

	public Map<String, Class<?>> getParameters() throws Exception {
		return MoaParameterFinder.findParams(classifier.getClass());
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		DataInstance instance = wrap(input);
		processInstance(instance);
		return input;
	}

	public DataInstance wrap(Data input) {
		return instanceWrapper.wrap(input);
	}

	public void processInstance(Instance instance) {

	}

	/**
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();
	}

	/**
	 * @see stream.Measurable#getByteSize()
	 */
	@Override
	public double getByteSize() {
		return classifier.measureByteSize();
	}
}