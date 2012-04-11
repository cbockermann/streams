package stream.io.multi;

import java.util.Map;

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

	/**
	 * @see stream.io.multi.AbstractMultiDataStream#readItem(stream.data.Data)
	 */
	@Override
	protected Data readNext(Data item, Map<String, DataStream> streams)
			throws Exception {
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
