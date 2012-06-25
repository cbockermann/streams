package stream.runtime.rpc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.ServerSocket;
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
import stream.service.ServiceInfo;

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
	final Map<String, ServiceInfo> classes = new LinkedHashMap<String, ServiceInfo>();

	Announcer announcer;
	Discovery discoverer;
	ContainerAnnouncement announcement;

	Map<String, NamingService> container = new LinkedHashMap<String, NamingService>();

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

		log.info("Looking up host address {}", host);
		InetAddress address = InetAddress.getByName(host);
		String hostAddress = address.getHostAddress();
		log.info("Host address is {}", hostAddress);
		System.setProperty("java.rmi.server.hostname", hostAddress);
		String names[] = null;
		Registry reg = null;

		if (port <= 0) {
			log.info("Checking for free port...");
			port = this.getFreePort();
			log.info("Using port {}", port);
		}

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

		log.debug("my rmi server name is: {}", address.getHostAddress());
		log.debug("Binding myself to RMI...");
		registry.rebind(RemoteNamingService.DIRECTORY_NAME, this);

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

	private int getFreePort() throws Exception {
		ServerSocket sock = new ServerSocket(0);
		int port = sock.getLocalPort();
		sock.close();
		return port;
	}

	/**
	 * @see stream.service.NamingService#addContainer(java.lang.String,
	 *      stream.service.NamingService)
	 */
	@Override
	public void addContainer(String key, NamingService remoteNamingService)
			throws Exception {
		this.container.put(key, remoteNamingService);
	}

	protected void discover() {
		try {
			Discovery discovery = new Discovery();
			discovery.discover();

			Map<String, ContainerAnnouncement> containers = discovery
					.getAnnouncements();
			for (String key : containers.keySet()) {
				ContainerAnnouncement info = containers.get(key);
				log.debug("found   {} => {}", key, info);
				if (info.equals(announcement))
					log.debug("  => That's me!");
				else {
					NamingService remote = new RMIClient(info.getHost(),
							info.getPort());
					log.debug(
							"Created new NamingService-connection for container {}: {}",
							key, remote);

					Map<String, ServiceInfo> services = remote.list();
					log.debug("RemoteServices are:");
					for (String s : services.keySet()) {
						log.debug("   {} = {}", s, services.get(s));
					}

					container.put(key, remote);
					log.debug("Remote-connection added...");
					// this.
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String discover(String container) throws Exception {

		Discovery discovery = new Discovery();
		discovery.discover();

		Map<String, String> containers = discovery.getContainerURLs();
		if (containers == null || !containers.containsKey(container)) {
			throw new Exception("No container found for name '" + container
					+ "'!");
		} else {
			log.debug("Found container {}: {}", container,
					containers.get(container));
			return containers.get(container);
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
	protected String getContainerName(String ref) {
		if (!ref.startsWith("//"))
			return this.name;

		int idx = ref.indexOf("/", 3);
		if (idx < 0)
			return null;

		return ref.substring(2, idx);
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

		if (!isLocal(ref)) {

			log.debug("Current list of known containers:");
			for (String key : this.container.keySet()) {
				log.debug("   {} => {}", container.get(key));
			}

			String con = this.getContainerName(ref);

			if (this.container.containsKey(con)) {
				log.info("Found container-ref {}", con);

				NamingService namingService = container.get(con);
				log.info("remote end-point is: {}", namingService);
				return namingService.lookup(ref, serviceClass);
			}

			log.debug("Container reference is '{}'", con);
			if (con == null)
				throw new Exception(
						"Failed to determine container for reference '" + ref
								+ "'!");

			NamingService ns = container.get(con);
			if (ns == null) {
				String url = discover(con);
				log.debug("Discovered container {} at {}", con, url);
				throw new Exception("No container known for name '" + con
						+ "'!");
			}

			return ns.lookup(ref, serviceClass);
			// throw new Exception(
			// "Remote container connections are currently not supported!" );
		}

		String localRef = getLocalRef(ref);
		if (localRef == null)
			throw new Exception("No local reference for '" + ref + "'!");
		RemoteEndpoint re = (RemoteEndpoint) registry.lookup(localRef);
		if (re == null)
			throw new Exception("No service entity found for reference '" + ref
					+ "'!");

		ServiceInfo info = classes.get(ref);
		if (info == null)
			throw new Exception("No service information available for '" + ref
					+ "'!");

		log.info("Creating proxy for {}, service interfaces: {}", ref,
				classes.get(ref));

		Service service = (Service) Proxy.newProxyInstance(re.getClass()
				.getClassLoader(), info.getServices(), new RMIServiceDelegator(
				re));
		log.debug("Service lookup of '{}' => {}", localRef, service);
		return (T) service;
	}

	/**
	 * @see stream.service.NamingService#register(java.lang.String,
	 *      stream.service.Service)
	 */
	@Override
	public void register(String ref, Service p) throws Exception {

		Class<? extends Service>[] services = ServiceProxy
				.getServiceInterfaces(p);
		if (services.length == 0) {
			log.error("Object {} does not implement a service!", p);
			throw new Exception("Object " + p
					+ " does not implement a service interface!");
		}

		log.debug("Service {} registered as {}.", p, ref);
		ServiceProxy proxy = new ServiceProxy(p);
		String localRef = getLocalRef(ref);
		if (localRef == null)
			throw new Exception("Cannot resolve reference '" + ref
					+ "' as local reference!");
		registry.rebind(localRef, proxy);

		classes.put(localRef, ServiceInfo.createServiceInfo(localRef, p));
		log.debug("After registration, classes are: {}", classes);
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
		log.debug("Service {} unregistered.", ref);
		registry.unbind(localRef);
		classes.remove(localRef);
		log.debug("After un-registration, classes are: {}", classes);
	}

	@Override
	public Map<String, ServiceInfo> list() throws Exception {
		log.debug("list() query received, classes are: {}", classes);
		Map<String, ServiceInfo> lst = new LinkedHashMap<String, ServiceInfo>();
		for (String key : classes.keySet()) {
			if (classes.get(key) != null) {
				ServiceInfo info = classes.get(key);
				log.info("Adding info {} for service {}", info, key);
				lst.put(key, info);
			}
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

		log.debug("Query for service-info on {} received!", name);
		Map<String, String> info = new LinkedHashMap<String, String>();

		ServiceInfo serviceInfo = this.classes.get(getLocalRef(name));
		Class<? extends Service> clazz = serviceInfo.getServices()[0];

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

		log.debug("Returning info {}", info);
		return info;
	}

	@Override
	public Serializable call(String name, String method, String signature,
			Serializable... args) throws RemoteException {
		try {
			log.debug("calling '{}.{}'", name, method);
			log.debug("   args: {}", args);

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