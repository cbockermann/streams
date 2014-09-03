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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.net.DataTap.ClientHandler;

/**
 * This thread maintains all clients. It
 * <ul>
 * <li>accepts new clients that connect to the socket</li>
 * <li>has a set of {@link ClientHandler} - one for each connected client</li>
 * <li>delegates incoming items to all {@link ClientHandler}</li>
 * </ul>
 * 
 * @author Hendrik Blom, chris, Tobias Beckers
 * 
 */
public class ConnectionHandler implements Runnable {

	private final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);
	/** flag to indicate that the server is active */
	protected boolean running = true;
	/** the {@link ServerSocket} that new clients can connect to */
	protected ServerSocket connectionSocket;

	private final ExecutorService pool;

	protected int port;
	protected int bufferSize;
	protected boolean gzip;
	protected boolean disconnectSlowClients;

	/**
	 * contains the {@link ClientHandler} objects - one for each connected
	 * client
	 */
	protected final List<Connection> connections = new CopyOnWriteArrayList<Connection>();

	public ConnectionHandler(ExecutorService pool, ServerSocket connectionSocket) {
		this.pool = pool;
		this.connectionSocket = connectionSocket;

	}

	public void init(int bufferSize, boolean gzip, boolean disconnectSlowClients)
			throws IOException {
		this.bufferSize = bufferSize;
		this.gzip = gzip;
		this.disconnectSlowClients = disconnectSlowClients;
	}

	public void register(Socket socket, int bufferSize, boolean gzip,
			boolean disconnectSlowClients) throws IOException {
		Connection connection = new Connection(this);
		connection.init(socket, bufferSize, gzip, disconnectSlowClients);
		pool.execute(connection);
		this.connections.add(connection);
	}

	public void unregister(Connection connection) {
		connections.remove(connection);
	}

	/**
	 * This methods accepts new client connections and creates a socket
	 * connection for each client in an endless loop
	 */
	public void run() {

		log.info("Starting TCP DataTap server on socket {}", connectionSocket);
		try {
			while (true) {
				try {
					// wait for clients to connect
					final Socket socket = connectionSocket.accept();
					register(socket, bufferSize, gzip, disconnectSlowClients);
					log.info("New client connection accepted: {}", socket);
				} catch (SocketException e) {
					log.warn(e.toString());
				} catch (Exception e) {
					log.error("Unexpected exception in server thread:", e);
				}
			}
		} finally {
			close();
		}
	}

	/**
	 * Shuts down this {@link ConnectionHandler}.<br/>
	 * Releases all resources.<br/>
	 * Once a {@link ConnectionHandler} has been closed, it is not available for
	 * further processing (i.e. can't be reactivated). A new thread needs to be
	 * created.
	 */
	public void close() {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(5, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	public void write(Data item) {
		for (Connection c : connections) {
			try {
				c.write(item);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
