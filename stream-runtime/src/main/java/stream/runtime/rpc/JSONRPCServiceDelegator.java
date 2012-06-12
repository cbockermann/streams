package stream.runtime.rpc;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.service.Service;

public class JSONRPCServiceDelegator extends AbstractServiceDelegator {

	static Logger log = LoggerFactory.getLogger( JSONRPCServiceDelegator.class );
	
	public JSONRPCServiceDelegator( Service service ){
		super( service );
	}
	
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
		if( method.equals( "toString" ) && args == null ){
			log.info( "Handling toString() call..." );
			return service.getClass() + "[" + this.toString() + "]";
		}
		
		log.info( "Would need to invoke method: {} with args: {}", method, args );
		return null;
	}
}