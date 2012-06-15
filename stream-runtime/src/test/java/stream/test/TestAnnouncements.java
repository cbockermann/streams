package stream.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.rpc.Announcer;
import stream.runtime.rpc.ContainerAnnouncement;

public class TestAnnouncements {

	static Logger log = LoggerFactory.getLogger( TestAnnouncements.class );
	
	public static void main( String[] args ) throws Exception {
		
		ContainerAnnouncement an = new ContainerAnnouncement( "ContainerB", "rmi", "127.0.0.1", 9105 );
		Announcer discovery = new Announcer( 9200, an );
		discovery.start();
		
		log.info( "Waiting for discovery to finish..." );
		discovery.join();
	}
}
