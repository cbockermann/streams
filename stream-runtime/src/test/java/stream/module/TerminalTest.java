package stream.module;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Test;

import stream.runtime.Container;
import stream.runtime.Containers;

/**
 * @author hendrik
 *
 */
public class TerminalTest {

	@Test
	public void test() throws IOException, InterruptedException,
			ExecutionException {

		Containers containers = new Containers(1);

		Map<String, String> props = new HashMap<String, String>();

		props.put(
				"container.properties",
				TerminalTest.class.getResource(
						"/module/TerminalTest.properties").toString());
		Container c = new Container(
				TerminalTest.class.getResource("/stream/module/server.xml"),
				props);
		containers.put("test", c);
		Future<Boolean> result = containers.start("test");
		if (result == null)
			Assert.fail();
		result.get();
	}
}
