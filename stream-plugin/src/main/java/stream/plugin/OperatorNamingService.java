/**
 * 
 */
package stream.plugin;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.learner.MetaDataLearner;
import stream.runtime.DefaultNamingService;
import stream.runtime.rpc.ContainerAnnouncement;
import stream.runtime.rpc.Discovery;
import stream.runtime.rpc.RMIClient;
import stream.runtime.rpc.RMINamingService;
import stream.service.NamingService;
import stream.service.Service;
import stream.service.ServiceInfo;

/**
 * This naming service extends the default naming service of the stream-api and
 * additionally exports the set of registered names.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class OperatorNamingService extends DefaultNamingService {

	static Logger log = LoggerFactory.getLogger(OperatorNamingService.class);

	final static OperatorNamingService service = new OperatorNamingService();
	protected final Set<String> names = new LinkedHashSet<String>();
	protected final Map<String, Processor> processors = new LinkedHashMap<String, Processor>();

	private RMINamingService localServices;
	private final Object lock = new Object();
	private Discovery discovery;
	String localName = "RapidMiner";

	public static OperatorNamingService getInstance() {
		return service;
	}

	private OperatorNamingService() {
		int port = 9105;
		String host = "127.0.0.1";
		try {

			port = RMINamingService.getFreePort();
			log.info("Using port {}", port);
			localName = "RapidMiner-" + port;
			localServices = new RMINamingService(localName, host, port);

			System.setProperty("stream.service.rmi.port", port + "");
			System.setProperty("stream.service.rmi.host", host);

			System.setProperty("java.rmi.server.hostname", host);
			log.info("Created RMI registry at port {}: {}", port, localServices);

		} catch (Exception e) {
			log.error("Failed to create RMI registry at port {}: {}", port,
					e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}

		this.discover();
	}

	protected void discover() {
		try {

			log.info("auto-discovery of remote containers...");

			if (discovery == null)
				discovery = new Discovery();

			discovery.discover();

			for (String key : discovery.getAnnouncements().keySet()) {

				ContainerAnnouncement an = discovery.getAnnouncements()
						.get(key);

				log.info("Found container: {}", an);

				if ("rmi".equalsIgnoreCase(an.getProtocol())) {

					addContainer(key, new RMIClient(an.getHost(), an.getPort()));
					log.info("Adding container '{}' at rmi://{}/", key,
							an.getHost() + ":" + an.getPort());
				} else {
					log.error(
							"container-refs with protocol {} are not supported!",
							an.getProtocol());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see stream.runtime.DefaultNamingService#lookup(java.lang.String)
	 */
	@Override
	public <T extends Service> T lookup(String ref, Class<T> serviceClass)
			throws Exception {
		synchronized (lock) {
			if (ref.startsWith("//" + localName + "/") || !ref.startsWith("//")) {
				return localServices.lookup(ref, serviceClass);
			} else {

				String container = getContainerName(ref);
				NamingService remote = remoteContainer.get(container);
				if (remote == null)
					throw new Exception("No container known for name '"
							+ container + "'!");

				return remote.lookup(ref, serviceClass);
			}
		}
	}

	/**
	 * @see stream.runtime.DefaultNamingService#register(java.lang.String,
	 *      stream.service.Service)
	 */
	@Override
	public void register(String ref, Service p) throws Exception {
		synchronized (lock) {
			log.info("Adding '{}' to set of registered service-names...", ref);
			try {
				log.info("Registering service {} in RMI registry...", p);
				localServices.register(ref, p);
				log.info("Registered local services:\n{}", localServices.list());
				names.add(ref);
			} catch (Exception e) {
				log.error("Failed to register service {} at rmi registry: {}",
						p, e.getMessage());
				if (log.isDebugEnabled())
					e.printStackTrace();
			}
		}
	}

	/**
	 * @see stream.runtime.DefaultNamingService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		try {
			synchronized (lock) {
				try {
					localServices.unregister(ref);
				} catch (Exception e) {
					e.printStackTrace();
				}
				names.remove(ref);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public Set<String> getServiceNames() {
		synchronized (lock) {

			Set<String> services = new LinkedHashSet<String>();

			try {
				Map<String, ServiceInfo> list = localServices.list();
				for (String service : list.keySet()) {
					log.info("Adding local service {}", service);
					services.add(service);
				}
			} catch (Exception e) {
				log.error("Failed to add list of local services: {}",
						e.getMessage());
				if (log.isTraceEnabled())
					e.printStackTrace();
			}

			for (String remote : this.remoteContainer.keySet()) {
				NamingService remoteServices = remoteContainer.get(remote);
				try {
					Map<String, ServiceInfo> list = remoteServices.list();
					for (String service : list.keySet()) {
						log.info("Adding service {}", service);
						services.add(service);
					}
				} catch (Exception e) {
					log.error("Error: {}", e.getMessage());
				}
			}
			return Collections.unmodifiableSet(services);
		}
	}

	/*
	 * public void registerProcessor(String id, Processor p) {
	 * 
	 * if (!id.startsWith("//")) { processors.put("//RapidMiner/" + id, p); }
	 * else processors.put(id, p); }
	 * 
	 * public Map<String, Processor> getProcessors() { return processors; }
	 */

	/**
	 * @see stream.runtime.DefaultNamingService#list()
	 */
	@Override
	public Map<String, ServiceInfo> list() throws Exception {

		discover();

		Map<String, ServiceInfo> list = new LinkedHashMap<String, ServiceInfo>();

		Map<String, ServiceInfo> infos = localServices.list();
		for (String key : infos.keySet()) {
			log.info("Adding info ({},{})", key, infos.get(key));
			list.put(key, infos.get(key));
		}

		for (String container : remoteContainer.keySet()) {

			Map<String, ServiceInfo> rlist = remoteContainer.get(container)
					.list();
			log.info("Container {} has services: {}", container, rlist);
			list.putAll(rlist);
		}

		return list;
	}

	public static void main(String[] args) throws Exception {

		OperatorNamingService ons = OperatorNamingService.getInstance();

		ons.register("MetaDataLearner", new MetaDataLearner());

		Thread.sleep(500);

		Map<String, ServiceInfo> list = ons.list();
		log.info("List of services: {}", list);
		for (String key : list.keySet()) {
			log.info("{} = {}", key, list.get(key));
		}
	}
}