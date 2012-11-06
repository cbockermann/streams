/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
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