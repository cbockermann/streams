/**
 * 
 */
package stream.io;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class JDBCUrlTest {

	static Logger log = LoggerFactory.getLogger(JDBCUrlTest.class);

	@Test
	public void test() {

		try {
			String jdbc = "jdbc:mysql://dbuser:dbpass@localhost:3306/dbname?autoReconnect=true";
			SourceURL url = new SourceURL(jdbc);

			String host = url.getHost();
			log.info("Database host is: {}", url.getHost());
			log.info("Database port is: {}", url.getPort());
			log.info("Database name is: {}", url.getPath());
			log.info("Database user is: {}", url.getUsername());
			log.info("Database password is: {}", url.getPassword());
			log.info("Database parameters are: {}", url.getParameters());
			Assert.assertEquals("localhost", host);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Parsing test of JDBC URL failed: " + e.getMessage());
		}
	}
}
