/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
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

import java.text.DecimalFormat;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 * 
 */
public class SequenceIDTest {

	static Logger log = LoggerFactory.getLogger(SequenceIDTest.class);
	Integer limit = 10000000;

	/**
	 * Test method for {@link stream.data.LongSequenceID#getNextValue()}.
	 */
	@Test
	public void testNextValue() {

		LongSequenceID seq = new LongSequenceID();

		for (int i = 0; i < 3000; i++) {
			seq.nextValue();
		}

		LongSequenceID s2 = new LongSequenceID();

		Assert.assertTrue(s2.compareTo(seq) < 0);

		// fail("Not yet implemented");
	}

	@Test
	public void testSpeed() {

		LongSequenceID seq = new LongSequenceID();
		for (int round = 0; round < 5; round++) {
			Long start = System.currentTimeMillis();

			for (int i = 0; i < limit; i++) {
				// System.out.println(seq.getNextValue());
				seq.increment(); // .nextValue();
			}

			Long time = System.currentTimeMillis() - start;
			log.info("Generating {} IDs took {} ms", limit, time);

			DecimalFormat fmt = new DecimalFormat("0.000");
			log.info(
					"Rate is {} IDs/sec",
					fmt.format(limit.doubleValue()
							/ (time.doubleValue() / 1000.0d)));
		}
	}

	@Test
	public void testByteSpeed() {

		SequenceID seq = new SequenceID(16);

		for (int round = 0; round < 5; round++) {
			Long start = System.currentTimeMillis();

			for (int i = 0; i < limit; i++) {
				// System.out.println(seq.nextValue());
				seq.increment(); // nextValue();
			}

			Long time = System.currentTimeMillis() - start;
			log.info("Generating ByteSequence {} IDs took {} ms", limit, time);

			DecimalFormat fmt = new DecimalFormat("0.000");
			log.info(
					"Rate is {} IDs/sec",
					fmt.format(limit.doubleValue()
							/ (time.doubleValue() / 1000.0d)));
		}
	}

	@Test
	public void testUUID() {
		SequenceID seq = new SequenceID(16);

		for (int i = 0; i < 10240; i++) {
			SequenceID id = seq.increment();
			log.trace(id.uuid().toString() + "   (toString:   " + id.toString()
					+ " )");
		}
	}
}
