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
package stream.generator;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import stream.Data;
import stream.io.CsvWriter;

public class MultiGaussStreamTest {

	Map<String, GaussianStream> streams = new LinkedHashMap<String, GaussianStream>();

	private GaussianStream create(double mean, double dev, double... params)
			throws Exception {
		GaussianStream g = new GaussianStream();

		g.setGenerator("x1", new Gaussian(mean, dev));
		int cnt = 2;
		for (int i = 0; i + 1 < params.length; i += 2) {
			g.setGenerator("x" + cnt, new Gaussian(params[i], params[i + 1]));
			cnt++;
		}

		return g;
	}

	@Test
	public void test() throws Exception {

		CsvWriter writer = new CsvWriter();
		writer.setUrl("file:/tmp/cluster-stream.csv.gz");
		writer.init(null);

		streams.put("cluster1", create(2.0, 1.0, 2.0, 0.5, -2.0, 0.25));
		streams.put("cluster2", create(3.0, 1.0, 3.0, 1.0, 5.0, 1.0));
		streams.put("cluster3", create(5.0, 2.0, 8.0, 0.1, 3.0, 4.0));
		streams.put("cluster4", create(0.0, 0.05, -2.0, 0.85, -2.0, 3.4));
		streams.put("cluster5", create(13.2, 0.0, 6.0, 1.0, -4.0, 2.5));

		int i = 100;
		while (i-- > 0) {

			Data item = null;

			Iterator<String> it = streams.keySet().iterator();
			while (it.hasNext()) {
				String source = it.next();

				item = streams.get(source).read();
				item.put("@label", source);
				// System.out.println( item );
				writer.process(item);
			}

		}

		writer.finish();
	}

	@Test
	public void outlierTestData() throws Exception {

		CsvWriter writer = new CsvWriter();
		writer.setUrl("file:/tmp/outlier-stream.csv.gz");
		writer.init(null);

		double fraction = 0.01;

		streams.put("normal", create(2.0, 1.0, 2.0, 0.5, -2.0, 0.25));
		streams.put("outlier", create(3.0, 1.0, 3.0, 1.0, 5.0, 1.0));

		Random rnd = new Random();
		int outlier = 0;

		int count = 0;
		int i = 100;
		while (i-- > 0) {

			Data item = null;

			String source = "normal";
			if (rnd.nextDouble() < fraction) {
				source = "outlier";
				outlier++;
			}

			item = streams.get(source).read();
			item.put("@label", source);
			// System.out.println( item );
			writer.process(item);
			count++;
		}

		writer.finish();
		System.out.println("Added " + outlier + " outliers in " + count
				+ " items.");
	}
}
