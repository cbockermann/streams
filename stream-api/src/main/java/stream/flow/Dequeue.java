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

import stream.AbstractProcessor;
import stream.Data;
import stream.data.DataFactory;
import stream.io.QueueService;

public class Dequeue extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger( Dequeue.class );
	QueueService queue;
	
	
	public QueueService getQueue() {
		return queue;
	}

	public void setQueue(QueueService queue) {
		this.queue = queue;
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(Data input) {
		
		if( queue != null ){
			
			if( input == null )
				input = DataFactory.create();
			
			Data item = queue.take();
			if( item != null ){
				log.debug( "Merging dequeued item '{}'", item );
				input.putAll( item );
			} else {
				log.error( "Dequeued 'null' from queue - unexpected?!" );
			}
		} else {
			log.debug( "No queue defined, not dequeuing anything..." );
		}
		
		return input;
	}
}
