package stream.twitter;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataFactory;
import stream.io.AbstractDataStream;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.StatusStream;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class TweetStream extends AbstractDataStream implements StatusListener {

	static Logger log = LoggerFactory.getLogger( TweetStream.class );
	
	StatusStream stream;
	
	final LinkedBlockingQueue<Data> items = new LinkedBlockingQueue<Data>();
	
	
	@Override
	public void init() throws Exception {
		super.init();
		
		TwitterStreamFactory tsf = new TwitterStreamFactory();
		TwitterStream ts = tsf.getInstance();
		//stream.addListener( this );
		
		FilterQuery filter = new FilterQuery();
		filter.track( "em2012".split( "," ) );
		stream = ts.getFilterStream( filter ); //.filter( filter );
	}

	@Override
	public void close() throws Exception {
		//stream.shutdown();
	}

	@Override
	public void readHeader() throws Exception {
	}

	@Override
	public Data readItem(Data instance) throws Exception {
		
		
		Data item = items.take();
		instance.putAll( item );
		return instance;
	}

	@Override
	public void onException(Exception arg0) {
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
	}

	@Override
	public void onStatus(Status arg0) {
		
		Data item = DataFactory.create();
		item.put( "@id", arg0.getId() + "" );
		item.put( "@from", arg0.getUser().getName() );
		item.put( "@source", arg0.getSource() );
		item.put( "txt", arg0.getText() );

		items.add( item );
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
	}
}