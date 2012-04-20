/*
 *  stream.ai
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  stream.ai is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The stream.ai library (and its submodules) is free software: you can 
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
package stream.io;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.data.Data;
import stream.data.DataListener;

/**
 * @author chris
 * 
 */
public class DataStreamQueue extends AbstractDataStream implements
		DataListener, Processor, QueueService {

	static Logger log = LoggerFactory.getLogger(DataStreamQueue.class);
	final LinkedBlockingQueue<Data> queue = new LinkedBlockingQueue<Data>();

	public DataStreamQueue() {
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
		queue.clear();
	}

	/**
	 * @see stream.io.AbstractDataStream#readHeader()
	 */
	@Override
	public void readHeader() throws Exception {
	}

	/**
	 * @see stream.io.AbstractDataStream#readItem(stream.data.Data)
	 */
	@Override
	public Data readItem(Data instance) throws Exception {
		Data item = queue.take();
		log.debug("took item from queue: {}", item);
		while (item == null) {
			try {
				log.debug("waiting for item to arrive in queue...");
				Thread.sleep(100);
				item = queue.take();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (item != null)
			instance.putAll(item);
		return instance;
	}

	/**
	 * @see stream.io.AbstractDataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		Data item = queue.take();
		log.debug("took item from queue: {}", item);
		while (item == null) {
			try {
				log.debug("waiting for item to arrive in queue...");
				Thread.sleep(100);
				item = queue.take();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return item;
	}

	/**
	 * @see stream.data.DataListener#dataArrived(stream.data.Data)
	 */
	@Override
	public void dataArrived(Data item) {
		queue.add(item);
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data input) {
		queue.add(input);
		return input;
	}

	/**
	 * @see stream.io.QueueService#poll()
	 */
	@Override
	public Data poll() {
		return queue.poll();
	}

	/**
	 * @see stream.io.QueueService#enqueue(stream.data.Data)
	 */
	@Override
	public void enqueue(Data item) {
		queue.add(item);
	}

	/**
	 * @see stream.service.Service#reset()
	 */
	@Override
	public void reset() throws Exception {
		log.debug("Cleared Queue.");
		queue.clear();

	}
}