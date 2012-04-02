/**
 * 
 */
package stream.data.test;

import stream.data.ConditionedDataProcessor;
import stream.data.Data;

/**
 * @author chris
 * 
 */
public class Assert extends ConditionedDataProcessor {

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {
		return data;
	}

	/**
	 * @see stream.data.ConditionedDataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		if (!matches(data))
			throw new RuntimeException("Assertion '" + getCondition()
					+ "' failed for data item: " + data);
		return processMatchingData(data);
	}
}
