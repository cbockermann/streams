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
package stream.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractStream;
import stream.io.SourceURL;

/**
 * @author chris
 * 
 */
public class UDPStream extends AbstractStream implements Runnable {

	static Logger log = LoggerFactory.getLogger(UDPStream.class);
	protected String address = "0.0.0.0";
	protected Integer port;
	protected DatagramSocket socket;

	protected boolean running = false;

	protected Integer packetSize = 1024;
	protected Integer backlog = 100;
	protected final LinkedBlockingQueue<Data> queue = new LinkedBlockingQueue<Data>();

	protected Thread t;
	protected String id;

	/**
	 * @param url
	 */
	public UDPStream() {
		super((SourceURL) null);
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.io.Stream#init()
	 */
	@Override
	public void init() throws Exception {
		socket = new DatagramSocket(port); // , InetAddress.getByName(address));
		if (running && t.isAlive()) {
			log.error("UDP-Stream {} already running.", this);
			return;
		}

		t = new Thread(this);
		t.start();
	}

	/**
	 * @see stream.io.Stream#read()
	 */
	@Override
	public Data read() throws Exception {

		Data item = null;
		while (item == null) {
			try {
				item = queue.take();
			} catch (InterruptedException ie) {
				if (socket.isClosed())
					return null;
			}
		}

		Data datum = DataFactory.create();
		datum.putAll(item);
		return datum;
	}

	/**
	 * @see stream.io.Stream#close()
	 */
	@Override
	public void close() throws Exception {
		running = false;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		running = true;

		while (running) {
			try {
				byte[] buf = new byte[packetSize];
				DatagramPacket packet = new DatagramPacket(buf, packetSize);
				socket.receive(packet);

				Data item = DataFactory.create();

				int off = packet.getOffset();
				int len = packet.getLength() - off;

				byte[] data = new byte[len];
				System.arraycopy(packet.getData(), off, data, 0, len);

				item.put("udp:data", data);
				item.put("udp:source", packet.getAddress().getHostAddress());
				item.put("udp:port", packet.getPort());
				item.put("udp:size", len);

				synchronized (queue) {
					if (!queue.isEmpty() && queue.remainingCapacity() < 1)
						queue.remove();
					queue.put(item);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return the backlog
	 */
	public Integer getBacklog() {
		return backlog;
	}

	/**
	 * @param backlog
	 *            the backlog to set
	 */
	public void setBacklog(Integer backlog) {
		this.backlog = backlog;
	}

	/**
	 * @see stream.io.AbstractStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return queue.take();
	}
}