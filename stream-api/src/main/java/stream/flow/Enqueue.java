/**
 * 
 */
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.data.Processor;

/**
 * @author chris
 * 
 */
public class Enqueue extends AbstractDataProcessor {

	static Logger log = LoggerFactory.getLogger(Enqueue.class);
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
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		try {
			Processor p = context.lookup(ref);
			return p.process(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

}
