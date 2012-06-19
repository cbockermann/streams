package stream.runtime.rpc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.service.NamingService;
import stream.service.Service;

/**
 * An implementation of the NamingService that uses RMI as underlying transport
 * layer.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class RMINamingService extends UnicastRemoteObject implements
		RemoteNamingService {

	/** The unique class ID */
	private static final long serialVersionUID = 3886371094536580516L;

	static Logger log = LoggerFactory.getLogger(RMINamingService.class);
	final String name;
	final String namespace;
	final Registry registry;
	final Map<String, Class<? extends Service>> classes = new LinkedHashMap<String, Class<? extends Service>>();

	Announcer announcer;
	Discovery discoverer;
	ContainerAnnouncement announcement;

	Map<String,NamingService> container = new LinkedHashMap<String,NamingService>();
	
	public RMINamingService() throws Exception {
		this("local");
	}

	public RMINamingService(String name) throws Exception {
		this(name, "localhost", 9105, true);
	}

	public RMINamingService(String name, String host, int port)
			throws Exception {
		this(name, host, port, false);
	}

	public RMINamingService(String name, String host, int port, boolean announce)
			throws Exception {
		this.name = name;
		this.namespace = "//" + name + "/";

		InetAddress address = InetAddress.getByName(host);
		System.setProperty("java.rmi.server.hostname", address.getHostAddress());
		String names[] = null;
		Registry reg = null;
		try {
			//
			// Try to connect to an existing registry at that port
			//
			reg = LocateRegistry.getRegistry(port);
			names = reg.list();
			log.debug("Found existing registry, names: {}", names);
		} catch (Exception e) {
			log.debug(
					"No RMI-registry exists as port {}: a new one will be created.",
					port);
			// e.printStackTrace();
		}

		try {
			if (names == null) {
				log.debug("Trying to create new registry at port {}", port);
				registry = LocateRegistry.createRegistry(port);
				log.debug("New registry has registered objects: {}",
						registry.list());
			} else {
				registry = reg;
			}
		} catch (Exception e) {
			log.error("Failed to create registry at port {}: {}", port,
					e.getMessage());
			throw new Exception("Failed to create RMI registry at port " + port
					+ ": " + e.getMessage());
		}
		
		log.info( "my rmi server name is: {}", address.getHostAddress() );
		log.debug("Binding myself to RMI...");
		registry.rebind("@ns", this);

		announcement = new ContainerAnnouncement(name, "rmi",
				address.getHostAddress(), port);
		log.debug("Announcement will be: {}", announcement);
		if (announce) {
			announcer = new Announcer(9200, announcement);
			announcer.setDaemon(true);
			announcer.start();
		}
		
		discover();
	}
	
	
	protected void discover() {
		try {
			Discovery discovery = new Discovery();
			discovery.discover();
			
			
			Map<String,ContainerAnnouncement> containers = discovery.getAnnouncements();
			for( String key : containers.keySet() ){
				
				ContainerAnnouncement info = containers.get(key);
				log.info( "found   {} => {}", key, info);
				if( info.equals( announcement) )
					log.info( "  => That's me!" );
				else {
					NamingService remote = new RMIClient( info.getHost(), info.getPort() );
					log.info( "Created new NamingService-connection for container {}: {}", key, remote );
					
					Map<String,String> services = remote.list();
					log.info( "RemoteServices are:" );
					for( String s : services.keySet() ){
						log.info( "   {} = {}", s, services.get(s) );
					}
					
					container.put( key, remote );
					log.info( "Remote-connection added..." );
					//this.
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	protected String discover( String container ) throws Exception {
		
		Discovery discovery = new Discovery();
		discovery.discover();
		
		Map<String,String> containers = discovery.getContainerURLs();
		if( containers == null || ! containers.containsKey( container ) ){
			throw new Exception( "No container found for name '" + container + "'!" );
		} else {
			log.info( "Found container {}: {}", container, containers.get( container ) );
			return containers.get( container );
		}
	}
	

	/**
	 * Checks if the given reference is local to this naming service. It is
	 * local if it does not start with "//" or if it starts with this name
	 * service's namespace.
	 * 
	 * @param ref
	 * @return
	 */
	protected boolean isLocal(String ref) {
		if (!ref.startsWith("//"))
			return true;

		if (ref.startsWith(namespace))
			return true;

		return false;
	}
	
	/**
	 * Extracts the container name from a service reference.
	 * 
	 * @param ref
	 * @return
	 */
	protected String getContainerName( String ref ){
		if( ! ref.startsWith( "//" ) )
			return this.name;
		
		int idx = ref.indexOf( "/", 3 );
		if( idx < 0 )
			return null;
		
		return ref.substring( 2, idx );
	}

	/**
	 * Extracts the local part of a reference, i.e. removes the name space part
	 * IF this is present and matching this name service's namespace. Otherwise
	 * this method will return <code>null</code>.
	 * 
	 * @param ref
	 * @return
	 */
	protected String getLocalRef(String ref) {
		if (isLocal(ref)) {
			if (!ref.startsWith(namespace))
				return namespace + ref; // ref.substring(namespace.length());
			return ref;
		}
		return null;
	}

	/**
	 * @see stream.service.NamingService#lookup(java.lang.String,
	 *      java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Service> T lookup(String ref, Class<T> serviceClass)
			throws Exception {
		log.debug("Received lookup for {} ({})", ref, serviceClass);

		if( ! isLocal( ref ) ){
			
			log.debug( "Current list of known containers:" );
			for( String key : this.container.keySet() ){
				log.debug( "   {} => {}", container.get(key) );
			}
			
			String con = this.getContainerName( ref );
			log.info( "Container reference is '{}'", con );
			if( con == null )
				throw new Exception( "Failed to determine container for reference '" + ref +"'!" );
			
			NamingService ns = container.get( con );
			if( ns == null ){
				String url = discover( con );
				log.info( "Discovered container {} at {}", con, url );
				throw new Exception( "No container known for name '" + con + "'!" );
			}
			
			return ns.lookup( ref, serviceClass );
			//throw new Exception( "Remote container connections are currently not supported!" );
		}
		
		String localRef = getLocalRef(ref);
		if (localRef == null)
			throw new Exception("No local reference for '" + ref + "'!");
		RemoteEndpoint re = (RemoteEndpoint) registry.lookup(localRef);
		if (re == null)
			throw new Exception("No service entity found for reference '" + ref
					+ "'!");

		Service service = (Service) Proxy.newProxyInstance(re.getClass()
				.getClassLoader(), new Class<?>[] { serviceClass },
				new RMIServiceDelegator(re));
		log.info("Service lookup of '{}' => {}", localRef, service);
		return (T) service;
	}

	/**
	 * @see stream.service.NamingService#register(java.lang.String,
	 *      stream.service.Service)
	 */
	@Override
	public void register(String ref, Service p) throws Exception {

		Class<? extends Service>[] services = getImplementedServices(p);
		if (services.length == 0) {
			log.error("Object {} does not implement a service!", p);
			throw new Exception("Object " + p
					+ " does not implement a service interface!");
		}

		log.info("Service {} registered as {}.", p, ref);
		ServiceProxy proxy = new ServiceProxy(p);
		String localRef = getLocalRef(ref);
		if (localRef == null)
			throw new Exception("Cannot resolve reference '" + ref
					+ "' as local reference!");
		registry.rebind(localRef, proxy);

		classes.put(localRef, services[0]);
		log.info("After registration, classes are: {}", classes);
	}

	@SuppressWarnings("unchecked")
	protected Class<? extends Service>[] getImplementedServices(Service p) {
		List<Class<? extends Service>> services = new ArrayList<Class<? extends Service>>();
		for (Class<?> intf : p.getClass().getInterfaces()) {
			if (intf != Service.class && Service.class.isAssignableFrom(intf)) {
				services.add((Class<? extends Service>) intf);
			}
		}

		return (Class<? extends Service>[]) services.toArray(new Class<?>[4]);
	}

	/**
	 * @see stream.service.NamingService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		String localRef = getLocalRef(ref);
		if (localRef == null)
			throw new Exception("Cannot resolve reference '" + ref
					+ "' as local reference!");
		log.info("Service {} unregistered.", ref);
		registry.unbind(localRef);
		classes.remove(localRef);
		log.info("After un-registration, classes are: {}", classes);
	}

	@Override
	public Map<String, String> list() throws Exception {
		log.info("list() query received, classes are: {}", classes);
		Map<String, String> lst = new LinkedHashMap<String, String>();
		for (String key : classes.keySet()) {
			if( classes.get( key ) != null )
				lst.put(namespace + key, classes.get(key).getCanonicalName());
		}

		return lst;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see stream.runtime.rpc.RemoteNamingService#getServiceInfo(java.lang.String)
	 */
	@Override
	public Map<String, String> getServiceInfo(String name)
			throws RemoteException {

		log.info("Query for service-info on {} received!", name);
		Map<String, String> info = new LinkedHashMap<String, String>();

		Class<? extends Service> clazz = classes.get(getLocalRef(name));

		info.put("name", name);

		for (Method m : clazz.getMethods()) {

			StringBuffer args = new StringBuffer();
			Class<?>[] types = m.getParameterTypes();
			if (types != null && types.length > 0) {
				for (int i = 0; i < types.length; i++) {
					args.append(types[i].getCanonicalName());
					if (i + 1 < types.length) {
						args.append(",");
					}
				}
			}

			String returnType = "void";
			if (m.getReturnType() != null)
				returnType = m.getReturnType().getCanonicalName();
			info.put("method:" + m.getName() + "(" + args.toString() + ")",
					returnType);
		}

		log.info("Returning info {}", info);
		return info;
	}

	@Override
	public Serializable call(String name, String method, String signature,
			Serializable... args) throws RemoteException {
		try {
			log.info("calling '{}.{}'", name, method);
			log.info("   args: {}", args);

			List<Serializable> params = new ArrayList<Serializable>();
			for (int i = 0; i < args.length; i++) {
				params.add(args[i]);
			}

			RemoteEndpoint re = (RemoteEndpoint) registry
					.lookup(getLocalRef(name));
			return re.call(method, signature, params);
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
	}
}