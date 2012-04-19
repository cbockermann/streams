package stream.data.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.annotations.Parameter;
import stream.data.Data;
import stream.expressions.ExpressionResolver;
import stream.service.LookupService;

/**
 * 
 * @author Hendrik Blom &lt;hendrik.blom@udo.edu&gt;
 * 
 */
public class Lookup extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(Lookup.class);

	protected LookupService lookup;
	protected String key;

	/**
	 * @return lookupService
	 */
	public LookupService getLookup() {
		return lookup;
	}

	/**
	 * @param lookup
	 */
	public void setLookup(LookupService lookup) {
		this.lookup = lookup;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	@Parameter(defaultValue = "@id")
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data item) {
		if (lookup == null) {
			log.error("No LookupService injected!");
			return item;
		}
		String varib = ExpressionResolver.resolve(key, context, item)
				.toString();
		Data lookupData = lookup.lookup(varib);
		item.putAll(lookupData);
		return item;
	}
}