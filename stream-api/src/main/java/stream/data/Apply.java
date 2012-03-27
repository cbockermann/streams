/**
 * 
 */
package stream.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class Apply extends AbstractDataProcessor {

	static Logger log = LoggerFactory.getLogger(Apply.class);
	String ref = null;

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
	 * @see stream.data.AbstractDataProcessor#init(stream.data.Context)
	 */
	@Override
	public void init(Context ctx) throws Exception {
		super.init(ctx);
		if (ref == null) {
			throw new Exception("No reference found for '" + ref + "'!");
		}
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		try {

			Processor proc = context.lookup(getRef());
			log.debug("Lookup of ref {} returns: {}", ref, proc);
			if (proc != null)
				return proc.process(data);

		} catch (Exception e) {
			log.error("Failed to apply processor with reference '{}': {}",
					getRef(), e.getMessage());
		}

		return data;
	}
}