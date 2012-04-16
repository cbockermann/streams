/**
 * 
 */
package stream.plugin;

import stream.io.DataStream;
import stream.plugin.sources.DataStreamReader;

import com.rapidminer.operator.OperatorDescription;

/**
 * @author chris
 * 
 */
public class GenericStreamReader extends DataStreamReader {

	/**
	 * @param description
	 * @param streamClass
	 */
	public GenericStreamReader(OperatorDescription description,
			Class<? extends DataStream> streamClass) {
		super(description, streamClass);
	}
}
