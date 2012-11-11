/**
 * 
 */
package stream.io;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import stream.Data;
import stream.Processor;

/**
 * @author chris
 * 
 */
public class DataObjectStream implements DataStream {

	SourceURL url;
	String id;
	ObjectInputStream input;
	final List<Processor> processors = new ArrayList<Processor>();

	public DataObjectStream(SourceURL url) {
		this.url = url;
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() throws Exception {
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see stream.io.DataStream#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see stream.io.DataStream#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return new LinkedHashMap<String, Class<?>>();
	}

	/**
	 * @see stream.io.DataStream#init()
	 */
	@Override
	public void init() throws Exception {
		input = new ObjectInputStream(url.openStream());
	}

	/**
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return (Data) input.readObject();
	}

	/**
	 * @see stream.io.DataStream#readNext(stream.Data)
	 */
	@Override
	public Data readNext(Data datum) throws Exception {
		return readNext();
	}

	/**
	 * @see stream.io.DataStream#getPreprocessors()
	 */
	@Override
	public List<Processor> getPreprocessors() {
		return processors;
	}
}
