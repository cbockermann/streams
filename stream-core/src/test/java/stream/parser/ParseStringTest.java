/**
 * 
 */
package stream.parser;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.ProcessContext;
import stream.ProcessorList;
import stream.data.PrintData;
import stream.flow.Delay;
import stream.io.CyclicMockLineStream;
import stream.monitor.DataRate;
import stream.runtime.ProcessContextImpl;

/**
 * @author chris
 * 
 */
public class ParseStringTest {

	static Logger log = LoggerFactory.getLogger(ParseStringTest.class);
	final static long K = 1000L;
	final static long M = 1000L * K;
	final static long G = 1000L * M;

	@Test
	public void test() {

		try {
			URL url = ParseStringTest.class.getResource("/test-access.log");
			CyclicMockLineStream stream = new CyclicMockLineStream(url, 1000);

			stream.setLimit(100L * K);
			stream.init();

			ParseString parser = new ParseString();
			parser.setKey("LINE");
			parser.setFormat("%(REMOTE_ADDR) %(HOST) %(REMOTE_USER) [%(DAY)/%(MONTH)/%(YEAR):%(TIME)] \"%(METHOD) %(URI) %(PROTOCOL)\" %(STATUS) %(SIZE) \"%(d)\" \"%(USER_AGENT)\"");

			ProcessContext pc = new ProcessContextImpl();
			ProcessorList list = new ProcessorList();

			DataRate rate = new DataRate();
			rate.setId("dataRate");
			rate.setEvery(10000);

			ParseDouble pd = new ParseDouble();
			pd.setKeys("SIZE,STATUS".split(","));

			Delay delay = new Delay();
			delay.setTime("10ms");
			// list.add(delay);
			list.add(parser);
			// list.add(pd);
			list.add(rate);
			if (stream.getLimit() <= 10)
				list.add(new PrintData());
			list.init(pc);

			Long start = System.currentTimeMillis();
			Integer count = 0;
			Data item = stream.read();
			while (item != null) {
				count++;
				try {

					item = list.process(item);
					// log.info("Data item has {} values", item.size());

					item = stream.read();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Long end = System.currentTimeMillis();
			log.info("Processed {} items in {} ms", count, (end - start));
			log.info("Rate is {}/second", count / ((end - start) / 1000.0d));
			list.finish();

		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
}
