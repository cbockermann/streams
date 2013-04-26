package stream.storm;

import stream.Data;
import stream.io.Sink;
import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Values;

/**
 * 
 * @author Thomas Scharrenbach
 * @version 0.9.10
 * @since 0.9.10
 * 
 */
public class SinkOutputCollector implements Sink {

	private OutputCollector outputCollector;
	private String id;

	@Override
	public boolean offer(Data d) {
		return true;
	}

	@Override
	public boolean write(Data item) throws Exception {
		outputCollector.emit(new Values(item));

		return true;
	}

	@Override
	public boolean write(Data[] data) throws Exception {
		for (Data item : data) {
			this.write(item);
		}
		return true;
	}

	/**
	 * Does currently nothing.
	 */
	@Override
	public void close() throws Exception {
		return;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return this.id;
	}

}
