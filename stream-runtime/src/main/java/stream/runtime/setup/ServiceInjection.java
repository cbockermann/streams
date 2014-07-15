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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.app.ComputeGraph;
import stream.runtime.ContainerContext;
import stream.runtime.ServiceReference;
import stream.service.Service;
import stream.util.Variables;

/**
 * This class implements the service-injection, i.e. it will check a number of
 * processor objects and their XML configuration. If any of these provides a
 * setter for a Service-Type object and has a corresponding <code>-ref</code>
 * attribute in its XML, then a service-lookup is performed and the resulting
 * object/reference is injected into that setter method.
 * 
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * @deprecated Will be replaced by the new DependencyInjection class
 * 
 */
public class ServiceInjection {

	static Logger log = LoggerFactory.getLogger(ServiceInjection.class);

	/**
	 * This method will iterate over all processors of the given container and
	 * check for setter-methods that obtain a service reference type. If the
	 * processor is configured with a corresponding <code>-ref</code> attribute,
	 * the service is looked up and injected into the processor by the setter
	 * method.
	 * 
	 * @param container
	 *            The container that holds the processors which are to be wired.
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public static void injectServices(Collection<ServiceReference> refs,
			ContainerContext ctx, ComputeGraph graph, Variables variables)
			throws Exception {

		Iterator<ServiceReference> it = refs.iterator();
		while (it.hasNext()) {
			ServiceReference ref = it.next();
			log.debug("Checking service-reference {}", ref);

			String serviceRef = variables.expand(ref.getRef());
			Object consumer = ref.getReceiver();

			if (serviceRef.contains(",")) {
				String[] serviceRefs = serviceRef.split(",");
				for (int i = 0; i < serviceRefs.length; i++) {
					serviceRefs[i] = serviceRefs[i].trim();
				}

				Object services = Array.newInstance(ref.getServiceClass()
						.getComponentType(), serviceRefs.length);

				for (int i = 0; i < serviceRefs.length; i++) {
					Service serv = ctx.lookup(serviceRefs[i],
							ref.getServiceClass());
					log.debug("Found service {}", serv);
					@SuppressWarnings("unchecked")
					Class<Service> sc = (Class<Service>) ref.getServiceClass()
							.getComponentType();
					log.debug("Casting to {}", ref.getServiceClass()
							.getComponentType());

					Array.set(services, i, serv);
					graph.add(consumer, serv);
					log.debug("Adding service for {}", serviceRefs[i]);
				}

				Method m = getServiceSetter(consumer, ref.getProperty(), true);
				if (m != null) {
					log.debug("Injecting service-array {} into consumer {}",
							services, consumer);
					log.info("Invoking method {}", m);
					Object[] args = new Object[] { services };
					log.debug("arguments: {}", args);

					log.debug("Starting invocation on {}", consumer);
					m.invoke(consumer, args);
					continue;
				} else {
					throw new Exception(
							"No service-setter found for service-array "
									+ services + " in object" + consumer);
				}
			}

			Service service = (Service) ctx.lookup(serviceRef,
					ref.getServiceClass());
			if (service == null) {
				throw new Exception(
						"No service could be injected for reference '"
								+ serviceRef
								+ "' - no service registered for that id?!");
			}
			log.debug("Found service of class {} for reference '{}'",
					service.getClass(), serviceRef);

			Method m = getServiceSetter(consumer, ref.getProperty(), false);
			if (m != null) {
				log.debug("Injecting service {} into consumer {}", service,
						consumer);
				log.debug("Method for injection is {}", m);
				graph.add(consumer, service);
				m.invoke(consumer, service);
			} else {
				throw new Exception("Failed to lookup service-setter for "
						+ consumer + " " + ref.getProperty());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends Service> hasServiceSetter(String name,
			Object o) {
		try {

			for (Method m : o.getClass().getMethods()) {
				if (m.getName().equalsIgnoreCase("set" + name)
						&& isServiceSetter(m)) {
					return (Class<? extends Service>) m.getParameterTypes()[0];
				}
			}

			return null;
		} catch (Exception e) {
			log.error("Failed to determine service-setter: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * This method returns a setter method of the given object for the specified
	 * service name. The service name may e.g. be the XML attribute (ending with
	 * &quot;..-ref&quot;.
	 * 
	 * @param o
	 * @param serviceRefName
	 * @return
	 */
	public static Method getServiceSetter(Object o, String serviceRefName,
			boolean array) {
		String serviceName = serviceRefName.replaceAll("-ref$", "");

		for (Method m : o.getClass().getMethods()) {

			if (m.getName().toLowerCase()
					.equals("set" + serviceName.toLowerCase())) {

				log.debug("Found setter  {}(..)  for serviceRefName {}",
						m.getName(), serviceRefName);

				Class<?>[] types = m.getParameterTypes();
				if (types.length != 1) {
					log.debug(
							"Skipping method {} as it does require a *single* parameter!",
							m.getName());
				} else {

					Class<?> type = types[0];
					if (isServiceImplementation(type)) {
						if (!array || (array && type.isArray()))
							return m;
					}
				}
			}

		}

		return null;
	}

	/**
	 * This method checks whether the provided method is a service setter, i.e.
	 * it is a setter method to inject service references into the object.
	 * 
	 * This requires the method to provide the following characteristics:
	 * <ol>
	 * <li>Its names starts with <code>set</code> and provides additional
	 * characters, i.e. <code>set</code> alone is not enough.</li>
	 * <li>It takes a single parameter, which is a service implementation</li>
	 * </ol>
	 * 
	 * @param m
	 * @return
	 */
	public static boolean isServiceSetter(Method m) {

		if (!m.getName().startsWith("set"))
			return false;

		Class<?>[] paramTypes = m.getParameterTypes();
		if (paramTypes.length != 1)
			return false;

		return isServiceImplementation(paramTypes[0]);
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
			log.debug("checking array component-type for service implementation");
			return isServiceImplementation(clazz.getComponentType());
			// log.debug("Injection of arrays of service references is not yet supported!");
			// return false;
		}

		// TODO: Is 'isAssignableFrom(..)' the better way here?
		//
		if (Service.class.isAssignableFrom(clazz))
			return true;

		for (Class<?> intf : clazz.getInterfaces()) {
			log.trace("Checking if {} = {}", intf, Service.class);
			if (intf.equals(Service.class) || intf == Service.class) {
				log.trace("Yes, class {} implements the service interface!",
						clazz);
				return true;
			}
		}

		log.trace("No, class {} does not implement the service interface!",
				clazz);
		return false;
	}
}