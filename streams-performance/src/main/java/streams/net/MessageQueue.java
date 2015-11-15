/**
 * 
 */
package streams.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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

        String host = System.getProperty("rlog.host");

        log.info("Initializing global MessageQueue rlog {}", host);

		if (host == null) {
			log.info("'rlog.host' not set, disabling rlog-sender");
			sender = null;
		} else {
            //TODO use default port number
			sender = new Sender(host, 6001, messages);
			log.info("rlog.host={} using port={}", host, 6001);
			sender.setDaemon(true);
			sender.start();
		}

		Runtime.getRuntime().addShutdownHook(new Shutdown());
	}

    /**
     * Add a message to a queue of messages to be sent to performance receiver.
     *
     * @param m Message with performance statistics
     */
    public static void add(Message m) {
		if (sender != null) {
			messages.offer(m);
		}
	}

	public static class Sender extends Thread {

		final Codec<Data> mc = new JavaCodec<>();
        private final int port;

        DataOutputStream out;
		final String host;
		BufferedReader in;
		final LinkedBlockingQueue<Message> messages;
		boolean running = false;

        /**
         * Create sender thread to be able to connect to performance receiver. This constructor type
         * uses the system property 'rlog.host' if it is set. Otherwise use another constructor with
         * a given host address as string variable.
         */
        public Sender() {
            this(System.getProperty("rlog.host"));
		}

        /**
         * Create sender thread to be able to connect to performance receiver. This constructor uses
         * host address given as parameter.
         *
         * @param host String value of host address
         */
        public Sender(String host) {
            //TODO define default port somewhere as constant
			this(host, 6001, new LinkedBlockingQueue<Message>());
		}

        /**
         * Create sender thread to be able to connect to performance receiver. This constructor uses
         * host address given as parameter.
         *
         * @param host String value of host address
         */
        public Sender(String host, int port) {
            this(host, port, new LinkedBlockingQueue<Message>());
        }

        /**
         * Create sender thread to be able to connect to performance receiver. This constructor uses
         * host address given as parameter.
         *
         * @param host String value of host address
         * @param messages linked blocking queue of messages to be sent
         */
		public Sender(String host, int port, LinkedBlockingQueue<Message> messages) {
			this.host = host;
            this.port = port;
			this.messages = messages;
			this.setDaemon(true);
		}

        /**
         * Connect to the specified host and port.
         *
         * @return Socket connection
         */
        protected Socket connect() throws Exception {
            Socket socket = SecureConnect.connect(host, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return socket;
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
