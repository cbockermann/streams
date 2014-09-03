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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.net.DataTap.ClientHandler;

/**
 * This thread represents one client.<br/>
 * It maintains all resources associated with the client:
 * <ul>
 * <li>The socket</li>
 * <li>The object send buffer</li>
 * <li>The {@link OutputStream}</li>
 * </ul>
 * and dispatches the buffered data items to the client
 * 
 * @author Hendrik Blom,chris, Tobias Beckers
 * 
 */
public class Connection implements Runnable {
	private final Logger log = LoggerFactory.getLogger(Connection.class);

	/**
	 * the socket to communicate with the client. Output: From server to client;
	 * Input: From client to server
	 */
	protected Socket socket;
	/**
	 * the buffer holding the next items that should be transferred to the
	 * client
	 */
	protected LinkedBlockingQueue<Data> buffer;

	/**
	 * flag to indicate that the corresponding client is connected and can
	 * receive data
	 */
	protected boolean running = true;

	protected int bufferSize;
	protected boolean disconnectSlowClients = true;
	protected ConnectionHandler connectionHandler;

	protected ObjectOutputStream out;

	public Connection(ConnectionHandler connectionHandler) {
		this.connectionHandler = connectionHandler;
	}

	public void init(final Socket socket, int bufferSize, boolean gzip,
			boolean disconnectSlowClients) throws IOException {
		this.disconnectSlowClients = disconnectSlowClients;
		buffer = new LinkedBlockingQueue<Data>(bufferSize);
		this.socket = socket;

		// build output stream cascade: Begin with innermost - socket output
		// stream
		// add buffer to output stream
		OutputStream innerOut = new BufferedOutputStream(
				socket.getOutputStream());
		// add gzip compression stream if wanted
		if (gzip)
			innerOut = new GZIPOutputStream(innerOut);
		// add last outermost stream - the object serialization stream
		out = new ObjectOutputStream(innerOut);

		// if wanted: start thread that monitors if the client disconnects

	}

	/**
	 * This method
	 * <ul>
	 * <li>creates a client connection shutdown listener (if wanted) in an own
	 * thread</li>
	 * <li>writes the buffered data items to the output stream in an endless
	 * loop</li>
	 * </ul>
	 * 
	 */
	public void run() {

		// begin writing the data items from the buffer to the output socket
		while (true) {
			try {
				Data item = buffer.take();
				if (item != null) {
					out.writeObject(item);
					out.flush();
					out.reset();
				}
			} catch (SocketException se) {
				log.warn(se.toString());
				close();
			} catch (Exception e) {
				log.error("Unexpected exception in client thread:", e);
			}
		}
	}

	/**
	 * Shuts down this {@link ClientHandler}.<br/>
	 * Releases all resources.<br/>
	 * Once a {@link ClientHandler} has been closed, it is not available for
	 * further processing (i.e. can't be reactivated). A new instance needs to
	 * be created.
	 */
	public void close() {
		log.info("Closing client socket {}", socket);
		try {
			socket.close();
			this.running = false;
		} catch (IOException e) {
			log.error("Exception thrown while closing client socket " + socket,
					e);
		}
		buffer.clear();
		this.connectionHandler.unregister(this);
	}

	/**
	 * Adds the specified data item to this client's buffer.<br/>
	 * If buffer is not full: Method returns directly and the item is shipped
	 * out later by this thread.<br/>
	 * If buffer is full: Blocking wait for space in the buffer
	 * 
	 * @param chunk
	 * @throws InterruptedException
	 */
	public boolean write(Data item) throws InterruptedException {
		// try to insert item into client buffer
		return buffer.offer(item);

	}

	public boolean isRunning() {
		return running;
	}

	@Override
	public String toString() {
		return super.toString() + " - client on socket " + socket;
	}
}