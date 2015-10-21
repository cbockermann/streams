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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minidev.json.JSONObject;
import stream.Data;
import stream.io.Codec;
import stream.io.JavaCodec;
import streams.io.BobCodec;
import streams.performance.PerformanceTree;
import streams.performance.ProcessorStatistics;

/**
 * @author chris
 *
 */
public class PerformanceReceiver {

	static Logger log = LoggerFactory.getLogger(PerformanceReceiver.class);

	final ServerSocket server;
	PerformanceTree perfTree;

	static Map<String, PerformanceTree> performanceTrees = new LinkedHashMap<String, PerformanceTree>();

	static LinkedBlockingQueue<Update> updates = new LinkedBlockingQueue<Update>();

	public PerformanceReceiver(int port) throws Exception {
		server = new ServerSocket(port);
		// perfTree = new PerformanceTree("", null);
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

			Updater updater = new Updater();
			updater.setDaemon(true);
			updater.start();

			try {

				while (true) {
					DataInputStream dis = new DataInputStream(socket.getInputStream());
					byte[] block = BobCodec.readBlock(dis);
					if (block == null) {
						log.info("Received null block - exiting receiver...");
						return;
					}
					Data message = codec.decode(block);

					Serializable id = message.get("performance.id");
					if (id != null) {
						log.info("Performance path: '{}'", id);

						String appId = id.toString().replaceAll("/(.*)$", "");
						log.info("application ID: {}", appId);

						Serializable perfs = message.get("processors");
						if (perfs != null && perfs.getClass().isArray()
								&& perfs.getClass().getComponentType() == ProcessorStatistics.class) {

							ProcessorStatistics[] stats = (ProcessorStatistics[]) perfs;
							perfs = message.get("performance.stats");

							if (perfs != null && perfs instanceof ProcessorStatistics) {
								ProcessorStatistics performance = (ProcessorStatistics) perfs;
								report(performance, stats);
								// parent.merge(id.toString(), performance);
								updates.add(new Update(id.toString(), performance));
								for (int i = 0; i < stats.length; i++) {
									// parent.merge(id.toString() +
									// "/processor:" + i, stats[i]);
									updates.add(new Update(id.toString() + "/processor:" + i, stats[i]));
								}

								// parent.perfTree.print();
							}

						}
					}

					log.info("Received message: {}", JSONObject.toJSONString(message));
					// System.out.println(message);
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
						log.info("Creating new performance tree for application '{}'", app);
					}

					tree.update(path, update.stats);
					tree.print();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		PerformanceReceiver receiver = new PerformanceReceiver(6001);
		log.info("Starting performance-receiver on port {}", receiver.server.getLocalPort());
		receiver.run();
	}
}
