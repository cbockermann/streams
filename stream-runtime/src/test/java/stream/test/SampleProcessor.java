/**
 * 
 */
package stream.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.Keys;
import stream.ProcessContext;

/**
 * @author chris
 * 
 */
public class SampleProcessor extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(SampleProcessor.class);
	Keys keys;
	String[] values;

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		log.info("keys: {}", keys);
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		return null;
	}

	/**
	 * @return the keys
	 */
	public Keys getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	public void setKeys(Keys keys) {
		log.info("Keys set: {}", keys);
		this.keys = keys;
	}

	/**
	 * @return the values
	 */
	public String[] getValues() {
		return values;
	}

	/**
	 * @param values
	 *            the values to set
	 */
	public void setValues(String[] values) {
		log.info("Values: {}", (Object) values);
		this.values = values;
	}

}
