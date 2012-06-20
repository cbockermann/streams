package stream.io.multi;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.DataStream;

public class SequentialMultiStream extends AbstractMultiDataStream {

	static Logger log = LoggerFactory.getLogger( SequentialMultiStream.class );
	
	String sourceKey = "@source";
	
	int index = 0;


	public String getSourceKey() {
		return sourceKey;
	}


	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}


	/**
	 * @see stream.io.multi.AbstractMultiDataStream#readNext(stream.data.Data, java.util.Map)
	 */
	@Override
	protected Data readNext(Data item, Map<String, DataStream> streams)
			throws Exception {

		Data data = null;

		while( ( data == null && index < additionOrder.size() ) ){
			try {
				String current = additionOrder.get( index );
				log.debug( "Current stream is: {}", current );
				DataStream currentStream = streams.get( current );
				data = currentStream.readNext( item );
				
				if( data != null ){
					data.put( sourceKey, current );
					log.debug( "   returning data {}", data );
					return data;
				}
				
				log.debug( "Stream {} ended, switching to next stream", current );
				index++;
				
				if( index >= additionOrder.size() ){
					log.debug( "No more streams to read from!" );
					return null;
				}
				
			} catch ( Exception e) {
				log.error( "Error: {}", e.getMessage() );
				if( log.isTraceEnabled() )
					e.printStackTrace();
			}
		}

		log.debug( "No more streams to read from - all seem to have reached their end." );
		return null;
	}
}