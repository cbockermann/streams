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
package stream;

/**
 * <p>
 * The process context interface defines the context provided to processors by
 * the executing process. The access to this context is limited to the
 * processors of the process.
 * </p>
 * <p>
 * In contrast to the container context, the process context allows write
 * access, i.e. storing of objects in the context.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface ProcessContext extends Context {

	/**
	 * Retrieves the given element from the process context. This method will
	 * return <code>null</code> if no element exists for the specified key.
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key);

	/**
	 * This method stores the given element in the context, possibly overwriting
	 * any existing object of the same key.
	 * 
	 * @param key
	 * @param o
	 */
	public void set(String key, Object o);

	public void clear();
}
