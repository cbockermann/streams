package stream.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.rpc.ContainerAnnouncement;
import stream.runtime.rpc.Discovery;

public class TestDiscovery {

	static Logger log = LoggerFactory.getLogger( TestDiscovery.class );

	public static void main( String[] args ) throws Exception {
		Discovery discovery = new Discovery();

		while( true ){
			ContainerAnnouncement an = discovery.discover();
			//log.info( "Found container '{}' at: " + an.getProtocol() + "://" + an.getHost() + ":" + an.getPort(), an.getName() );
			discovery.printContainers();
			Thread.sleep( 1000 );
		}
	}
}
