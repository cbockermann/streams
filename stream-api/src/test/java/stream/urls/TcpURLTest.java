/**
 * 
 */
package stream.urls;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.SourceURL;

/**
 * @author chris
 * 
 */
public class TcpURLTest {

	static Logger log = LoggerFactory.getLogger(TcpURLTest.class);

	@Test
	public void test() {

		try {

			RandomDataServer server = new RandomDataServer(100);
			server.start();
			String tcp = "tcp://" + server.getLocalAddress() + ":"
					+ server.getLocalPort();
			log.info("TCP URL is: {}", tcp);

			SourceURL url = new SourceURL(tcp);
			log.debug("SourceURL is: {}", url);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));
			int cnt = 0;
			String line = reader.readLine();
			while (line != null) {
				log.info("line[{}]: {}", cnt, line);
				line = reader.readLine();
				cnt++;
			}

		} catch (Exception e) {
			fail("Test failed: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
