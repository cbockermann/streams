/**
 * 
 */
package stream.flow;

import stream.ConditionedProcessor;
import stream.Data;

/**
 * @author chris
 * 
 */
public class ConditionProcessorMock extends ConditionedProcessor {

	/**
	 * @see stream.ConditionedProcessor#processMatchingData(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {
		return data;
	}
}
