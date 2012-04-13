/**
 * 
 */
package stream.data.test;

import stream.ConditionedProcessor;
import stream.annotations.Description;
import stream.data.Data;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Monitoring", name = "Print Data")
public class PrintData extends ConditionedProcessor {

	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {

		if (data == null)
			return null;

		System.out.println("data-item: " + data);
		return data;
	}
}