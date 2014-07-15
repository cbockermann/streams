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
package stream.runtime.setup;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 * This interface defines a single method for finding parameters given a class.
 * The implementing classes may use annotations or conventions to search the
 * class for parameters.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface ParameterFinder {

	/**
	 * Returns a map of parameters (name,type) for the specified class.
	 * 
	 * @param clazz
	 * @return
	 */
	public Map<String, Class<?>> findParameters(Class<?> clazz);

	/**
	 * This method injects the given parameters into the specified object, based
	 * on the map returned by the {{@link #findParameters(Class)} method.
	 * 
	 * @param params
	 * @param o
	 * @return The parameter names that have been injected.
	 */
	public Set<String> inject(Map<String, ?> params, Object o) throws Exception;
}
