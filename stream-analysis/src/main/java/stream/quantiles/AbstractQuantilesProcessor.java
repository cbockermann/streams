/**
 * 
 */
package stream.quantiles;

import java.util.HashMap;
import java.util.Map;

import stream.AbstractProcessor;
import stream.data.Data;
import stream.quantiles.impl.QuantileLearner;

/**
 * @author chris
 * 
 */
public abstract class AbstractQuantilesProcessor extends AbstractProcessor
		implements QuantilesService {

	String[] keys;

	Map<String, QuantileLearner> learner = new HashMap<String, QuantileLearner>();

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		return input;
	}
}
