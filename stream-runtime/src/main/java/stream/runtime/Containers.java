package stream.runtime;

import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hendrik Blom
 * 
 */
public class Containers extends LinkedHashMap<String, Container> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Logger log = LoggerFactory.getLogger(Containers.class);

	ExecutorService pool;

	public Containers() {
		super();
		pool = Executors.newCachedThreadPool();
	}

	public Containers(int n) {
		super();
		pool = Executors.newFixedThreadPool(n);
	}

	public boolean start(String name) {
		Container c = get(name);
		if (c == null)
			return false;
		pool.execute(c);
		return true;
	}

	public boolean stop(String name) {
		Container c = get(name);
		if (c == null)
			return false;
		// TODO streams stuff
		return true;
	}

	public boolean stop() {
		pool.shutdown();

		int c = 0;
		try {
			while (c < 10 && !pool.awaitTermination(10, TimeUnit.SECONDS)) {
				c++;
				log.info("Awaiting completion of threads.");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (c == 10) {
			log.info("Can't stop pool. Killed...");
			pool.shutdownNow();
			return false;
		}
		return true;
	}

}
