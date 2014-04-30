package stream.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import cern.jet.random.engine.MersenneTwister64;

public class IndexProducer implements Runnable {
	private final Logger log = LoggerFactory.getLogger(BlockingQueue.class);

	private Join queue;
	private String id;
	private MersenneTwister64 random;
	private long i;
	private int n;

	public IndexProducer(Join queue, String id, int n, int seed) {
		this.queue = queue;
		this.id = id;
		this.i = 0;
		this.n = n;
		random = new MersenneTwister64(seed);
	}

	@Override
	public void run() {
		int c = 0;
		boolean run = true;
		while (run) {
			try {
				i++;
				if (random.nextDouble() < 0.01d) {
					c++;
					Data d = DataFactory.create();
					d.put("id", id);
					d.put("index", i);
					queue.write(d);
					if (c > n) {
						run = false;
						log.info("{}:Finished to write {} elements. ", id, n);
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
}
