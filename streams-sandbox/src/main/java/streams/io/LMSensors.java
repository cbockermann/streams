/**
 * 
 */
package streams.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractStream;
import stream.util.Time;

/**
 * @author chris
 *
 */
public class LMSensors extends AbstractStream {

	static Logger log = LoggerFactory.getLogger(LMSensors.class);

	final LinkedBlockingQueue<Data> items = new LinkedBlockingQueue<Data>();
	Time interval = new Time(Time.SECOND);

	/**
	 * @see stream.io.AbstractStream#init()
	 */
	@Override
	public void init() throws Exception {
		super.init();
		log.info("Creating poll thread...");
		PollThread p = new PollThread(this);
		p.setDaemon(true);
		log.debug("starting poll thread");
		p.start();
	}

	/**
	 * @see stream.io.AbstractStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		Data item = items.take();
		return item;
	}

	/**
	 * @return the interval
	 */
	public Time getInterval() {
		return interval;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(Time interval) {
		this.interval = interval;
	}

	protected synchronized void add(Data item) {
		items.add(item);
	}

	public static class PollThread extends Thread {
		Logger log = LoggerFactory.getLogger(PollThread.class);
		final LMSensors stream;

		public PollThread(LMSensors stream) {
			this.stream = stream;
		}

		public void run() {

			while (true) {

				try {

					ProcessBuilder pb = new ProcessBuilder();
					pb.command("/usr/bin/sensors", "-u");
					log.info("Calling '{}'", pb.command());
					Process p = pb.start();

					BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line = reader.readLine();
					while (line != null) {
						log.info("output line is: '{}'", line);
						line = line.trim();
						if (line.startsWith("temp1_input:")) {
							String[] tok = line.split(": ");
							Double temp = Double.parseDouble(tok[1]);

							Data item = DataFactory.create();
							item.put("@timestamp", System.currentTimeMillis());
							item.put("temp", temp);
							log.info("queueing item " + item);
							stream.add(item);
						}
						line = reader.readLine();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				doSleep(stream.interval.asMillis());
			}
		}

		public void doSleep(long ms) {
			try {
				Thread.sleep(ms);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {

		LMSensors sensors = new LMSensors();
		sensors.setInterval(new Time(System.getProperty("interval", "1s")));
		sensors.init();

		Data item = sensors.read();
		while (item != null) {
			System.out.println("item: " + item);
			item = sensors.read();
		}
	}
}
