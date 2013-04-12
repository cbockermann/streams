/**
 * 
 */
package stream.flow;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;

/**
 * @author chris
 * 
 */
public class ConditionPerformanceTest {

	static Logger log = LoggerFactory.getLogger(ConditionPerformanceTest.class);

	@Test
	public void test() {

		Data item = DataFactory.create();
		item.put("x", 2.0d);
		item.put("t", 4.0d);
		item.put("y", "test");

		Skip skip = new Skip();
		skip.setCondition("%{data.x} < 4.0 and %{data.y} != null and %{data.x} > %{data.t}");

		Long start = System.currentTimeMillis();
		int rounds = 1000000;

		for (int i = 0; i < rounds; i++) {
			skip.process(item);
		}

		Long end = System.currentTimeMillis();
		log.info("Processed {} items in {} ms", rounds, (end - start));
		log.info("Rate is {}/second", rounds / ((end - start) / 1000.0d));

	}

}
