package stream;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.ProcessContainer;

public class StreamRunnerTest {
	static Logger log = LoggerFactory.getLogger(StreamRunnerTest.class);

	// a test dummy
	@Test
	public void test() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		URL url = ProcessContainer.class.getResource("/multi.xml");
		log.info("Running experiment from {}", url);
		ProcessContainer runner = new ProcessContainer(url);
		runner.run();
	}
}