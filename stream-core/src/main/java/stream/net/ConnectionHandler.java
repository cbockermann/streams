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
