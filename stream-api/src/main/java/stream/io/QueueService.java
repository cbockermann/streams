/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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

import stream.Data;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public interface QueueService extends Service {

	/**
	 * This method removes the head of the queue. It will return
	 * <code>null</code> if the queue is empty.
	 * 
	 * @return First element in the queue or <code>null</code> for an empty
	 *         queue.
	 */
	public Data poll();

	public Data take();

	/**
	 * This method will insert the given element into the queue. The method does
	 * not block and will return <code>false</code> if insertion could not be
	 * performed successfully.
	 * 
	 * @param item
	 *            The item to insert.
	 * @return <code>true</code> if item inserted, <code>false</code> otherwise.
	 */
	public boolean enqueue(Data item);

	/**
	 * This method will return the current fill-level of the queue. Calls to
	 * this method may return different results based on the current state.
	 * 
	 * @return
	 */
	public int level();

	/**
	 * This method returns the number if items that can be stored in this queue
	 * at maximum. Calls to this method will return the same result every time.
	 * 
	 * @return
	 */
	public int capacity();
}