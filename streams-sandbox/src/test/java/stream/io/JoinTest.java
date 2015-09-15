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
package stream.io;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cern.jet.random.engine.MersenneTwister64;

/**
 * @author hendrik
 * 
 */
public class JoinTest {
	private int q;
	private int n;
	private int c;

	@Before
	public void init() {
		q = 4;
		n = 100000;
		c = 1000;
	}

	@Test
	public void test() throws Exception {
		// 100*10000=4.685,5.119

		ExecutorService pool1 = Executors.newCachedThreadPool();
		ExecutorService pool2 = Executors.newCachedThreadPool();

		String[] streams = new String[q];

		for (int i = 0; i < q; i++) {
			streams[i] = String.valueOf(i);
		}

		Join join = new Join();
		join.setCapacity(c);
		join.setIndex("index");
		join.setStreams(streams);
		join.setSync("id");
		join.init();

		MersenneTwister64 random = new MersenneTwister64();

		long start = System.currentTimeMillis();
		for (int i = 0; i < q; i++) {
			pool1.execute(new IndexProducer(join, String.valueOf(i), n, random
					.nextInt()));
		}
		BlockingQueue<Boolean> resultQueue = new ArrayBlockingQueue<>(q);

		for (int i = 0; i < q; i++) {
			pool2.execute(new IndexConsumer(join, n / 2, resultQueue));
		}

		boolean run = true;
		int count = 0;
		while (run) {
			Boolean result = resultQueue.take();
			if (result.equals(false))
				Assert.fail();
			count++;
			if (count == q)
				run = false;
		}
		long stop = System.currentTimeMillis();
		System.out.println("Join:runtime:" + (stop - start));
	}

	@Test
	public void snappyTest() throws Exception {
		ExecutorService pool1 = Executors.newCachedThreadPool();
		ExecutorService pool2 = Executors.newCachedThreadPool();

		String[] streams = new String[q];

		for (int i = 0; i < q; i++) {
			streams[i] = String.valueOf(i);
		}

		SnappyJoin join = new SnappyJoin();
		join.setCapacity(c);
		join.setIndex("index");
		join.setStreams(streams);
		join.setSync("id");
		join.init();

		MersenneTwister64 random = new MersenneTwister64();

		long start = System.currentTimeMillis();
		for (int i = 0; i < q; i++) {
			pool1.execute(new IndexProducer(join, String.valueOf(i), n, random
					.nextInt()));
		}
		BlockingQueue<Boolean> resultQueue = new ArrayBlockingQueue<>(q);

		for (int i = 0; i < q; i++) {
			pool2.execute(new IndexConsumer(join, n / 2, resultQueue));
		}

		boolean run = true;
		int count = 0;
		while (run) {
			Boolean result = resultQueue.take();
			if (result.equals(false))
				Assert.fail();
			count++;
			if (count == q)
				run = false;
		}
		long stop = System.currentTimeMillis();
		System.out.println("SnappyJoin:runtime:" + (stop - start));
	}
}
