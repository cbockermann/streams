/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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
package stream.io.multi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.Stream;

/**
 * @author chris, hendrik
 * 
 */
public class SequentialMultiStream extends AbstractMultiStream {

	static Logger log = LoggerFactory.getLogger(SequentialMultiStream.class);

	String sourceKey = "@source";

	int index = 0;

	public String getSourceKey() {
		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	/**
	 * @see stream.io.multi.AbstractMultiStream#readNext(stream.Data,
	 *      java.util.Map)
	 */
	@Override
	public Data readNext() throws Exception {

		Data data = null;

		while ((data == null && index < additionOrder.size())) {
			try {
				String current = additionOrder.get(index);
				log.debug("Current stream is: {}", current);
				Stream currentStream = streams.get(current);
				data = currentStream.read();

				if (data != null) {
					data.put(sourceKey, current);
					log.debug("   returning data {}", data);
					return data;
				}

				log.debug("Stream {} ended, switching to next stream", current);
				index++;

				if (index >= additionOrder.size()) {
					log.debug("No more streams to read from!");
					return null;
				}

			} catch (Exception e) {
				log.error("Error: {}", e.getMessage());
				if (log.isTraceEnabled())
					e.printStackTrace();
			}
		}

		log.debug("No more streams to read from - all seem to have reached their end.");
		return null;
	}
}