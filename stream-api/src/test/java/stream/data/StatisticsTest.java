/**
 * 
 */
package stream.data;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class StatisticsTest {

	static Logger log = LoggerFactory.getLogger(StatisticsTest.class);

	@Test
	public void test() {

		int rounds = 2000;
		List<Statistics> stats = new ArrayList<Statistics>();
		for (int r = 0; r < rounds; r++) {
			stats.add(createRandomStats(100));
		}

		log.debug("{} statistic vectors created", rounds);
		Statistics st = new Statistics();
		Long start = System.currentTimeMillis();
		for (Statistics s : stats) {
			st.add(s);
		}
		Long end = System.currentTimeMillis();
		log.info("Adding {} elements required {} ms", rounds, (end - start));
		log.info("Result: {}", st);
	}

	private Statistics createRandomStats(int attrs) {
		Statistics st = new Statistics();
		for (int i = 0; i < attrs; i++) {
			String key = "att_" + (i % attrs);
			Double rnd = Math.random();
			st.add(key, rnd);
		}
		return st;
	}
}