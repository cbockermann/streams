/**
 * 
 */
package stream.runtime;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Processor;

/**
 * @author chris
 * 
 */
public class DefaultLookupService implements LookupService {

	static Logger log = LoggerFactory.getLogger(DefaultLookupService.class);
	final Map<String, Processor> processors = new HashMap<String, Processor>();
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
	 * @see stream.runtime.LookupService#lookup(java.lang.String)
	 */
	@Override
	public Processor lookup(String ref) throws Exception {
		log.debug("Looking up processor by reference '{}'", ref);

		if (!isLocal(ref)) {
			log.debug("Reference is non-local. Non-local references are currently not supported!");
			return null;
		}

		if (!ref.startsWith("//" + name + "/"))
			return processors.get("//" + name + "/" + ref);
		return processors.get(ref);
	}

	/**
	 * @see stream.runtime.LookupService#register(java.lang.String,
	 *      stream.data.Processor)
	 */
	@Override
	public void register(String ref, Processor p) throws Exception {

		if (!isLocal(ref)) {
			throw new Exception("Cannot register remote-references!");
		}

		if (processors.containsKey(ref))
			throw new Exception("A processor is already registered for ID '"
					+ ref + "'!");

		log.debug("Registering new processor {} for key {}", p, ref);

		if (ref.startsWith("//" + name + "/"))
			processors.put(ref, p);
		else
			processors.put("//" + name + "/" + ref, p);
	}

	/**
	 * @see stream.runtime.LookupService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		if (processors.containsKey(ref)) {
			log.debug("Unregistering processor {}", ref);
			processors.remove(ref);
		} else
			log.debug("No processor registered for reference {}", ref);
	}
}