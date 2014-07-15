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
package stream.runtime;

import stream.Context;

/**
 * <p>
 * The life cycle interface defines a general life cycle of elements within a
 * context. This is provided by elements that exists within the container of the
 * streams environment and which have a well defined life cycle.
 * </p>
 * 
 * @author Christian Bockermann, Hendrik Blom
 * 
 */
public interface LifeCycle {

	/**
	 * Initializes this life cycle instance with the provided context.
	 */
	public void init(Context context) throws Exception;

	/**
	 * This method is called at the end of the life cycle.
	 * 
	 * @throws Exception
	 */
	public void finish() throws Exception;
}
