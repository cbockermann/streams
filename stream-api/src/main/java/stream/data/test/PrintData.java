/**
 * 
 */
package stream.data.test;

import stream.data.AbstractDataProcessor;
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
public class PrintData extends AbstractDataProcessor {

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (data == null)
			return null;

		System.out.println("data-item: " + data);
		return data;
	}
}