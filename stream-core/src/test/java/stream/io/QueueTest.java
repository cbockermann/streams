package stream.io;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;

public class QueueTest {

	@Test
	public void test() {
		try {
			
			URL url = QueueTest.class.getResource( "/queue-test.xml" );
			stream.run.main( url );
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error: " + e.getMessage() );
		}
	}

}
