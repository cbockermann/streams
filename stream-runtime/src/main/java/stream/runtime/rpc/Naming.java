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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.service.Service;

/**
 * 
 * 
 * @author chris
 * @deprecated
 * 
 */
public final class Naming {

	static Logger log = LoggerFactory.getLogger(Naming.class);
	private static Registry registry;

	final static Naming NAMING = new Naming();

	private Naming() {
		try {

			registry = LocateRegistry.createRegistry(9100);
			log.info("Created registry at port 9100");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean bind(String name, Service service) throws Exception {
		ServiceProxy proxy = new ServiceProxy(service);
		registry.bind(name, proxy);
		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Service> T lookup(String name,
			Class<T> serviceClass) throws Exception {
		RemoteEndpoint re = (RemoteEndpoint) registry.lookup(name);
		Service service = (Service) Proxy.newProxyInstance(re.getClass()
				.getClassLoader(), new Class<?>[] { serviceClass },
				new ServiceDelegator(re));
		return (T) service;
	}

	public static final class ServiceDelegator implements InvocationHandler {

		Logger log = LoggerFactory.getLogger(ServiceDelegator.class);
		final RemoteEndpoint endpoint;

		public ServiceDelegator(RemoteEndpoint endpoint) {
			this.endpoint = endpoint;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			try {

				if (method.getName().equals("toString") && args == null) {
					return this.toString();
				}

				/*
				 * log.info( "received invoke-request, method: {}, args: {}",
				 * method.getName(), args ); log.info(
				 * "   object reference is: {}", proxy );
				 * 
				 * if( ! (args.getClass().getComponentType() instanceof
				 * Serializable ) ){ log.error(
				 * "Arguments are not serializable!" ); }
				 */

				List<Serializable> params = new ArrayList<Serializable>();

				if (args != null) {
					for (int i = 0; i < args.length; i++) {
						params.add((Serializable) args[i]);
					}
				}

				Object result = endpoint.call(method.getName(),
						RMIServiceDelegator.computeSignature(method), params);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}
}
