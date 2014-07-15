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
package stream.runtime.rpc;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.service.NamingService;
import stream.service.Service;
import stream.service.ServiceInfo;

public class RMIClient implements RemoteNamingService {

	static Logger log = LoggerFactory.getLogger(RMIClient.class);
	final String host;
	final int port;
	final Registry registry;
	RemoteNamingService namingService;
	Map<String, NamingService> remotes = new LinkedHashMap<String, NamingService>();

	public RMIClient(int port) throws Exception {
		this("127.0.0.1", port);
	}

	public RMIClient(String host, int port) throws Exception {
		this.host = host;
		this.port = port;

		String codeBase = System.getProperty("java.rmi.server.codebase", "");
		if (!codeBase.contains("http://" + host + ":9999/")) {
			if (codeBase.isEmpty())
				codeBase = "http://" + host + ":9999/";
			else {
				codeBase = codeBase + " http://" + host + ":9999/";
			}
		}

		System.setProperty("java.rmi.server.codebase", codeBase);
		registry = LocateRegistry.getRegistry(host, port);
		log.debug("Registry is: {}", registry);

		namingService = (RemoteNamingService) registry
				.lookup(RemoteNamingService.DIRECTORY_NAME);
		log.debug("NamingService is: {}", namingService);
	}

	@Override
	public <T extends Service> T lookup(String ref, Class<T> serviceClass)
			throws Exception {
		return namingService.lookup(ref, serviceClass);
	}

	@Override
	public void register(String ref, Service p) throws Exception {
	}

	@Override
	public void unregister(String ref) throws Exception {
	}

	@Override
	public Map<String, ServiceInfo> list() throws Exception {
		return namingService.list();
	}

	@Override
	public Map<String, String> getServiceInfo(String name)
			throws RemoteException {
		return namingService.getServiceInfo(name);
	}

	@Override
	public Serializable call(String name, String method, String signature,
			Serializable... args) throws RemoteException {
		return namingService.call(name, method, signature, args);
	}

	/**
	 * @see stream.service.NamingService#addContainer(java.lang.String,
	 *      stream.service.NamingService)
	 */
	@Override
	public void addContainer(String key, NamingService remoteNamingService)
			throws Exception {
		this.remotes.put(key, remoteNamingService);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "rmi://" + host + ":" + port;
	}
}