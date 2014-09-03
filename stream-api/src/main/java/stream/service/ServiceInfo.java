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
package stream.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a simple class that provides information about a service. The
 * information consists of the name of the service and the service interfaces,
 * provided.
 * 
 * @author Christian Bockermann
 * 
 */
public final class ServiceInfo implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -6189552556440798733L;

	final String name;
	final Class<? extends Service> services[];

	private ServiceInfo(String name, Class<? extends Service> services[]) {
		this.name = name;
		this.services = services;
	}

	public String getName() {
		return this.name;
	}

	public Class<? extends Service>[] getServices() {
		return services;
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("ServiceInfo[" + name + "]{");
		for (int i = 0; i < services.length; i++) {
			s.append(services[i]);
			if (i + 1 < services.length)
				s.append(",");
		}
		s.append("}");
		return s.toString();
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends Service>[] getServiceInterfaces(
			Class<?> serviceImpl) {
		Class<?> cur = serviceImpl;
		List<Class<? extends Service>> intfs = new ArrayList<Class<? extends Service>>();

		while (cur != null) {
			for (Class<?> clazz : cur.getInterfaces()) {
				if (clazz != Service.class && isServiceImplementation(clazz)) {
					intfs.add((Class<? extends Service>) clazz);
				}
			}
			cur = cur.getSuperclass();
		}

		return (Class<? extends Service>[]) intfs.toArray(new Class<?>[intfs
				.size()]);
	}

	public static Class<? extends Service>[] getServiceInterfaces(Service p) {
		return getServiceInterfaces(p.getClass());
	}

	public static ServiceInfo createServiceInfo(String name, Service service)
			throws Exception {
		return new ServiceInfo(name, getServiceInterfaces(service));
	}

	public static ServiceInfo createServiceInfo(String name,
			Class<? extends Service> serviceClass) {
		return new ServiceInfo(name, getServiceInterfaces(serviceClass));
	}

	/**
	 * This method checks whether the given class implements the Service
	 * interface.
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isServiceImplementation(Class<?> clazz) {

		if (clazz == Service.class)
			return true;

		if (clazz.isArray()) {
			return false;
		}

		for (Class<?> intf : clazz.getInterfaces()) {
			if (intf.equals(Service.class) || intf == Service.class) {
				return true;
			}
		}

		return false;
	}
}