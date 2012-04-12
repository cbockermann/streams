/**
 * 
 */
package stream.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.stats.Statistics;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class MemoryLogger extends Thread {

	static Logger log = LoggerFactory.getLogger(MemoryLogger.class);

	public MemoryLogger() {
	}

	public void run() {
		while (true) {
			Long mem0 = Runtime.getRuntime().totalMemory()
					- Runtime.getRuntime().freeMemory();
			log.info("Current memory usage is {} bytes", mem0);

			try {
				Statistics stats = new Statistics();
				stats.put("memory.usage", mem0.doubleValue());
				log.info("Memory usage: {}", stats);
			} catch (Exception e) {
				if (log.isTraceEnabled())
					e.printStackTrace();
			}

			try {
				Thread.sleep(5 * 1000L);
			} catch (Exception e) {
			}
		}
	}
}