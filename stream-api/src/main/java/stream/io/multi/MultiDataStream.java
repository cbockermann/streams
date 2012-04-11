package stream.io.multi;

import java.util.Map;

import stream.io.DataStream;

/**
 * @author Hendrik Blom
 * 
 */
public interface MultiDataStream extends DataStream {

	public void addStream(String id, DataStream stream);

	public Map<String, DataStream> getStreams();

}
