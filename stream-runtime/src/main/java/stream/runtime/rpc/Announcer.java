package stream.runtime.rpc;

import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Announcer extends Thread {

	static Logger log = LoggerFactory.getLogger(Announcer.class);
	boolean running = true;

	final Map<String, Long> alive = new LinkedHashMap<String, Long>();
	final MulticastSocket broadcast;

	Long interval = 1000L;
	final ContainerAnnouncement announcement;

	public Announcer(int port, ContainerAnnouncement announcement)
			throws Exception {
		this.setDaemon(true);
		broadcast = new MulticastSocket(port);
		broadcast.setBroadcast(true);
		this.announcement = announcement;
	}

	public void run() {

		byte[] data = announcement.toByteArray();
		int len = data.length;

		while (running) {

			try {
				log.debug("Waiting for container queries to {}:{}",
						broadcast.getLocalAddress(), broadcast.getLocalPort());

				DatagramPacket query = new DatagramPacket(
						ContainerAnnouncement.CONTAINER_QUERY,
						ContainerAnnouncement.CONTAINER_QUERY.length);
				broadcast.receive(query);

				if (running) {
					DatagramPacket p = new DatagramPacket(data, len);
					log.debug("Sending response to {}:{}", query.getAddress(),
							query.getPort());
					p.setAddress(query.getAddress());
					p.setPort(query.getPort());
					broadcast.send(p);

					log.debug("Sent '" + new String(p.getData())
							+ "' to broadcast...");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				log.debug("Sleeping for " + interval + "ms");
				Thread.sleep(interval);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void finish() {
		running = false;
	}
}