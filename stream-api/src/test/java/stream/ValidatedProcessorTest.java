/**
 * 
 */
package stream;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.DataFactory;
import stream.data.ProcessContextMock;

/**
 * @author chris
 * 
 */
public class ValidatedProcessorTest {

	static Logger log = LoggerFactory.getLogger(ValidatedProcessorTest.class);

	@Test
	public void test() {

		VIdentity id = new VIdentity();

		id.setRequires("id:Long".split(","));

		Data item = DataFactory.create();
		item.put("id", 1L);

		try {
			id.init(new ProcessContextMock());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			log.info("Processing valid item...");
			id.process(item);
		} catch (Exception e) {
			fail("Processing threw exception!");
		}

		try {
			log.info("Processing invalid item...");
			item.remove("id");
			id.process(item);
			fail("Errorneous item not detected!");
		} catch (Exception e) {
			log.info("Validation successfully failed!");
		}
	}
}
