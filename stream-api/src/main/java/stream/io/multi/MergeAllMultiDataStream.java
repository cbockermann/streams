package stream.io.multi;

import stream.data.Data;
import stream.data.DataImpl;
import stream.io.DataStream;

/**
 * *
 * <p>
 * A simple multi stream implementation, that merges the items of the
 * substreams.
 * </p>
 * 
 * @author Hendrik Blom
 * 
 */
public class MergeAllMultiDataStream extends AbstractMultiDataStream {

	@Override
	protected Data readItem(Data item) throws Exception {
		if (item == null)
			item = new DataImpl();

		boolean stop = true;

		for (DataStream s : streams.values()) {
			Data d = s.readNext(item);
			if (d != null) {
				item.putAll(d);
				stop = false;
			}
		}
		if (stop)
			return null;
		return item;
	}
}
