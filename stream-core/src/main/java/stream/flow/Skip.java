/**
 * 
 */
package stream.flow;

import stream.ConditionedDataProcessor;
import stream.data.Data;
import stream.runtime.annotations.Description;

/**
 * <p>
 * This class provides a data process that will skip (i.e. return <code>null</code>)
 * all data items matching a given condition.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
@Description( text="",
			  group="Data Stream.Logic" )
public class Skip extends ConditionedDataProcessor {


	/**
	 * @see stream.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		if( this.matches( data ) )
			return null;
		
		return data;
	}

	/**
	 * @see stream.ConditionedDataProcessor#processMatchingData(stream.data.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {
		return data;
	}
}