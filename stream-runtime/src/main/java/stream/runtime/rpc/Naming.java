package stream.runtime.rpc;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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

				Serializable[] params = null;

				if (args != null) {
					params = new Serializable[args.length];
					for (int i = 0; i < args.length; i++) {
						params[i] = (Serializable) args[i];
					}
				}

				Object result = endpoint.call(method.getName(), params);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}
}
