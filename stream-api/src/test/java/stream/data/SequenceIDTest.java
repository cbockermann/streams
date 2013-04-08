/**
 * 
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

		for (int i = 0; i < 384; i++) {
			SequenceID id = seq.increment();
			System.out.println(id.uuid().toString() + "   (toString:   "
					+ id.toString() + " )");
		}
	}
}
