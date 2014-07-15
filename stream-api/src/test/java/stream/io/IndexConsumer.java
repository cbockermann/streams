package stream.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;

public class IndexConsumer implements Runnable {
	final Logger log = LoggerFactory.getLogger(BlockingQueue.class);

	private Join join;
	private int reads;
	private java.util.concurrent.BlockingQueue<Boolean> resultQueue;

	public IndexConsumer(Join join, int reads,
			java.util.concurrent.BlockingQueue<Boolean> resultQueue) {
		this.join = join;
		this.reads = reads;
		this.resultQueue = resultQueue;
	}

	@Override
	public void run() {
		int c = 0;
		long index = 0;
		while (c < reads) {
			Data d;
			try {
				d = join.read();
				long tindex = (Long) d.get("index");
				if (tindex < index)
					resultQueue.put(false);
				index = tindex;
				c++;
				// System.out.println(d + ":"+c);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		try {
			resultQueue.put(true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
