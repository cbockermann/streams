/**
 * 
 */
package stream.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.AbstractContext;
import stream.service.NamingService;
import stream.service.Service;
import stream.service.ServiceInfo;

/**
 * This class implements a local application context. It provides an in-memory
 * lookup table for service registry.
 * 
 * @author Christian Bockermann
 *
 */
public class LocalContext extends AbstractContext implements ApplicationContext {

	static Logger log = LoggerFactory.getLogger(LocalContext.class);

	final Map<String, Service> services = new HashMap<String, Service>();

	/**
	 */
	public LocalContext() {
		super(UUID.randomUUID().toString());
	}

	/**
	 * @see stream.service.NamingService#lookup(java.lang.String,
	 *      java.lang.Class)
	 */
	@Override
	public <T extends Service> T lookup(String ref, Class<T> serviceClass) throws Exception {
		return serviceClass.cast(services.get(ref));
	}

	/**
	 * @see stream.service.NamingService#register(java.lang.String,
	 *      stream.service.Service)
	 */
	@Override
	public void register(String ref, Service p) throws Exception {
		services.put(ref, p);
	}

	/**
	 * @see stream.service.NamingService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		services.remove(ref);
	}

	/**
	 * @see stream.service.NamingService#list()
	 */
	@Override
	public Map<String, ServiceInfo> list() throws Exception {
		Map<String, ServiceInfo> map = new HashMap<String, ServiceInfo>();
		return map;
	}

	/**
	 * @see stream.service.NamingService#addContainer(java.lang.String,
	 *      stream.service.NamingService)
	 */
	@Override
	public void addContainer(String key, NamingService remoteNamingService) throws Exception {
	}
}