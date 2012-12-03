/**
 * 
 */
package stream.runtime;

import stream.Context;
import stream.Data;
import stream.io.Stream;

/**
 * @author chris
 * 
 */
public class StreamLifeCycle implements LifeCycle, Stream {

	final Stream stream;

	public StreamLifeCycle(Stream stream) {
		this.stream = stream;
	}

	/**
	 * @see stream.runtime.LifeCycle#init(stream.Context)
	 */
	@Override
	public void init(Context context) throws Exception {
	}

	/**
	 * @see stream.runtime.LifeCycle#finish()
	 */
	@Override
	public void finish() throws Exception {
		stream.close();
	}

	/**
	 * @return
	 * @see stream.io.Source#getId()
	 */
	public String getId() {
		return stream.getId();
	}

	/**
	 * @param id
	 * @see stream.io.Source#setId(java.lang.String)
	 */
	public void setId(String id) {
		stream.setId(id);
	}

	/**
	 * @throws Exception
	 * @see stream.io.Source#init()
	 */
	public void init() throws Exception {
		stream.init();
	}

	/**
	 * @return
	 * @throws Exception
	 * @see stream.io.Source#read()
	 */
	public Data read() throws Exception {
		return stream.read();
	}

	/**
	 * @throws Exception
	 * @see stream.io.Source#close()
	 */
	public void close() throws Exception {
		stream.close();
	}

	/**
	 * @return
	 * @see stream.io.Stream#getLimit()
	 */
	public Long getLimit() {
		return stream.getLimit();
	}

	/**
	 * @param limit
	 * @see stream.io.Stream#setLimit(java.lang.Long)
	 */
	public void setLimit(Long limit) {
		stream.setLimit(limit);
	}

}
