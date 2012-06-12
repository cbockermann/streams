package stream.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.rpc.Naming;

public class NamingTest {
	
	static Logger log = LoggerFactory.getLogger( NamingTest.class );

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		EchoService serviceImpl = new EchoServiceImpl();
		
		Naming.bind( "echo", serviceImpl );
		Long start = System.currentTimeMillis();
		Long count = 100000L;
		for( int i = 0; i < count; i++ ){
			EchoService service = Naming.lookup( "echo", EchoService.class );
			service.echo("Hello!?");
		}
		Long end = System.currentTimeMillis();
		Double duration = end.doubleValue() - start.doubleValue();
		log.info( "{} invocations required {} ms", count, duration);
		Double seconds = duration / 1000.0d;
		log.info( " {} invocations per second", count / seconds );
		//log.info( "Service is: {}", service );
		//log.info( "Result: {}", service.echo( "Hello!?" ) );
	}
}
