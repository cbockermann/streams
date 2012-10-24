/**
 * 
 */
package stream.script;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.data.DataFactory;
import stream.data.ProcessContextMock;

/**
 * @author chris
 * 
 */
public class JavaScriptTest {

	static Logger log = LoggerFactory.getLogger(JavaScriptTest.class);

	@Test
	public void test() {

		URL url = JavaScriptTest.class.getResource("/test.js");

		JavaScript script = new JavaScript();
		script.setFile(new File(url.getFile()));

		try {
			ProcessContext ctx = new ProcessContextMock();
			ctx.set("stamp-value", "ProcessContextMock object");
			script.init(ctx);

			Data item = DataFactory.create();
			item.put("@timestamp", System.currentTimeMillis());

			log.info("initial item: {}", item);
			item = script.process(item);
			log.info("processed item: {}", item);

			item.clear();

			item = script.process(item);

			Assert.assertNotNull(item.get("count"));
			Assert.assertEquals("2.0", item.get("count").toString());

			log.info("2nd time: {}", item);

		} catch (Exception e) {
			fail("Error: " + e.getMessage());
		}
	}
}
