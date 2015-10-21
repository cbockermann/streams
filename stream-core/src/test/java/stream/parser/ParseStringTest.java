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
import stream.runtime.ContainerContext;
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
			parser.setFormat(
					"%(REMOTE_ADDR) %(HOST) %(REMOTE_USER) [%(DAY)/%(MONTH)/%(YEAR):%(TIME)] \"%(METHOD) %(URI) %(PROTOCOL)\" %(STATUS) %(SIZE) \"%(d)\" \"%(USER_AGENT)\"");

			ContainerContext cc = new ContainerContext("application:0");
			ProcessContext pc = new ProcessContextImpl("process:0", cc);
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
