/**
 * 
 */
package stream.runtime;

import stream.data.Data;
import stream.data.DataImpl;

/**
 * @author chris
 * 
 */
public class Monitor extends AbstractProcess {

	/**
	 * @see stream.runtime.AbstractProcess#getNextItem()
	 */
	@Override
	public Data getNextItem() {
		if (lastItem == null) {
			lastItem = new DataImpl();
		}
		return lastItem;
	}
}