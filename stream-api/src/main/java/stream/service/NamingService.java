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
package stream.service;

import java.util.Map;

/**
 * <p>
 * This interface provides a service registry. Services can be registered using
 * a simple string reference.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface NamingService {

	/**
	 * The lookup method provides the lookup of a given reference within the
	 * Lookup service. If there is no service registered for that reference,
	 * this method will return <code>null</code>.
	 * 
	 * @param ref
	 * @return
	 * @throws Exception
	 */
	public <T extends Service> T lookup(String ref, Class<T> serviceClass ) throws Exception;

	/**
	 * This method registers a given Service in the naming service.
	 * 
	 * @param ref
	 * @param p
	 * @throws Exception
	 */
	public void register(String ref, Service p) throws Exception;

	/**
	 * This method removed a service from the registry.
	 * 
	 * @param ref
	 * @throws Exception
	 */
	public void unregister(String ref) throws Exception;
	
	
	/**
	 * This method returns a list of names, registered and the service interfaces for
	 * these names.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> list() throws Exception;
}