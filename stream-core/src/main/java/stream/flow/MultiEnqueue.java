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
package stream.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.ProcessContext;
import stream.data.Data;
import stream.io.QueueService;

/**
 * @author Hendrik Blom
 * 
 */
public class MultiEnqueue extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(MultiEnqueue.class);
	String ref = null;

	protected String[] queuesNames;
	protected QueueService[] queues;

	public void setQueues(String[] queues) {
		this.queuesNames = queues;
	}

	public String[] getKeys() {
		return queuesNames;
	}

	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		queues = new QueueService[queuesNames.length];
		for (int i = 0; i < queuesNames.length; i++) {
			queues[i] = (QueueService) context.lookup(queuesNames[i]);
		}
	}

	/**
	 * @see stream.Processor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		if (queues == null || queues.length == 0) {
			log.error("No QueueService injected!");
			return data;
		}

		enqueue(data);
		return data;
	}

	protected void enqueue(Data data) {
		for (QueueService qs : queues) {
			qs.enqueue(data);
		}
	}
}
