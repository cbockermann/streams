/**
 * 
 */
package stream.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.data.Data;
import stream.data.DataFactory;
import stream.io.DataStream;

/**
 * @author chris
 * 
 */
public class UDPStream implements DataStream, Runnable {

	static Logger log = LoggerFactory.getLogger(UDPStream.class);
	private String protocol = "udp";
	protected String address = "0.0.0.0";
	protected Integer port;
	protected DatagramSocket socket;

	protected boolean running = false;

	protected Integer packetSize = 1024;
	protected Integer backlog = 100;
	protected final LinkedBlockingQueue<Data> queue = new LinkedBlockingQueue<Data>();
	protected final List<Processor> processors = new ArrayList<Processor>();

	protected Thread t;
	protected String id;

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return new HashMap<String, Class<?>>();
	}

	/**
	 * @see stream.io.DataStream#init()
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
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return readNext(DataFactory.create());
	}

	/**
	 * @see stream.io.DataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readNext(Data datum) throws Exception {

		Data item = null;
		while (item == null) {
			try {
				item = queue.take();
			} catch (InterruptedException ie) {
				if (socket.isClosed())
					return null;
			}
		}

		datum.putAll(item);
		return datum;
	}

	/**
	 * @see stream.io.DataStream#getPreprocessors()
	 */
	@Override
	public List<Processor> getPreprocessors() {
		return processors;
	}

	/**
	 * @see stream.io.DataStream#close()
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
}