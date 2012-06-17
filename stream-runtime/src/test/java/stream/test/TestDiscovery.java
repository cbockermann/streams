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

		ServerSocket sock = new ServerSocket(0);
		registryPort = sock.getLocalPort();
		sock.close();

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
}
