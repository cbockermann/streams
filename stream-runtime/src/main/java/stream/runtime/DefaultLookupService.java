/**
 * 
 */
package stream.runtime;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.service.NamingService;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public class DefaultLookupService implements NamingService {

	static Logger log = LoggerFactory.getLogger(DefaultLookupService.class);
	final Map<String, Service> services = new HashMap<String, Service>();
	final String name;

	public DefaultLookupService() {
		this.name = "local";
	}

	public DefaultLookupService(String name) {
		this.name = name;
	}

	protected boolean isLocal(String ref) {
		if (!ref.startsWith("//"))
			return true;

		if (ref.startsWith("//" + name + "/"))
			return true;

		return false;
	}

	/**
	 * @see stream.service.NamingService#lookup(java.lang.String)
	 */
	@Override
	public Service lookup(String ref) throws Exception {
		log.debug("Looking up processor by reference '{}'", ref);

		if (!isLocal(ref)) {
			log.debug("Reference is non-local. Non-local references are currently not supported!");
			return null;
		}

		if (!ref.startsWith("//" + name + "/"))
			return services.get("//" + name + "/" + ref);
		return services.get(ref);
	}

	/**
	 * @see stream.service.NamingService#register(java.lang.String,
	 *      stream.Processor)
	 */
	@Override
	public void register(String ref, Service p) throws Exception {

		if (!isLocal(ref)) {
			throw new Exception("Cannot register remote-references!");
		}

		if (services.containsKey(ref))
			throw new Exception("A processor is already registered for ID '"
					+ ref + "'!");

		log.debug("Registering new processor {} for key {}", p, ref);

		if (ref.startsWith("//" + name + "/"))
			services.put(ref, p);
		else
			services.put("//" + name + "/" + ref, p);
	}

	/**
	 * @see stream.service.NamingService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		if (services.containsKey(ref)) {
			log.debug("Unregistering processor {}", ref);
			services.remove(ref);
		} else
			log.debug("No processor registered for reference {}", ref);
	}
}