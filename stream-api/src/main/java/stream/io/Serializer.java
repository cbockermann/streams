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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * <p>
 * This interface defines an abstract serializer that provides clone and
 * serializing/deserializing of objects.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public interface Serializer {

	/**
	 * Creates a clone of the given object by serializing and de-serializing it.
	 * 
	 * @param object
	 * @return
	 */
	public Serializable clone(Serializable object) throws Exception;

	/**
	 * Reads (de-serializes) an object from the given input stream.
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public Serializable read(InputStream in) throws IOException;

	/**
	 * Writes (serializes) an object into the given output stream.
	 * 
	 * @param object
	 * @param out
	 * @throws IOException
	 */
	public void write(Serializable object, OutputStream out) throws IOException;
}
