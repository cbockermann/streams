/**
 * 
 */
package stream.io.multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.data.Data;
import stream.data.DataImpl;
import stream.io.DataStream;
import stream.io.active.ActiveDataStreamImpl;

/**
 * <p>
 * A simple multi stream implementation.
 * </p>
 * 
 * @author Hendrik Blom
 * 
 */
public abstract class AbstractMultiDataStream implements MultiDataStream {

	static Logger log = LoggerFactory.getLogger(AbstractMultiDataStream.class);

	protected ArrayList<Processor> preprocessors;
	protected Map<String, Class<?>> attributes;

	protected Long limit = -1L;
	protected Long count = 0L;

	protected Map<String, DataStream> streams;

	public AbstractMultiDataStream() {
		this.attributes = new LinkedHashMap<String, Class<?>>();
		this.preprocessors = new ArrayList<Processor>();
		this.streams = new HashMap<String, DataStream>();
	}

	protected abstract Data readNext(Data item, Map<String, DataStream> streams)
			throws Exception;

	/**
	 * Returns the next datum from this stream.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Data readNext() throws Exception {
		return readNext(new DataImpl());
	}

	public final Data readNext(Data item) throws Exception {

		if (limit > 0 && count >= limit)
			return null;

		Data datum = null;
		while (datum == null) {

			//
			// If the source is empty (i.e. readItem(..) returned null), we
			// cannot continue, so we leave by returning null
			//
			datum = readNext(item, streams);
			if (datum == null) {
				log.debug("End-of-stream reached!");
				return null;
			}

			//
			// Hand over the item to all pre-processors. If one of them
			// discards the item, we will continue reading the next one.
			//
			for (Processor proc : preprocessors) {
				datum = proc.process(datum);
				if (datum == null)
					break;
			}
		}
		count++;
		return datum;
	}

	public void close() {
		for (DataStream s : streams.values())
			s.close();
	}

	@Override
	public void addStream(String id, DataStream stream) {
		streams.put(id, new ActiveDataStreamImpl(stream));
	}

	@Override
	public Map<String, DataStream> getStreams() {
		return streams;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	/**
	 * This method returns a mapping of attributes to types for all substreams.
	 * 
	 * @return
	 */
	public Map<String, Class<?>> getAttributes() {
		return this.attributes;
	}

	public List<Processor> getPreprocessors() {
		return this.preprocessors;
	}

	public void addPreprocessor(Processor proc) {
		preprocessors.add(proc);
	}

	public void addPreprocessor(int idx, Processor proc) {
		preprocessors.add(idx, proc);
	}

	public boolean removePreprocessor(Processor proc) {
		return preprocessors.remove(proc);
	}

	public Processor removePreprocessor(int idx) {
		return preprocessors.remove(idx);
	}

}