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
package stream.test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.RandomStream;
import stream.io.Stream;

/**
 * @author chris
 * 
 */
public class WrapperSpeedTest {

	static Logger log = LoggerFactory.getLogger(WrapperSpeedTest.class);

	@Test
	public void test() {

		try {
			Stream source = new RandomStream();
			for (int i = 0; i < 1000; i++) {
				source.read();
			}

			Map<String, Double> times = new LinkedHashMap<String, Double>();

			int length = 100000;
			int depth = 10;
			int rounds = 10;

			for (int r = 0; r < rounds; r++) {
				for (int i = 0; i < depth; i++) {
					Stream wrapped = wrap(depth, new RandomStream());
					Long wrap = readTest(length, wrapped);
					String id = "" + i;
					if (r < 5)
						continue;

					Double val = times.get(id);
					if (val == null) {
						val = 0.0;
					}
					times.put(id, val + wrap);
					if (r % 10 == 0)
						log.info("{}-times wrapped stream required {} ns", i,
								wrap);
				}
			}

			for (String key : times.keySet()) {
				Double val = times.get(key);
				log.info("wrapping {} required {} ns", key, val / rounds);
			}

		} catch (Exception e) {
		}
	}

	protected Stream wrap(int i, Stream stream) {
		if (i > 0) {
			return wrap(i - 1, new DataStreamWrapper(stream));
		}
		return stream;
	}

	protected Long readTest(int count, Stream stream) {
		try {
			Long start = getCpuTime();
			while (count > 0) {
				stream.read();
				count--;
			}
			Long end = getCpuTime();
			return end - start;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1L;
	}

	/** Get CPU time in nanoseconds. */
	public long getCpuTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? bean
				.getCurrentThreadCpuTime() : 0L;
	}

	public class DataStreamWrapper implements Stream {
		Stream stream;
		Long items = 0L;
		Long limit = -1L;

		public DataStreamWrapper(Stream stream) {
			this.stream = stream;
		}

		/**
		 * @return
		 * @see stream.io.Stream#getId()
		 */
		public String getId() {
			return stream.getId();
		}

		/**
		 * @param id
		 * @see stream.io.Stream#setId(java.lang.String)
		 */
		public void setId(String id) {
			stream.setId(id);
		}

		/**
		 * @throws Exception
		 * @see stream.io.Stream#init()
		 */
		public void init() throws Exception {
			stream.init();
		}

		/**
		 * @return
		 * @throws Exception
		 * @see stream.io.Stream#read()
		 */
		public Data read() throws Exception {
			Data item = stream.read();
			if (item != null)
				items++;
			return item;
		}

		/**
		 * @throws Exception
		 * @see stream.io.Stream#close()
		 */
		public void close() throws Exception {
			stream.close();
		}

		/**
		 * @see stream.io.Stream#getLimit()
		 */
		@Override
		public Long getLimit() {
			return limit;
		}

		/**
		 * @see stream.io.Stream#setLimit(java.lang.Long)
		 */
		@Override
		public void setLimit(Long limit) {
			this.limit = limit;
		}
	}
}