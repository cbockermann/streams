package stream.runtime.rpc;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.setup.ServiceInjection;
import stream.service.Service;

public final class ServiceProxy extends UnicastRemoteObject implements
RemoteEndpoint {

	/** The unique class ID */
	private static final long serialVersionUID = -8727610044832533407L;

	static Logger log = LoggerFactory.getLogger(ServiceProxy.class);

	final Service serviceImpl;
	Class<? extends Service> serviceInterfaces[];
	Map<String, Method> methods = new LinkedHashMap<String, Method>();

	public ServiceProxy(Service service) throws RemoteException {
		log.debug("Creating ServiceProxy for {}", service);
		this.serviceImpl = service;

		serviceInterfaces = getServiceInterfaces( service );
		
		for (Class<? extends Service> cl : serviceInterfaces) {

			for (Method m : cl.getMethods()) {
				log.debug("Method: '{}'", m.getName());
				log.debug("    Args: {}", m.getParameterTypes());
				String sig = RMIServiceDelegator.computeSignature(m);
				methods.put(sig, m);
				log.debug("Adding (method,signature) with ({},{})", m, sig);
			}
		}
	}

	@Override
	public Serializable call(String methodName, String signature,
			List<Serializable> args) throws RemoteException {

		try {
			log.debug("Service implementation is {}", serviceImpl);
			Method method = methods.get(signature);
			if (method == null) {
				throw new RuntimeException(
						"No method found for that signature!");
			}

			log.trace("Invoking method {} with args: {}", method.getName(),
					args.toArray());
			return (Serializable) method.invoke(serviceImpl,
					(Object[]) args.toArray());
		} catch (RuntimeException e) {
			log.error("Runtime error: {}", e.getMessage());
			throw e;
		} catch (InvocationTargetException ie) {
			log.error("Invocation exception: {}", ie.getCause());
			throw new RemoteException(ie.getMessage());
		} catch (Exception e) {
			log.error("Exception while calling method: {}", e.getMessage());
			throw new RemoteException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public Class<? extends Service> getServiceInterface() {

		for (Class<?> clazz : serviceImpl.getClass().getInterfaces()) {
			if (clazz != Service.class
					&& ServiceInjection.isServiceImplementation(clazz)) {
				return (Class<? extends Service>) clazz;
			}
		}

		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Class<? extends Service>[] getServiceInterfaces( Class<?> serviceImpl ){
		Class<?> cur = serviceImpl;
		List<Class<? extends Service>> intfs = new ArrayList<Class<? extends Service>>();

		while( cur != null ){
			log.debug( "checking interfaces of class {}", cur );
			for (Class<?> clazz : cur.getInterfaces()) {
				if (clazz != Service.class
						&& ServiceInjection.isServiceImplementation(clazz)) {
					log.debug( "Adding service interface: {}", clazz );
					intfs.add((Class<? extends Service>) clazz);
				} else {
					log.debug( "Not a service interface: {}", clazz );
				}
			}
			cur = cur.getSuperclass();
		}
		
		return (Class<? extends Service>[]) intfs.toArray( new Class<?>[ intfs.size() ] );
	}
	
	public static Class<? extends Service>[] getServiceInterfaces( Service p ){
		return getServiceInterfaces( p.getClass() );
	}
}
