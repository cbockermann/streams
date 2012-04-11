package stream.io.active;

import stream.io.DataStream;

/**
 * 
 * @author Hendrik Blom
 * 
 */
public interface ActiveDataStream extends DataStream {

	public void activate() throws Exception;
}
