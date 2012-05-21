/**
 * 
 */
package stream.quantiles;

import java.net.URL;
import java.text.DecimalFormat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ProcessContext;
import stream.data.Data;
import stream.generator.RandomStream;
import stream.io.CsvStream;
import stream.io.DataStream;
import stream.parser.ParseDouble;
import stream.runtime.ProcessContextImpl;

/**
 * @author chris
 * 
 */
public class QuantilesTest {

	static Logger log = LoggerFactory.getLogger(QuantilesTest.class);

	public static DataStream openStream() throws Exception {
		URL url = QuantilesTest.class.getResource("/single-data.csv");
		CsvStream stream = new CsvStream(url);

		ParseDouble p = new ParseDouble();
		p.setKeys(new String[] { "x" });
		stream.getPreprocessors().add(p);

		return stream;
	}

	@Test
	public void test() throws Exception {

		// RandomStream stream = new RandomStream();
		// stream.setKeys(new String[] { "x" });

		DataStream stream = openStream();

		ExactQuantiles eq = new ExactQuantiles();
		eq.setKey("x");

		Data item = stream.readNext();
		while (item != null) {
			eq.process(item);
			item = stream.readNext();
		}

		Double[] phis = new Double[] { 0.25, 0.3, 0.5, 0.75 };

		for (Double phi : phis) {
			log.info("{}-quantile = {}", phi, eq.getQuantile(phi));
		}
	}

	@Test
	public void testSumQuantiles() throws Exception {

		SumQuantiles eq = new SumQuantiles(10, 4);
		eq.setKey("x");

		RandomStream stream = new RandomStream();
		stream.setKeys(new String[] { "x" });
		stream.setLimit(1000L);

		Data item = stream.readNext();
		while (item != null) {
			eq.process(item);
			item = stream.readNext();
		}

		Double[] phis = new Double[] { 0.25, 0.3, 0.5, 0.75 };

		for (Double phi : phis) {
			log.info("{}-quantile = {}", phi, eq.getQuantile(phi));
		}
	}

	@Test
	public void testGKQuantiles() throws Exception {

		// DataStream stream = openStream();

		RandomStream stream = new RandomStream();
		stream.setKeys(new String[] { "x" });
		stream.setLimit(100000L);

		ProcessContext ctx = new ProcessContextImpl();

		GKQuantiles gk = new GKQuantiles(0.01);
		gk.setKey("x");
		gk.init(ctx);

		ExactQuantiles eq = new ExactQuantiles();
		eq.setKey("x");
		eq.init(ctx);

		Data item = stream.readNext();
		while (item != null) {
			gk.process(item);
			eq.process(item);
			item = stream.readNext();
		}

		DecimalFormat fmt = new DecimalFormat("0.0");
		for (Double phi = 0.0d; phi < 1.0; phi += 0.1) {
			// for (Double phi : phis) {
			log.info("GKQuantiles:     {}-quantile = {}", fmt.format(phi),
					gk.getQuantile(phi));
			log.info("ExactQuantiles:  {}-quantile = {}", fmt.format(phi),
					eq.getQuantile(phi));
			log.info("--------------------------------------------------");
		}
	}
}
