package stream.runtime.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.service.Service;

public class AbstractServiceDelegator implements InvocationHandler {

	static Logger log = LoggerFactory.getLogger( AbstractServiceDelegator.class );
	protected final Service service;
	
	public AbstractServiceDelegator( Service service ){
		this.service = service;
	}

	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		
		if( method.equals( "toString" ) && args == null ){
			log.info( "Handling toString() call..." );
			return service.getClass() + "[" + this.toString() + "]";
		}
		
		throw new Exception( "Not implemented!" );
	}
}
