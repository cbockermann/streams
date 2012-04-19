/**
 * 
 */
package stream.flow;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataFactory;
import stream.expressions.ExpressionResolver;

/**
 * @author Hendrik Blom
 * 
 */
public class CreateAndMultiEnqueue extends MultiEnqueue {

	static Logger log = LoggerFactory.getLogger(CreateAndMultiEnqueue.class);
	String ref = null;

	protected String[] keys = null;

	public CreateAndMultiEnqueue() {
		super();

	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String[] getKeys() {
		return keys;
	}

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (queues == null || queues.length == 0) {
			log.error("No QueueService injected!");
			return data;
		}

		Data result = DataFactory.create();
		for (String key : keys) {
			Object o = ExpressionResolver.resolve(key, context, data);
			if (o != null) {
				String[] s = ExpressionResolver.extractName(key);
				result.put(s[1], create(o));
			}
		}
		enqueue(result);
		return data;
	}

	private Serializable create(Object object) {
		if (isNumeric(object))
			return new Double(object.toString());
		return object.toString();
	}

	public boolean isNumeric(Object val) {

		if (val instanceof Double) {
			return true;
		}

		if (val == null)
			return false;

		try {
			new Double(val.toString());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
