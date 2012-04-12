/**
 * 
 */
package stream.data.test;

import stream.ConditionedDataProcessor;
import stream.data.Data;
import stream.runtime.annotations.Description;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
@Description(group = "Data Stream.Monitoring", name = "Print Data")
public class PrintData extends ConditionedDataProcessor {

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