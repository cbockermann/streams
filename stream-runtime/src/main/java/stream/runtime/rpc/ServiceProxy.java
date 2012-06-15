package stream.runtime.rpc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.setup.ServiceInjection;
import stream.service.Service;

public final class ServiceProxy extends UnicastRemoteObject implements RemoteEndpoint {

	/** The unique class ID */
	private static final long serialVersionUID = -8727610044832533407L;

	static Logger log = LoggerFactory.getLogger( ServiceProxy.class );

	final Service serviceImpl;
	Class<? extends Service> serviceInterfaces[];

	@SuppressWarnings("unchecked")
	public ServiceProxy( Service service ) throws RemoteException {
		this.serviceImpl = service;
		
		
		List<Class<? extends Service>> intfs = new ArrayList<Class<? extends Service>>();
		for( Class<?> clazz : serviceImpl.getClass().getInterfaces() ){
			if( clazz != Service.class && ServiceInjection.isServiceImplementation( clazz ) ){
				intfs.add( (Class<? extends Service>) clazz );
			}
		}
		
		serviceInterfaces = (Class<? extends Service>[]) intfs.toArray( new Class<?>[ intfs.size() ] );
	}



	@Override
	public Serializable call(String methodName, Serializable... args) throws RemoteException {

		Class<?>[] types = null;

		if( args != null ){
			types = new Class<?>[ args.length ];
			for( int i = 0; i < args.length; i++ ){
				types[i] = args[i].getClass();
			}
		}

		try {
			Method method = serviceImpl.getClass().getMethod(methodName, types);
			return (Serializable) method.invoke( serviceImpl, (Object[]) args );
		} catch (Exception e) {
			throw new RemoteException( e.getMessage() );
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public Class<? extends Service> getServiceInterface(){
		
		for( Class<?> clazz : serviceImpl.getClass().getInterfaces() ){
			if( clazz != Service.class && ServiceInjection.isServiceImplementation( clazz ) ){
				return (Class<? extends Service>) clazz;
			}
		}
		
		return null;
	}
}
