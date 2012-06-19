package stream.service;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.BlockingQueue;
import stream.runtime.rpc.ServiceProxy;

public class QueueProxyTest {

	static Logger log = LoggerFactory.getLogger( QueueProxyTest.class );
	
	@Test
	public void test() {
		
		BlockingQueue queue = new BlockingQueue( 100 );
		try {
			ServiceProxy proxy = new ServiceProxy( queue );
			log.info( "Created proxy: {}", proxy );
		} catch (Exception e) {
			fail("Error while creating service-proxy: " + e.getMessage() );
		}
	}
	
	public static void main( String[] args ){
		(new QueueProxyTest()).test();
	}
}