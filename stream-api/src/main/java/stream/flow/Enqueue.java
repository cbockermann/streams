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
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.data.Data;
import stream.data.DataFactory;
import stream.io.QueueService;

/**
 * @author chris
 * 
 */
public class Enqueue extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(Enqueue.class);
	String ref = null;

	protected QueueService[] queues;

	public void setQueue(QueueService queue) {
		this.queues = new QueueService[] { queue };
	}

	public void setQueues(QueueService[] queues) {
		this.queues = queues;
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		if (data == null)
			return data;

		enqueue(data);
		return data;
	}

	protected void enqueue(Data data) {

		if (queues == null) {
			log.error("No QueueService injected!");
			return;
		}

		for (int i = 0; i < queues.length; i++) {

			if (i < 1)
				queues[i].enqueue(data);
			else {
				queues[i].enqueue(DataFactory.create(data));
			}
		}
	}

	/**
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();
		log.debug("Sending EndOfStream item to all queues...");
		enqueue(Data.END_OF_STREAM);
	}
}