package stream.runtime.rpc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.service.Service;

public final class ServiceProxy extends UnicastRemoteObject implements RemoteEndpoint {

	/** The unique class ID */
	private static final long serialVersionUID = -8727610044832533407L;

	static Logger log = LoggerFactory.getLogger( ServiceProxy.class );

	final Service serviceImpl;


	public ServiceProxy( Service service ) throws RemoteException {
		this.serviceImpl = service;
	}



	@Override
	public Serializable call(String methodName, Serializable... args) throws RemoteException {
		//log.info( "Would need to call {} with {}", methodName, args );

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
}
