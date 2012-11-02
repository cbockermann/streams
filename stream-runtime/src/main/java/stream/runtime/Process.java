/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
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
	String output;

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
	 * @return the output
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public void setOutput(String output) {
		this.output = output;
	}

	/**
	 * @see stream.runtime.DataStreamConsumer#setDataStream(stream.io.DataStream)
	 */
	public void setDataStream(DataStream ds) {
		dataStream = ds;
	}

	/**
	 * 
	 * @return
	 */
	public DataStream getDataStream() {
		return dataStream;
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

			Data item = null;
			int tries = 0;
			while (item == null && tries < 10) {
				try {
					log.trace("Reading next item from {}", dataStream);
					synchronized (dataStream) {
						item = dataStream.readNext();
					}
					tries = 0;
					return item;
				} catch (Exception e) {
					e.printStackTrace();
					tries++;
				}
			}
			return item;
		} catch (Exception e) {
			e.printStackTrace();
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