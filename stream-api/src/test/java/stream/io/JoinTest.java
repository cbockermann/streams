package stream.io;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.junit.Test;

import cern.jet.random.engine.MersenneTwister64;

import stream.Data;

public class JoinTest {
	@Test
	public void test() throws Exception {
		// 100*10000=4.685,5.119
		int q = 5;
		int n =100;

		ExecutorService pool1 = Executors.newCachedThreadPool();
		ExecutorService pool2 = Executors.newCachedThreadPool();

		String[] streams = new String[q];

		for (int i = 0; i < q; i++) {
			streams[i] = String.valueOf(i);
		}

		Join queue = new Join();
		queue.setIndex("index");
		queue.setStreams(streams);
		queue.setSync("id");
		queue.init();

		MersenneTwister64 random = new MersenneTwister64();
		for (int i = 0; i < q; i++) {
			pool1.execute(new IndexProducer(queue, String.valueOf(i), n, random
					.nextInt()));
		}
		BlockingQueue<Boolean> resultQueue = new ArrayBlockingQueue<>(q);

		for (int i = 0; i < q; i++) {
			pool2.execute(new IndexConsumer(queue, n/2, resultQueue));
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

	}
}
