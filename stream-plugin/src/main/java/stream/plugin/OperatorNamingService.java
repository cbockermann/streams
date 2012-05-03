/**
 * 
 */
package stream.plugin;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

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

	final static OperatorNamingService service = new OperatorNamingService();
	protected final Set<String> names = new LinkedHashSet<String>();

	private final Object lock = new Object();

	public static OperatorNamingService getInstance() {
		return service;
	}

	private OperatorNamingService() {
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
			names.add(ref);
		}
	}

	/**
	 * @see stream.runtime.DefaultNamingService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		try {
			synchronized (lock) {
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
}