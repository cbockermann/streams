/**
 * 
 */
package streams.net;

import java.io.DataInputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.SSLServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.io.Codec;
import stream.io.JavaCodec;
import stream.util.MultiSet;
import streams.io.BobCodec;
import streams.performance.PerformanceTree;
import streams.performance.ProcessorStatistics;

/**
 * @author chris
 *
 */
public class PerformanceReceiver extends Thread {

	static Logger log = LoggerFactory.getLogger(PerformanceReceiver.class);

	final ServerSocket server;
	PerformanceTree perfTree;

	static Map<String, PerformanceTree> performanceTrees = new LinkedHashMap<String, PerformanceTree>();
	static MultiSet<String> updateCount = new MultiSet<String>();

	static LinkedBlockingQueue<Update> updates = new LinkedBlockingQueue<Update>();

	public PerformanceReceiver(int port) throws Exception {
		// server = new ServerSocket(port);
		SSLServerSocket server = SecureConnect.openServer(port);
		server.setWantClientAuth(true);
		this.server = server;
		this.setDaemon(true);
	}

	public static class Receiver extends Thread {
		final Socket socket;
		final Codec<Data> codec = new JavaCodec<Data>();
		final PerformanceReceiver parent;

		public Receiver(Socket socket, PerformanceReceiver parent) {
			this.socket = socket;
			this.parent = parent;
			this.setDaemon(true);
		}

		public void run() {

			try {

				while (true) {
					DataInputStream dis = new DataInputStream(socket.getInputStream());
					byte[] block = BobCodec.readBlock(dis);
					if (block == null) {
						log.debug("Received null block - exiting receiver...");
						return;
					}
					Data message = codec.decode(block);

					Serializable id = message.get("performance.id");
					if (id != null) {

						Serializable perfs = message.get("processors");
						if (perfs != null && perfs.getClass().isArray()
								&& perfs.getClass().getComponentType() == ProcessorStatistics.class) {

							ProcessorStatistics[] stats = (ProcessorStatistics[]) perfs;
							perfs = message.get("performance.stats");

							if (perfs != null && perfs instanceof ProcessorStatistics) {
								ProcessorStatistics performance = (ProcessorStatistics) perfs;
								updates.add(new Update(id.toString(), performance));
								for (int i = 0; i < stats.length; i++) {
									updates.add(new Update(id.toString() + "/processor:" + i, stats[i]));
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void report(ProcessorStatistics performance, ProcessorStatistics[] statistics) {

			log.info("+------------------------- Performance Report ------------------------------");
			log.info("|");
			log.info("| Performance recorded based on {} events processed in {} ms", performance.itemsProcessed(),
					performance.end() - performance.start());
			// log.info("| Average performance is {} ms/item => {} items/sec",
			// performance.f.format(msPerItem), f.format(items / sec));
			log.info("|");
			log.info("| The following {} processes have been measured:", statistics.length);
			log.info("|");
			for (int i = 0; i < statistics.length; i++) {
				log.info("|     [{}]  {}", i, statistics[i]);
			}
			log.info("|");
			log.info("| streams.performance.Performance statistics:");
			log.info("|    {}", performance);
			log.info("+---------------------------------------------------------------------------");
		}
	}

	public void run() {
		try {

			Updater updater = new Updater();
			updater.setDaemon(true);
			updater.start();

			while (true) {

				Socket client = server.accept();
				log.info("client connection from {}", client);
				Receiver receiver = new Receiver(client, this);
				receiver.start();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class Update {
		final String path;
		final ProcessorStatistics stats;

		public Update(String path, ProcessorStatistics up) {
			this.path = path;
			this.stats = up;
		}
	}

	public static class Updater extends Thread {

		public void run() {
			while (true) {
				try {
					Update update = updates.take();

					String[] path = update.path.split("/");
					String app = path[0];

					PerformanceTree tree = performanceTrees.get(app);
					if (tree == null) {
						tree = new PerformanceTree("", null);
						performanceTrees.put(app, tree);
						log.debug("Creating new performance tree for application '{}'", app);
					}

					tree.update(path, update.stats);

					updateCount.add(app);
					int cnt = updateCount.count(app);
					if (cnt % 10 == 0) {
						tree.print();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static class Dump extends Thread {

		public void run() {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
			for (String id : performanceTrees.keySet()) {
				PerformanceTree tree = performanceTrees.get(id);
				System.out.println("------------------------ Application " + id + " ------------------------");
				tree.print();
				System.out.println("------------------------ -------------- ------------------------");
			}
		}
	}

	/**
	 * Start performance receiver on a server.
	 */
	public static void main(String[] args) throws Exception {
        int port = 6001;
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            }
            catch (Exception e) {
                log.error("You can only define port number as a parameter. "
                        + " Using default: 6001." + args[0]);
            }
        }
		Runtime.getRuntime().addShutdownHook(new Dump());

		PerformanceReceiver receiver = new PerformanceReceiver(port);
		log.info("Starting performance-receiver on port {}", receiver.server.getLocalPort());
		receiver.run();
	}
}
