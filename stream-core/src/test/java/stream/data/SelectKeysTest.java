/**
 * 
 */
package stream.data;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;

/**
 * @author chris
 * 
 */
public class SelectKeysTest {

	static Logger log = LoggerFactory.getLogger(SelectKeysTest.class);

	@Test
	public void test() {

		SelectKeys selector = new SelectKeys();
		selector.setKeys(new String[] { "x1", "!x2", "user:*", "!user:name" });

		Processor check = new Processor() {
			@Override
			public Data process(Data input) {
				boolean ok = input.containsKey("x1")
						&& !input.containsKey("x2")
						&& input.containsKey("user:id")
						&& !input.containsKey("testKey");
				if (!ok)
					fail("Test failed. Unexpected set of keys found in data item: "
							+ input.keySet());
				else
					log.info("Selection works!");

				input.put("processed", "true");

				return input;
			}
		};

		selector.getProcessors().add(check);

		Data item = DataFactory.create();
		item.put("x1", 1.0);
		item.put("x2", 2.0);
		item.put("testKey", "streams for the world!");
		item.put("user:id", "chris");
		item.put("user:name", "Christian");

		item = selector.process(item);
		log.info("Data item: {}", item);
		// fail("Not yet implemented");
	}

}
