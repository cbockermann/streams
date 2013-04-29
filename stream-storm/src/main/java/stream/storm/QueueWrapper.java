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
import stream.io.Sink;
import backtype.storm.task.OutputCollector;

/**
 * @author chris
 * 
 */
public class QueueWrapper implements Sink, Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 5528349910849921579L;

	static Logger log = LoggerFactory.getLogger(QueueWrapper.class);

	final OutputCollector collector;
	final String name;

	public QueueWrapper(OutputCollector collector, String name) {
		this.collector = collector;
		this.name = name;
		log.info("Creating QueueWrapper for queue '{}'", name);
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
		log.info("Writing to queue '{}' item {} to '{}'", name, item);
		log.info("   using collector {}", collector);
		List<Object> tuple = new ArrayList<Object>();
		tuple.add(item);
		collector.emit(tuple);
		return true;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean write(Collection<Data> data) throws Exception {
		// TODO Auto-generated method stub
		return false;
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
}
