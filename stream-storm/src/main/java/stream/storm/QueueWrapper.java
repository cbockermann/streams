/**
 * 
 */
package stream.storm;

import java.io.Serializable;
import java.util.ArrayList;
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
	public void write(Data item) throws Exception {
		List<Object> tuple = new ArrayList<Object>();
		tuple.add(item);
		collector.emit(name, tuple);
	}
}
