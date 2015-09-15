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
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ConditionedProcessor;
import stream.Data;
import stream.io.QueueService;

/**
 * This processor will send items to the specified queues and stop processing,
 * i.e. it works similar to the <code>Enqueue</code> processor, except that the
 * latter passes on the item to subsequent processors.
 * 
 * @author Christian Bockermann
 * 
 */
public class Redirect extends ConditionedProcessor {

	static Logger log = LoggerFactory.getLogger(Redirect.class);
	QueueService queues[];

	/**
	 * @return the queues
	 */
	public QueueService[] getQueues() {
		return queues;
	}

	/**
	 * @param queues
	 *            the queues to set
	 */
	public void setQueues(QueueService[] queues) {
		this.queues = queues;
	}

	public void setQueue(QueueService queue) {
		setQueues(new QueueService[] { queue });
	}

	/**
	 * @see stream.ConditionedProcessor#processMatchingData(stream.Data)
	 */
	@Override
	public Data processMatchingData(Data data) {

		if (queues == null)
			return null;

		for (QueueService queue : queues) {
			try {
				if (queue.write(data)) {
					log.debug("Redirected item to {}", queue);
				} else {
					log.error("Failed to redirect item to {}", queue);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
