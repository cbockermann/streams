/**
 * 
 */
package stream.plugin;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Processor;
import stream.runtime.DefaultNamingService;
import stream.service.NamingService;
import stream.service.Service;

/**
 * This naming service extends the default naming service of the stream-api and
 * additionally exports the set of registered names.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class OperatorNamingService extends DefaultNamingService implements
		NamingService {

	static Logger log = LoggerFactory.getLogger(OperatorNamingService.class);

	final static OperatorNamingService service = new OperatorNamingService();
	protected final Set<String> names = new LinkedHashSet<String>();
	protected final Map<String, Processor> processors = new LinkedHashMap<String, Processor>();

	private Registry registry;
	private final Object lock = new Object();

	public static OperatorNamingService getInstance() {
		return service;
	}

	private OperatorNamingService() {
		int port = 9105;
		String host = "127.0.0.1";
		try {

			System.setProperty("stream.service.rmi.port", port + "");
			System.setProperty("stream.service.rmi.host", host);

			System.setProperty("java.rmi.server.hostname", host);
			registry = LocateRegistry.createRegistry(port);
			log.info("Created RMI registry at port {}: {}", port, registry);
		} catch (Exception e) {
			log.error("Failed to create RMI registry at port {}: {}", port,
					e.getMessage());
			if (log.isDebugEnabled())
				e.printStackTrace();
		}
	}

	/**
	 * @see stream.runtime.DefaultNamingService#lookup(java.lang.String)
	 */
	@Override
	public Service lookup(String ref) throws Exception {
		synchronized (lock) {
			return super.lookup(ref);
		}
	}

	/**
	 * @see stream.runtime.DefaultNamingService#register(java.lang.String,
	 *      stream.service.Service)
	 */
	@Override
	public void register(String ref, Service p) throws Exception {
		synchronized (lock) {
			super.register(ref, p);
			log.info("Adding '{}' to set of registered service-names...", ref);
			names.add(ref);
			try {
				log.info("Registering service {} in RMI registry...", p);
				registry.rebind(ref, p);
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
					registry.unbind(ref);
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.unregister(ref);
				names.remove(ref);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public Set<String> getServiceNames() {
		synchronized (lock) {
			return Collections.unmodifiableSet(names);
		}
	}

	public void registerProcessor(String id, Processor p) {
		processors.put(id, p);
	}

	public Map<String, Processor> getProcessors() {
		return processors;
	}
}