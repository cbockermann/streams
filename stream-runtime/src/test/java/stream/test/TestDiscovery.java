package stream.test;

import java.net.ServerSocket;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.rpc.Announcer;
import stream.runtime.rpc.ContainerAnnouncement;
import stream.runtime.rpc.Discovery;

public class TestDiscovery {

	static Logger log = LoggerFactory.getLogger(TestDiscovery.class);

	int registryPort = 9105;
	Announcer announcer;
	ContainerAnnouncement container;

	@Before
	public void setup() throws Exception {

		final TCPDummyServer dummyServer = new TCPDummyServer(0);
		registryPort = dummyServer.getPort();
		dummyServer.start();

		container = new ContainerAnnouncement("TestContainer", "rmi",
				"localhost", registryPort);
		announcer = new Announcer(18901, container);
		announcer.start();

	}

	@Test
	public void test() throws Exception {

		log.info("Sleeping for 2 seconds... ");
		Thread.sleep(2000);

		log.info("Looking for container... ");
		Discovery discovery = new Discovery(18901);
		ContainerAnnouncement ref = discovery.discover();
		Assert.assertNotNull(ref);
	}

	public static class TCPDummyServer extends Thread {

		ServerSocket sock;
		int registryPort;

		public TCPDummyServer(int port) throws Exception {
			sock = new ServerSocket(port);
			registryPort = sock.getLocalPort();
			setDaemon(true);
		}

		public int getPort() {
			return registryPort;
		}

		public void run() {
			try {
				while (sock.isBound()) {
					final java.net.Socket client = sock.accept();
					log.info("Client connection from {}", client);
					Thread t = new Thread() {
						public void run() {
							while (client.isConnected()) {
								try {
									Thread.sleep(100);
								} catch (Exception e) {
								}
							}

							log.info("Client disconnected (client: {})", client);
						}
					};
					t.setDaemon(true);
					t.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
