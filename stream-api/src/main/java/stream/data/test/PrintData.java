/**
 * 
 */
package stream.data.test;

import stream.data.ConditionedDataProcessor;
import stream.data.Data;
import stream.util.Description;

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
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {

		if (data == null)
			return null;

		System.out.println("data-item: " + data);
		return data;
	}
}