/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
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