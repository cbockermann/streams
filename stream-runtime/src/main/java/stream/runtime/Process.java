/**
 * 
 */
package stream.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.DataStream;

/**
 * @author chris
 * 
 */
public class Process extends AbstractProcess implements DataStreamConsumer {

	static Logger log = LoggerFactory.getLogger(Process.class);
	static Integer LAST_ID = 0;
	DataStream dataStream;
	Long limit = -1L;
	String input;

	public Process() {
		this.interval = 0L;
	}

	/**
	 * @param input
	 *            the input to set
	 */
	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * @see stream.runtime.DataStreamConsumer#getInput()
	 */
	public String getInput() {
		return input;
	}

	/**
	 * @see stream.runtime.DataStreamConsumer#setDataStream(stream.io.DataStream)
	 */
	public void setDataStream(DataStream ds) {
		dataStream = ds;
	}

	/**
	 * @see stream.runtime.AbstractProcess#getNextItem()
	 */
	@Override
	public Data getNextItem() {
		try {

			if (limit > 0 && count > limit) {
				log.debug("Limit '{}' reached, no more data from the input will be processed.");
				return null;
			}

			log.trace("Reading next item from {}", dataStream);
			Data item = dataStream.readNext();
			return item;

		} catch (Exception e) {
			log.error("Failed to read next item from input '{}'", dataStream);
			throw new RuntimeException("Failed to read next item from input '"
					+ dataStream + "': " + e.getMessage());
		}
	}

	/**
	 * @return the limit
	 */
	public Long getLimit() {
		return limit;
	}

	/**
	 * @param limit
	 *            the limit to set
	 */
	public void setLimit(Long limit) {
		this.limit = limit;
	}
}