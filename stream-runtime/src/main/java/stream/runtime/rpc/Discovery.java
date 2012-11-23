package stream.runtime.rpc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Discovery extends Thread {

	static Logger log = LoggerFactory.getLogger(Discovery.class);
	boolean running = true;

	final Map<String, Long> alive = new LinkedHashMap<String, Long>();
	final Map<String, ContainerAnnouncement> containers = new LinkedHashMap<String, ContainerAnnouncement>();
	final DatagramSocket discovery;

	Long interval = 1000L;
	int count = 1;
	int announcementPort = 9200;

	public Discovery() throws Exception {
		this(9200);
	}

	public Discovery(int announcementPort) throws Exception {
		this.announcementPort = announcementPort;
		discovery = new DatagramSocket(0);
		discovery.setBroadcast(true);
	}

	public ContainerAnnouncement discover() throws Exception {

		this.containers.clear();
		this.alive.clear();

		int timeout = 100;
		try {
			timeout = Integer.parseInt(System.getProperty(
					"stream.container.timeout", "250"));
		} catch (Exception e) {
			timeout = 100;
		}
		log.info("Using connection timeout of {} ms", timeout);

		DatagramPacket query = new DatagramPacket(
				ContainerAnnouncement.CONTAINER_QUERY,
				ContainerAnnouncement.CONTAINER_QUERY.length);
		query.setAddress(InetAddress.getByName("255.255.255.255"));
		query.setPort(announcementPort);

		log.debug("Sending broadcast-query to {}:{}", query.getAddress(),
				query.getPort());
		discovery.send(query);
		log.debug("query sent...");
		int i = 0;
		List<ContainerAnnouncement> discovered = new ArrayList<ContainerAnnouncement>();

		while (i++ < 5) {

			try {
				DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
				discovery.setSoTimeout(100);
				log.debug("receiving...");
				discovery.receive(p);

				if (p.getData() != null) {
					ContainerAnnouncement announcement = new ContainerAnnouncement(
							p.getData());
					log.debug(
							"Discovered container {} at "
									+ announcement.getProtocol() + "://"
									+ announcement.getHost() + ":"
									+ announcement.getPort(),
							announcement.getName());

					try {
						//
						// check if there exists a route to the remote
						// container...
						//
						Socket sock = new Socket();
						log.debug("Creating socket-address...");
						SocketAddress addr = new InetSocketAddress(
								announcement.getHost(), announcement.getPort());

						log.debug("Checking connection to {}", addr);
						sock.connect(addr, timeout);

						if (sock.isConnected()) {
							log.debug("Test-Connection succeeded.");
							sock.close();
							log.debug("Test-connection closed.");
							discovered.add(announcement);
							synchronized (containers) {
								containers.put(announcement.getName(),
										announcement);
							}

							synchronized (alive) {
								alive.put(announcement.toString(),
										System.currentTimeMillis());
							}

						}
					} catch (SocketTimeoutException e) {
						log.error("Cannot connect to container {}: {}",
								announcement, e.getMessage());
						if (log.isTraceEnabled())
							e.printStackTrace();
					} catch (Exception ce) {
						log.error(
								"Found container at {}, but failed to connect: {}",
								announcement, ce.getMessage());
						if (log.isDebugEnabled()) {
							ce.printStackTrace();
						}
					}

				} else {
					log.debug("received data-gram without data... {}", p);
				}
			} catch (SocketTimeoutException ste) {
				// if (log.isDebugEnabled())
				// ste.printStackTrace();
			} catch (Exception e) {
				log.error("Error: {}", e.getMessage());
				// if (log.isDebugEnabled())
				// e.printStackTrace();
			}
		}

		if (discovered.isEmpty()) {
			log.debug("No containers discovered!");
			return null;
		}

		log.info("Discovered containers: {}", discovered);
		return discovered.get(0);
	}

	public void run() {

		while (running) {

			try {
				DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
				discovery.receive(p);
			} catch (Exception e) {

				e.printStackTrace();
			}

			try {
				Thread.sleep(interval);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void printContainers() {

		synchronized (alive) {
			for (String key : alive.keySet()) {
				log.debug("  {}   (last checked: {})", key,
						new Date(alive.get(key)));
			}
		}
	}

	public Map<String, Long> getContainers() {
		synchronized (alive) {
			return new LinkedHashMap<String, Long>(alive);
		}
	}

	public Map<String, String> getContainerURLs() {
		Map<String, String> urls = new LinkedHashMap<String, String>();
		synchronized (containers) {
			for (String key : containers.keySet()) {
				ContainerAnnouncement rem = containers.get(key);
				String url = rem.getProtocol() + "://" + rem.getHost() + ":"
						+ rem.getPort();
				urls.put(key, url);
			}
		}
		return urls;
	}

	public Map<String, ContainerAnnouncement> getAnnouncements() {
		synchronized (containers) {
			return new LinkedHashMap<String, ContainerAnnouncement>(containers);
		}
	}

}