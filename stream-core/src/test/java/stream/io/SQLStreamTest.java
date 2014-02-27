/**
 * 
 */
package stream.io;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.runtime.ProcessContextImpl;
import stream.test.TestStream;

/**
 * @author chris
 * 
 */
public class SQLStreamTest {

	static Logger log = LoggerFactory.getLogger(SQLStreamTest.class);
	String dbUrl = "jdbc:hsqldb:res:/test.db";
	String dbUser = "SA";
	String dbPass = "";

	@Before
	public void setup() throws Exception {

		dbUrl = "jdbc:hsqldb:res:/test.db";
		log.info("Creating test database at url {}", dbUrl);

		TestStream stream = new TestStream();

		SQLWriter writer = new SQLWriter();
		writer.setUrl(dbUrl);
		writer.setUsername(dbUser);
		writer.setPassword(dbPass);
		writer.setTable("TEST_TABLE");

		writer.init(new ProcessContextImpl());

		for (int i = 0; i < 100; i++) {
			Data item = stream.readNext();
			writer.process(item);
		}

		writer.finish();

	}

	/**
	 * Test method for {@link stream.io.AbstractStream#read()}.
	 */
	@Test
	public void testReadNext() {

		try {
			SQLStream stream = new SQLStream();
			stream.setUrl(new SourceURL(dbUrl));
			stream.setUsername(dbUser);
			stream.setPassword(dbPass);
			stream.setSelect("SELECT * FROM TEST_TABLE");

			stream.init();

			Data item = stream.read();
			while (item != null) {
				log.info("Read item: {}", item);
				item = stream.read();
			}

			stream.close();

		} catch (Exception e) {
			e.printStackTrace();
			fail("Error: " + e.getMessage());
		}
	}

}
