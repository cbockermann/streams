/**
 * 
 */
package stream.distribution;

import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.generator.RandomStream;
import stream.io.DataStream;

/**
 * @author chris
 * 
 */
public class DistributionTest {

	static Logger log = LoggerFactory.getLogger(DistributionTest.class);

	@Test
	public void test() throws Exception {

		DataStream stream = new RandomStream(); // QuantilesTest.openStream();
		((RandomStream) stream).setKeys(new String[] { "x" });
		int limit = 1000;

		NumericalDistribution d = new NumericalDistribution();
		Data item = stream.readNext();
		int i = 1;
		while (item != null && i < limit) {
			Double value = (Double) item.get("x");
			d.update(value);
			item = stream.readNext();
			i++;
		}

		log.info("mean = {}", d.getMean());
		log.info("variance = {}", Math.sqrt(d.getVariance()));

		Map<Double, Double> hist = d.getHistogram();
		for (Double x : hist.keySet()) {
			// log.info(" {} = {}", x, hist.get(x));
			System.out.println(x + " " + hist.get(x));
		}
	}
}