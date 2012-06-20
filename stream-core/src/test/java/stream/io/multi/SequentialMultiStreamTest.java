package stream.io.multi;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.generator.Gaussian;
import stream.generator.GaussianStream;

public class SequentialMultiStreamTest {

	static Logger log = LoggerFactory.getLogger( SequentialMultiStreamTest.class );

	@Test
	public void test() throws Exception {

		try {

			SequentialMultiStream stream = new SequentialMultiStream();

			GaussianStream g1 = new GaussianStream();
			g1.setGenerator( "x1", new Gaussian( -500.0, 0.5 ) );
			g1.setGenerator( "x2", new Gaussian( -100.0, 0.1 ) );
			g1.setLimit( 4L );

			GaussianStream g2 = new GaussianStream();
			g2.setGenerator( "x1", new Gaussian( 500.0, 0.1 ) );
			g2.setGenerator( "x2", new Gaussian( 100.0, 0.0 ) );
			g2.setLimit( 2L );

			stream.addStream( "G1", g1 );
			stream.addStream( "G2", g2 );

			stream.init();

			int count = 0;
			Data item = null;
			do {
				item = stream.readNext();
				log.info( "item: {}", item );
				if( item != null )
					count++;

			} while( item != null );

			log.info( "Read {} items", count );
		} catch (Exception e) {
			fail( "Test failed: " + e.getMessage() );
		}
	}

}
