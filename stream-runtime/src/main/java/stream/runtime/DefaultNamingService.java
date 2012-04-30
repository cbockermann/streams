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
package stream.runtime;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.service.NamingService;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public class DefaultNamingService implements NamingService {

	static Logger log = LoggerFactory.getLogger(DefaultNamingService.class);
	final Map<String, Service> services = new HashMap<String, Service>();
	final String name;

	public DefaultNamingService() {
		this.name = "local";
	}

	public DefaultNamingService(String name) {
		this.name = name;
	}

	protected boolean isLocal(String ref) {
		if (!ref.startsWith("//"))
			return true;

		if (ref.startsWith("//" + name + "/"))
			return true;

		return false;
	}

	/**
	 * @see stream.service.NamingService#lookup(java.lang.String)
	 */
	@Override
	public Service lookup(String ref) throws Exception {
		log.debug("Looking up processor by reference '{}'", ref);

		if (!isLocal(ref)) {
			log.debug("Reference is non-local. Non-local references are currently not supported!");
			return null;
		}

		if (!ref.startsWith("//" + name + "/"))
			return services.get("//" + name + "/" + ref);
		return services.get(ref);
	}

	/**
	 * @see stream.service.NamingService#register(java.lang.String,
	 *      stream.Processor)
	 */
	@Override
	public void register(String ref, Service p) throws Exception {

		if (!isLocal(ref)) {
			throw new Exception("Cannot register remote-references!");
		}

		if (services.containsKey(ref))
			throw new Exception("A processor is already registered for ID '"
					+ ref + "'!");

		log.debug("Registering new processor {} for key {}", p, ref);

		if (ref.startsWith("//" + name + "/"))
			services.put(ref, p);
		else
			services.put("//" + name + "/" + ref, p);
	}

	/**
	 * @see stream.service.NamingService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		if (services.containsKey(ref)) {
			log.debug("Unregistering processor {}", ref);
			services.remove(ref);
		} else
			log.debug("No processor registered for reference {}", ref);
	}
}