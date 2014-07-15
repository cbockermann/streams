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
package stream.io;

import java.util.Collection;

import stream.Data;

/**
 * <p>
 * This interface specifies a sink for data, i.e. any element that can receive
 * data items (e.g. Queues).
 * </p>
 * 
 * @author Christian Bockermann, Hendrik Blom
 * 
 */
public interface Sink {

	public String getId();

	public void setId(String id);

	/**
	 * Initialize the sink implementation. This method will be called only once.
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception;

	/**
	 * Writes data into the instance represented by this sink within a
	 * transaction. The return type indicates whether the insertion of the item
	 * succeeded.
	 * 
	 * @param item
	 * @throws Exception
	 */
	public boolean write(Data item) throws Exception;

	/**
	 * Writes the collection of data items into the queue within <b>a single
	 * transaction</b>. The return value of this method indicates whether the
	 * writing of <b>all</b> items succeeded or not. If the write of any of the
	 * items of the collection fails, then this method will return false.
	 * 
	 * <b>Important:</b> Implementations of this interface need to take care of
	 * this transaction sematic:
	 * <ul>
	 * <li>If only a portion of the items could be written, then a rollback of
	 * the previous writes needs to be performed.</li>
	 * <li>Only a successful write of all items must be ack'ed with a return
	 * type of <code>true</code>.</li>
	 * </ul>
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public boolean write(Collection<Data> data) throws Exception;

	/**
	 * This method is called by the stream runtime environment as the process
	 * container is shut down. This can be used to close file handles, streams
	 * or database connections.
	 * 
	 */
	public void close() throws Exception;
}