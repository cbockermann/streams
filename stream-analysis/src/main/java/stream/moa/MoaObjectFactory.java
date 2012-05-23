/**
 * 
 */
package stream.moa;

import java.util.Map;

import moa.classifiers.Classifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.setup.ObjectCreator;

/**
 * @author chris
 * 
 */
public class MoaObjectFactory implements ObjectCreator {

	static Logger log = LoggerFactory.getLogger(MoaObjectFactory.class);

	/**
	 * @see stream.runtime.setup.ObjectCreator#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return "moa";
	}

	/**
	 * @see stream.runtime.setup.ObjectCreator#create(java.lang.String,
	 *      java.util.Map)
	 */
	@Override
	public Object create(String className, Map<String, String> parameters)
			throws Exception {
		log.debug(
				"Creating MOA wrapper object for class '{}', with parameters: {}",
				className, parameters);

		MoaProcessor proc;
		Class<?> clazz = Class.forName(className);
		if (Classifier.class.isAssignableFrom(clazz)) {
			proc = new MoaClassifier(clazz);
		} else
			throw new Exception("MOA class of type " + clazz.getCanonicalName()
					+ " is currently not supported!");

		proc.setParameters(parameters);
		log.debug("MOA wrapper is {}", proc);
		return proc;
	}
}
