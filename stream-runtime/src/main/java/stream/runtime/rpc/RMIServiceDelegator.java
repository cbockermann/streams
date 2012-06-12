package stream.runtime.rpc;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.runtime.rpc.Naming.ServiceDelegator;

public class RMIServiceDelegator implements InvocationHandler {
	
	Logger log = LoggerFactory.getLogger( ServiceDelegator.class );
	final RemoteEndpoint endpoint;
	
	public RMIServiceDelegator( RemoteEndpoint endpoint ){
		this.endpoint = endpoint;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
		try {
			
			if( method.getName().equals( "toString" ) && args == null ){
				return this.toString();
			}
			
			log.info( "received invoke-request, method: {}, args: {}", method.getName(), args );
			log.info( "   object reference is: {}", proxy );
			
			if( ! (args.getClass().getComponentType() instanceof Serializable ) ){
				log.error( "Arguments are not serializable!" );
			}
			
			Serializable[] params = null;
			
			if( args != null ){
				params = new Serializable[ args.length ];
				for( int i = 0; i < args.length; i++ ){
					params[i] = (Serializable) args[i];
				}
			}
			
			Object result = endpoint.call( method.getName(), params);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
