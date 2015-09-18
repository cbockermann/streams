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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractProcessor;
import stream.Data;
import stream.ProcessContext;
import stream.annotations.Parameter;

/**
 * @author chris
 * 
 */
public class DataTap extends AbstractProcessor {

	static Logger log = LoggerFactory.getLogger(DataTap.class);
	String address = "0.0.0.0";
	Integer port = 9100;
	boolean gzip = false;
	ServerThread server;

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
	@Parameter(description = "The socket address to listen on, needs to be a local address, defaults to 0.0.0.0.")
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
	@Parameter(description = "The port to listen on for incoming tap connections, defaults to 9100.")
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return the gzip
	 */
	public boolean isGzip() {
		return gzip;
	}

	/**
	 * @param gzip
	 *            the gzip to set
	 */
	@Parameter(description = "This parameter allows for enabling GZIP compression on the TCP stream, default is no compression.")
	public void setGzip(boolean gzip) {
		this.gzip = gzip;
	}

	/**
	 * @see stream.AbstractProcessor#init(stream.ProcessContext)
	 */
	@Override
	public void init(ProcessContext ctx) throws Exception {
		super.init(ctx);
		server = new ServerThread(address, port, gzip);
		server.start();
	}

	/**
	 * @see stream.AbstractProcessor#finish()
	 */
	@Override
	public void finish() throws Exception {
		super.finish();
		server.shutdown();
	}

	/**
	 * @see stream.Processor#process(stream.Data)
	 */
	@Override
	public Data process(final Data input) {
		int clients = server.getNumberOfClients();
		if (clients > 0) {
			log.debug("Copying item to {} clients", clients);
			server.add(input);
		} else {
			log.debug("No clients connected, no tap for this item.");
		}
		return input;
	}

	public final static class ServerThread extends Thread {

		static Logger log = LoggerFactory.getLogger(ServerThread.class);
		boolean running = true;
		final ServerSocket server;
		final List<ClientHandler> clients = new ArrayList<ClientHandler>();
		final boolean gzip;

		public ServerThread(String address, int port, boolean gz)
				throws Exception {
			server = new ServerSocket(port);
			gzip = gz;
			setDaemon(true);
		}

		public void run() {

			log.info("Starting TCP DataTap server on socket {}", server);
			try {
				while (running) {
					try {
						final Socket socket = server.accept();
						final ClientHandler handler = new ClientHandler(this,
								socket, gzip);
						log.info("New client connection accepted: {}", socket);
						synchronized (clients) {
							clients.add(handler);
						}
						handler.start();
					} catch (Exception e) {
						log.error("Error: {}", e.getMessage());
						e.printStackTrace();
					}
				}
			} finally {
				try {
					log.info("Closing TAP socket");
					server.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void shutdown() {
			log.debug("Shutting down ServerThread");
			running = false;
			this.interrupt();
		}

		public int getNumberOfClients() {
			synchronized (clients) {
				return clients.size();
			}
		}

		public void add(final Data item) {
			synchronized (clients) {
				log.debug("Spreading data item to {} clients", clients.size());
				for (ClientHandler client : clients) {
					client.add(item);
				}
			}
		}

		public void clientExited(ClientHandler client) {
			synchronized (clients) {
				log.debug("Removed client {} from the list of clients.", client);
				clients.remove(client);
			}
		}
	}

	public final static class ClientHandler extends Thread {

		final static Logger log = LoggerFactory.getLogger(ClientHandler.class);
		final Socket socket;
		final LinkedBlockingQueue<Data> chunks = new LinkedBlockingQueue<Data>();
		final ObjectOutputStream out;
		final ServerThread server;

		public ClientHandler(ServerThread server, Socket sock, boolean gzip)
				throws IOException {
			this.server = server;
			this.socket = sock;
			if (gzip) {
				out = new ObjectOutputStream(new GZIPOutputStream(
						sock.getOutputStream()));
			} else {
				out = new ObjectOutputStream(sock.getOutputStream());
			}
		}

		public void run() {

			boolean running = true;

			while (running && socket.isConnected()) {

				try {
					Data chunk = chunks.take();
					if (chunk != null) {
						out.writeObject(chunk);
						out.reset();
					}
				} catch (SocketException se) {
					log.error("Socket error: {}", se.getMessage());
					log.debug("Disconnecting client...");
					running = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			log.debug("Sending exit signal to server...");
			chunks.clear();
			server.clientExited(this);
		}

		public void add(Data chunk) {
			if (chunks.size() > 25) {
				log.debug("{} chunks pending in client queue for {}", socket);
			}
			chunks.add(chunk);
		}
	}
}