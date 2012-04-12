/**
 * 
 */
package stream.io;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.AbstractDataProcessor;
import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.Processor;
import stream.runtime.Context;

/**
 * <p>
 * This class implements a chain/list of data-processor instances, that are
 * applied to the data-items in order. The class implements the DataStream
 * interface itself, i.e. it can be used as a data-stream source to subsequent
 * listeners.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class DataStreamProcessor extends AbstractDataProcessor implements
		DataStream {
	/* A global logger for this class */
	static Logger log = LoggerFactory.getLogger(DataStreamProcessor.class);

	/* The source from which this processor chain reads */
	DataStream source;

	/* The attribute/type mapping for this stream */
	Map<String, Class<?>> finalAttributes = null;

	/* The list of data preprocessors */
	List<Processor> processors = new ArrayList<Processor>();

	public DataStreamProcessor() {
		source = null;
	}

	public DataStreamProcessor(DataStream stream) {
		source = stream;
	}

	/**
	 * @return the source
	 */
	public DataStream getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(DataStream source) {
		this.source = source;
	}

	public void addDataProcessor(Processor proc) {
		processors.add(proc);
	}

	public void removeDataProcessor(Processor proc) {
		processors.remove(proc);
	}

	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		if (finalAttributes != null)
			return finalAttributes;

		return source.getAttributes();
	}

	/**
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		Data data = source.readNext();
		if (data == null)
			return null;
		return process(data);
	}

	public Data readNext(Data data) throws Exception {
		return readNext();
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {

		log.trace("Processing item with {} processors", processors.size());

		Data data = input;
		if (input == null)
			return null;

		for (Processor p : processors) {
			log.trace("Processing item with processor {}", p);
			data = p.process(data);
			if (data == null)
				return null;
		}

		if (finalAttributes == null && data != null) {
			finalAttributes = new LinkedHashMap<String, Class<?>>();
			for (String key : data.keySet()) {
				if (data.get(key) != null) {
					if (data.get(key).getClass().equals(Double.class))
						finalAttributes.put(key, Double.class);
					else
						finalAttributes.put(key, String.class);

				}
			}
		}
		return data;
	}

	@Override
	public void addPreprocessor(Processor proc) {
		this.processors.add(proc);
	}

	@Override
	public void addPreprocessor(int idx, Processor proc) {
		this.processors.add(idx, proc);
	}

	@Override
	public List<Processor> getPreprocessors() {
		return processors;
	}

	public void close() {
	}

	/**
	 * @see stream.data.AbstractDataProcessor#init()
	 */
	@Override
	public void init(Context ctx) throws Exception {
		super.init(ctx);

		for (Processor p : processors) {
			if (p instanceof DataProcessor)
				((DataProcessor) p).init(ctx);
		}
	}

	/**
	 * @see stream.data.AbstractDataProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();

		for (Processor p : processors) {
			try {
				if (p instanceof DataProcessor)
					((DataProcessor) p).finish();
			} catch (Exception e) {
				log.error("Error while finishing processor '{}': {}", p,
						e.getMessage());
			}
		}
	}
}