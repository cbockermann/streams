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

/**
 * <p>
 * This interface is the top-level definition of queues provided within the
 * *streams* framework. Queues provide a limited space for temporarily storing
 * data items in main memory.
 * </p>
 * 
 * @author Hendrik Blom, Christian Bockermann
 * 
 */
public interface Queue extends Barrel {

	/**
	 * The maximum size the implementation of this queue is able to hold.
	 * 
	 * @param limit
	 */
	public void setCapacity(Integer limit);

	/**
	 * Returns the maximum size of this queue implementation.
	 * 
	 * @return
	 */
	public Integer getSize();

	public Integer getCapacity();
}
