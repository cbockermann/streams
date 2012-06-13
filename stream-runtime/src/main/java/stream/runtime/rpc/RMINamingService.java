package stream.runtime.rpc;

import java.lang.reflect.Proxy;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.service.NamingService;
import stream.service.Service;


/**
 * An implementation of the NamingService that uses RMI as underlying transport layer.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class RMINamingService implements NamingService {

	static Logger log = LoggerFactory.getLogger( RMINamingService.class );
	final String name;
	final String namespace;
	final Registry registry;
	
	public RMINamingService() throws Exception {
		this( "local" );
	}
	
	public RMINamingService( String name ) throws Exception {
		this.name = name;
		this.namespace = "//" + name + "/";
		int port = 9105;
		
		String names[] = null;
		Registry reg = null;
		try {
			//
			// Try to connect to an existing registry at that port
			//
			reg = LocateRegistry.getRegistry( port );
			names = reg.list();
			log.info( "Found existing registry, names: {}", names );
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		try {
			if( names == null ){
				log.info( "Trying to create new registry at port {}", port );
				registry = LocateRegistry.createRegistry( port );
				log.info( "New registry has registered objects: {}", registry.list() );
			} else {
				registry = reg;
			}
		} catch (Exception e) {
			log.error( "Failed to create registry at port {}: {}", port, e.getMessage() );
			throw new Exception( "Failed to create RMI registry at port " + port + ": " + e.getMessage());
		}
	}

	
	/**
	 * Checks if the given reference is local to this naming service. It is local if
	 * it does not start with "//" or if it starts with this name service's namespace.
	 * 
	 * @param ref
	 * @return
	 */
	protected boolean isLocal( String ref ){
		if( ! ref.startsWith( "//" ) )
			return true;
		
		if( ref.startsWith( namespace ) )
			return true;
		
		return false;
	}
	
	/**
	 * Extracts the local part of a reference, i.e. removes the name space part IF
	 * this is present and matching this name service's namespace. Otherwise this
	 * method will return <code>null</code>.
	 * 
	 * @param ref
	 * @return
	 */
	protected String getLocalRef( String ref ){
		if( isLocal( ref ) ){
			if( ref.startsWith( namespace ) )
				return ref.substring( namespace.length() );
			return ref;
		}
		return null;
	}
	
	/**
	 * @see stream.service.NamingService#lookup(java.lang.String, java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Service> T lookup(String ref, Class<T> serviceClass) throws Exception {
		String localRef = getLocalRef( ref );
		if( localRef == null )
			throw new Exception( "No local reference for '" + ref + "'!");
		RemoteEndpoint re = (RemoteEndpoint) registry.lookup( localRef );
		Service service = (Service) Proxy.newProxyInstance( re.getClass().getClassLoader(), new Class<?>[]{ serviceClass }, new RMIServiceDelegator( re ) );
		return (T) service;
	}

	/**
	 * @see stream.service.NamingService#register(java.lang.String, stream.service.Service)
	 */
	@Override
	public void register(String ref, Service p) throws Exception {
		ServiceProxy proxy = new ServiceProxy( p );
		String localRef = getLocalRef( ref );
		if( localRef == null )
			throw new Exception( "Cannot resolve reference '" + ref + "' as local reference!" );
		registry.rebind( localRef, proxy );
	}

	/**
	 * @see stream.service.NamingService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		String localRef = getLocalRef( ref );
		if( localRef == null )
			throw new Exception( "Cannot resolve reference '" + ref + "' as local reference!" );
		registry.unbind( ref );
	}
}