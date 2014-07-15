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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import stream.util.Variables;

/**
 * @author chris
 * 
 */
public class DefaultParameterFinder implements ParameterFinder {

	final static List<ParameterFinder> finders = new ArrayList<ParameterFinder>();

	/**
	 * @see stream.runtime.setup.ParameterFinder#findParameters(java.lang.Class)
	 */
	@Override
	public Map<String, Class<?>> findParameters(Class<?> clazz) {
		Map<String, Class<?>> params = ParameterDiscovery
				.discoverParameters(clazz);
		for (ParameterFinder finder : finders) {
			params.putAll(finder.findParameters(clazz));
		}
		return params;
	}

	/**
	 * @see stream.runtime.setup.ParameterFinder#inject(java.util.Map,
	 *      java.lang.Object)
	 */
	@Override
	public Set<String> inject(Map<String, ?> params, Object o) throws Exception {
		Set<String> set = new LinkedHashSet<String>();
		set.addAll(ParameterInjection.inject(o, params, new Variables()));

		for (ParameterFinder finder : finders) {
			set.addAll(finder.inject(params, o));
		}
		return set;
	}
}
