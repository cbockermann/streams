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

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Context;
import stream.service.NamingService;
import stream.service.Service;
import stream.service.ServiceInfo;

/**
 * @author chris
 * 
 */
public class ContainerContext implements Context {

	final static String CONTEXT_NAME = "container";
	static Logger log = LoggerFactory.getLogger(ContainerContext.class);
	final Map<String, String> properties = new LinkedHashMap<String, String>();
	NamingService namingService;
	String name;
	final Map<String, NamingService> remoteContainers = new LinkedHashMap<String, NamingService>();

	public ContainerContext() {
		this(new DefaultNamingService());
	}

	public ContainerContext(NamingService ns) {
		this("local", ns);
	}

	public ContainerContext(String name, NamingService ns) {
		this.namingService = ns;
		log.debug("Creating experiment-context '{}'", name);
		this.name = name;
	}

	/**
	 * @return the namingService
	 */
	public NamingService getNamingService() {
		return namingService;
	}

	/**
	 * @param namingService
	 *            the namingService to set
	 */
	public void setNamingService(NamingService namingService) {
		this.namingService = namingService;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperty(String key, String value) {
		if (value == null)
			properties.remove(key);
		else
			properties.put(key, value);
	}

	/**
	 * @see stream.Context#resolve(java.lang.String)
	 */
	@Override
	public Object resolve(String variable) {

		if (variable == null)
			return null;

		String var = variable.trim();
		if (var.startsWith("container.")) {
			String key = var.substring(CONTEXT_NAME.length() + 1);
			if (properties.containsKey(key))
				return properties.get(key);
		}

		return null;
	}

	/**
	 * @see stream.runtime.DefaultNamingService#lookup(java.lang.String)
	 */
	// @Override
	public <T extends Service> T lookup(String ref, Class<T> serviceClass)
			throws Exception {
		return namingService.lookup(ref, serviceClass);
	}

	/**
	 * @see stream.runtime.DefaultNamingService#register(java.lang.String,
	 *      stream.service.Service)
	 */
	public void register(String ref, Service p) throws Exception {
		namingService.register(ref, p);
	}

	/**
	 * @see stream.runtime.DefaultNamingService#unregister(java.lang.String)
	 */
	public void unregister(String ref) throws Exception {
		namingService.unregister(ref);
	}

	public Map<String, ServiceInfo> list() throws Exception {
		return namingService.list();
	}

	/**
	 * @see stream.service.NamingService#addContainer(java.lang.String,
	 *      stream.service.NamingService)
	 */
	public void addContainer(String key, NamingService remoteNamingService)
			throws Exception {
		log.info("Adding remote container '{}' at {}", key, remoteNamingService);
		// remoteContainers.put(key, remoteNamingService);
		this.namingService.addContainer(key, remoteNamingService);
	}

	@Override
	public boolean contains(String key) {
		if (key.startsWith("container.")) {
			key = key.substring(CONTEXT_NAME.length() + 1);
			return properties.containsKey(key);
		}
		return false;
	}
}