/**
 * 
 */
package stream.data;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;
import stream.test.SampleProcessor;

/**
 * @author chris
 * 
 */
public class KeysParsingTest {
	static Logger log = LoggerFactory.getLogger(KeysParsingTest.class);

	@Test
	public void test() {
		URL url = KeysParsingTest.class.getResource("/keys-parsing.xml");
		try {
			ProcessContainer container = new ProcessContainer(url);
			stream.Process p = container.getProcesses().get(0);
			SampleProcessor sample = (SampleProcessor) p.getProcessors().get(0);

			String ks = "key1,key2,key3";
			Assert.assertEquals(ks, sample.getKeys().toString());

			String[] evs = "value1,                          value2,   value3"
					.split(",");

			Assert.assertTrue(evs.length == sample.getValues().length);
			for (int i = 0; i < evs.length; i++) {
				String v = sample.getValues()[i];
				String e = evs[i];
				log.info("Checking '{}' against '{}'", e, v);
				Assert.assertEquals(e, v);
			}

			// stream.run.main(url);
		} catch (Exception e) {
			fail("Test failed: " + e.getMessage());
		}
	}
}
