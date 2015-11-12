/**
 * 
 */
package streams.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.io.Codec;
import stream.io.JavaCodec;
import streams.io.BobCodec;
import streams.logging.Message;

/**
 * @author chris
 *
 */
public class MessageQueue {

	static Logger log = LoggerFactory.getLogger(MessageQueue.class);

	final static LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<Message>();

	static List<Sender> senders = new ArrayList<Sender>();
	private static Sender sender;

	static {
		log.info("Initializing global MessageQueue");

		String host = System.getProperty("rlog.host");
		if (host == null) {
			log.error("'rlog.host' not set, disabling rlog-sender");
			sender = null;
		} else {
			sender = new Sender(host, messages);
			sender.setDaemon(true);
			sender.start();
		}

		Runtime.getRuntime().addShutdownHook(new Shutdown());
	}

	public static void add(Message m) {
		if (sender != null) {
			messages.offer(m);
		}
	}

	public static class Sender extends Thread {

		final Codec<Data> mc = new JavaCodec<Data>();

		DataOutputStream out;
		final String host;
		BufferedReader in;
		final LinkedBlockingQueue<Message> messages;
		boolean running = false;

		public Sender() {
			setDaemon(true);
			this.host = System.getProperty("rlog.host");
			this.messages = new LinkedBlockingQueue<Message>();
		}

		public Sender(String host) {
			this(host, new LinkedBlockingQueue<Message>());
		}

		public Sender(String host, LinkedBlockingQueue<Message> msgs) {
			this.host = host;
			this.messages = msgs;
			this.setDaemon(true);
		}

		public void run() {
			running = true;
			if (!senders.contains(this)) {
				senders.add(this);
			}

			while (running || !messages.isEmpty()) {
				try {
					Message m = messages.poll(1000, TimeUnit.MILLISECONDS);
					if (m != null) {
                        send(m);
                    }
					// System.out.println("Sending message " + m);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		protected Socket connect() throws Exception {
			Socket socket = SecureConnect.connect(host, PerformanceReceiver.port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return socket;
		}

		public void send(Message m) {
			try {
				if (out == null || in == null) {
					connect();
				}

				byte[] bytes = mc.encode(DataFactory.create(m));
				log.debug("Encoded message to " + bytes.length + " bytes");
				int written = BobCodec.writeBlock(bytes, out);
				log.debug(written + " bytes written to socket...");
				out.flush();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public int messagesPending() {
			return messages.size();
		}

		public void add(Message m) {
			this.messages.add(m);
		}
	}

	/**
	 * Shutdown thread that is used as ShutdownHook. In this case we wait for senders to finish
	 * their message queues.
	 */
	public static class Shutdown extends Thread {
		public void run() {
			log.info("Shutting down message queue...");
			for (Sender sender : senders) {
				sender.running = false;

				while (sender != null && !messages.isEmpty()) {
					try {
						log.info("Waiting for sender to finish ({} messages pending)...", messages.size());
						sender.join(1000);
					} catch (Exception e) {
						log.error("Waiting for sender was interrupted: " + e);
					}
				}
			}
		}
	}
}
