/**
 * 
 */
package streams.performance;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.SourceURL;
import stream.util.MD5;

/**
 * @author chris
 *
 */
public class RlogSender extends Thread {

	static Logger log = LoggerFactory.getLogger(RlogSender.class);

	final LinkedBlockingQueue<Item> messages = new LinkedBlockingQueue<Item>();

	private final static RlogSender sender = new RlogSender();

	boolean running = false;
	String baseUrl;
	String auth = "";

	private RlogSender() {
		baseUrl = System.getProperty("rlog.url", Rlog.DEFAULT_URL);
		if (System.getProperty("rlog.token") == null) {
			try {
				SourceURL su = new SourceURL(baseUrl);
				String user = System.getProperty("rlog.user", su.getUsername());
				String password = System.getProperty("rlog.password", su.getPassword());
				if (user != null && password != null) {
					auth = "?auth=" + MD5.md5(user + ":" + password);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			auth = "?auth=" + System.getProperty("rlog.token");
		}
		log.info("Using auth '{}'", auth);
		setDaemon(true);
	}

	public void run() {
		running = true;
		while (running || !messages.isEmpty()) {

			try {
				log.debug("Waiting for next message to send...");
				Item msg = messages.poll(1000, TimeUnit.MILLISECONDS);

				if (msg != null) {

					String encoded = URLEncoder.encode(msg.message, "UTF-8");
					URL url = new URL(baseUrl + "/" + msg.trace + auth + "&message=" + encoded);
					log.debug("Opening {}", url);
					InputStream in = url.openStream();
					while (in.read() >= 0) {
					}
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		running = false;
	}

	public static void send(long timestamp, String trace, String msg) {
		sender.messages.add(new Item(timestamp, trace, msg));

		if (!sender.running) {
			sender.start();
		}
	}

	public static class Item {
		final long timestamp;
		final String trace;
		final String message;

		public Item(long timestamp, String t, String m) {
			this.timestamp = timestamp;
			this.trace = t;
			this.message = m;
		}
	}
}
