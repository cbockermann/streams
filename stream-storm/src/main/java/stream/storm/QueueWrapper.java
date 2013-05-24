/**
 * 
 */
package stream.storm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.Queue;
import stream.io.Sink;
import backtype.storm.task.OutputCollector;

/**
 * @author chris
 * 
 */
public class QueueWrapper implements Queue, Sink, Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 5528349910849921579L;

	static Logger log = LoggerFactory.getLogger(QueueWrapper.class);

	final OutputCollector collector;
	final String name;

	public QueueWrapper(OutputCollector collector, String name) {
		this.collector = collector;
		this.name = name;
		log.debug("Creating QueueWrapper for queue '{}'", name);
	}

	/**
	 * @see stream.io.Sink#getId()
	 */
	@Override
	public String getId() {
		return name;
	}

	/**
	 * @see stream.io.Sink#write(stream.Data)
	 */
	@Override
	public boolean write(Data item) throws Exception {
		log.debug("Writing to queue '{}'  (item is: {})", name, item);
		log.debug("   using collector {}", collector);
		List<Object> tuple = new ArrayList<Object>();
		tuple.add(item);
		collector.emit(tuple);
		return true;
	}

	@Override
	public void close() throws Exception {

	}

	@Override
	public boolean write(Collection<Data> data) throws Exception {

		for (Data item : data) {
			List<Object> tuple = new ArrayList<Object>();
			tuple.add(item);
			collector.emit(tuple);
		}

		return true;
	}

	/**
	 * @see stream.io.Sink#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
	}

	/**
	 * @see stream.io.Sink#init()
	 */
	@Override
	public void init() throws Exception {

	}

	/**
	 * @see stream.io.Barrel#clear()
	 */
	@Override
	public int clear() {
		return 0;
	}

	/**
	 * @see stream.io.Source#read()
	 */
	@Override
	public Data read() throws Exception {
		return null;
	}

	/**
	 * @see stream.io.Queue#setSize(java.lang.Integer)
	 */
	@Override
	public void setSize(Integer limit) {
	}

	/**
	 * @see stream.io.Queue#getSize()
	 */
	@Override
	public Integer getSize() {
		return Integer.MAX_VALUE;
	}
}
