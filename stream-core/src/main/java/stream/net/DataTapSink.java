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

import java.net.ServerSocket;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Data;
import stream.annotations.Parameter;
import stream.io.Sink;

/**
 * <p>
 * This {@link Sink} receives data items and transmit them via TCP socket
 * connections.<br/>
 * It can be used for inter-process- / inter-machine-communication.
 * </p>
 * 
 * <p>
 * Other objects can register themselves to be notified, if a new client
 * connects to this DataTapSink - see e.g. {@link OnEvent}.<br/>
 * To receive the 'new client connect event' they must
 * <ul>
 * <li>implement the {@link CallableEventListener} interface and</li>
 * <li>register themselves to this DataTapSink's {@link ListenerHandler}:
 * <ul>
 * <li>{@link #getListenerHandler()} and invoke</li>
 * <li>{@link ListenerHandler#addListener(CallableEventListener)}-method on the
 * returned object.</li>
 * </ul>
 * </ul>
 * Therefore this class implements the {@link Listenable} interface.
 * </p>
 * 
 * @author Hendrik Blom, chris, Tobias Beckers
 * 
 * 
 * 
 * 
 * 
 *         TODO Handle / Throw general Exceptions in this class or hand them
 *         over to caller
 * 
 * 
 * 
 * 
 */
public class DataTapSink implements Sink {

	// STREAMS-PARAMETERS

	/** The port to listen on for incoming tap connections, defaults to 9100. */
	protected Integer port = 9100;
	/** The buffer size (number of items) used for each client */
	protected int clientBufferSize = 10;
	/**
	 * This parameter allows for enabling GZIP compression on the TCP stream,
	 * default is no compression.
	 */
	protected boolean gzip = false;

	/**
	 * Defines if this sink actively listens to client disconnect events (=
	 * client's input stream read-method returns '-1'). If true, this sink will
	 * close the connection immediately if the event occurs. Otherwise a client
	 * disconnect will be detected (and the connection will also be closed) the
	 * next time, an item should be transferred to the client (and therefore the
	 * client's output stream write-method is unsuccessful).
	 */
	protected boolean detectClientClose = false;
	/**
	 * Defines if slow clients should be disconnected. A client is defined to be
	 * 'slow', if its buffer is completely filled.
	 */
	protected boolean disconnectSlowClients = false;
	/** Defines if the event 'client buffer is full' should be logged. */
	protected boolean logBufferFull = false;

	// OTHER CLASS FIELDS

	private static final Logger log = LoggerFactory
			.getLogger(DataTapSink.class);

	/** the {@link ConnectionHandler} */
	protected ConnectionHandler connectionHandler;

	/**
	 * contains the {@link ClientConnectListener} that are notified when a new
	 * client connects to this server
	 */

	protected String id;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
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
	 * Returns the buffer size used for each client
	 * 
	 * @return the buffer size used for each client
	 */
	public int getClientBufferSize() {
		return clientBufferSize;
	}

	/**
	 * Sets the buffer size used for each client
	 * 
	 * @param clientBufferSize
	 */
	@Parameter(description = "The buffer size (number of items) used for each client", defaultValue = "10")
	public void setClientBufferSize(int clientBufferSize) {
		this.clientBufferSize = clientBufferSize;
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

	public boolean isActivelyDetectClientClose() {
		return detectClientClose;
	}

	@Parameter(required = false, defaultValue = "false", description = "Defines if this sink actively listens to client disconnect events (= client's input stream read-method returns '-1')."
			+ "If true, this sink will close the connection immediately if the event occurs."
			+ "Otherwise a client disconnect will be detected (and the connection will also be closed)"
			+ "the next time, an item should be transferred to the client (and therefore the client's output stream write-method is unsuccessful).")
	public void setDetectClientClose(boolean activelyDetectClientClose) {
		this.detectClientClose = activelyDetectClientClose;
	}

	public boolean isDisconnectSlowClients() {
		return disconnectSlowClients;
	}

	@Parameter(required = false, defaultValue = "false", description = "Defines if slow clients should be disconnected. A client is defined to be 'slow', if its buffer is completely filled.")
	public void setDisconnectSlowClients(boolean disconnectSlowClients) {
		this.disconnectSlowClients = disconnectSlowClients;
	}

	/** {@inheritDoc} */
	@Override
	public void init() throws Exception {
		final ExecutorService pool = Executors.newCachedThreadPool();
		final ServerSocket socket = new ServerSocket(port);
		connectionHandler = new ConnectionHandler(pool, socket);
		connectionHandler.init(clientBufferSize, gzip, disconnectSlowClients);

		pool.execute(connectionHandler);
	}

	@Override
	public boolean write(Collection<Data> data) throws Exception {
		for (Data d : data) {
			write(d);
		}
		return true;
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 * <p>
	 * This operation blocks if at least one client buffer is exceeded.
	 * </p>
	 */
	@Override
	public boolean write(Data item) throws Exception {
		if (item != null)
			connectionHandler.write(item);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws Exception {
		connectionHandler.close();
	}

}