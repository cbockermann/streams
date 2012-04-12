/**
 * 
 */
package stream.data;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;

/**
 * @author chris
 *
 */
public class ScriptTest {

	static Logger log = LoggerFactory.getLogger( ScriptTest.class );

	@Test
	public void test() {

		try {
			URL url = ProcessContainer.class.getResource( "/script-example.xml" );
			log.info( "Running experiment from {}", url );
			ProcessContainer runner = new ProcessContainer( url );
			runner.run();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Not yet implemented");
		}
	}
}