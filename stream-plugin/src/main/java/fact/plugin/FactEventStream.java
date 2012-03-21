/**
 * 
 */
package fact.plugin;

import stream.data.Data;
import stream.io.DataStream;
import stream.plugin.DataObject;
import stream.plugin.DataSourceObject;

/**
 * @author chris
 * 
 */
public class FactEventStream extends DataSourceObject {

	/** The unique class ID */
	private static final long serialVersionUID = 1611811690868611841L;

	/**
	 * @param stream
	 */
	public FactEventStream(DataStream stream) {
		super(stream);
	}

	/**
	 * @see stream.plugin.DataSourceObject#wrap(stream.data.Data)
	 */
	@Override
	public DataObject wrap(Data item) {
		return new FactEventObject(item);
	}
}