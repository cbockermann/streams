/**
 * 
 */
package stream.learner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.data.Processor;
import stream.util.Description;

/**
 * @author chris
 * 
 */
@Description(name = "Apply Model", group = "Data Stream.Mining")
public class ApplyModel extends AbstractDataProcessor {

	static Logger log = LoggerFactory.getLogger(ApplyModel.class);

	String ref;

	/**
	 * @return the ref
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * @param ref
	 *            the ref to set
	 */
	public void setRef(String ref) {
		this.ref = ref;
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		try {
			Processor p = context.lookup(ref);

			if (p instanceof ModelProvider) {
				ModelProvider<?> modelProvider = (ModelProvider<?>) p;
				log.debug("Found model-provider: {}", modelProvider);
				log.debug("Model is: {}", modelProvider.getModel());
				return modelProvider.getModel().process(data);
			} else {
				log.error(
						"Referenced processor '{}' is not a learner/does not provide a model!",
						p);
			}

		} catch (Exception e) {
			log.error("Failed to look up: {}", e.getMessage());
		}

		return data;
	}
}
