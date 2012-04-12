/**
 * 
 */
package stream.runtime.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractDataProcessor;
import stream.data.Data;
import stream.util.SizeOf;

/**
 * @author chris
 * 
 */
public class MemorySize extends AbstractDataProcessor {

	static Logger log = LoggerFactory.getLogger(MemorySize.class);
	String ref = null;
	String prefix = "@memory";

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
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		try {
			Object p = context.lookup(getRef());
			log.debug("checking memory of processor {}", p);
			Double size = SizeOf.sizeOf(p);
			input.put(prefix + ":" + getRef(), size);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return input;
	}

}
