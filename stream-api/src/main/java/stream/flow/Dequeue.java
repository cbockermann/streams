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
