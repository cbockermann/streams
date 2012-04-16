/**
 * 
 */
package stream.runtime;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.ProcessContext;
import stream.service.Service;

/**
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class ProcessContextImpl implements ProcessContext {

	static Logger log = LoggerFactory.getLogger(ProcessContextImpl.class);
	final ContainerContext containerContext;
	final Map<String, Object> context = new HashMap<String, Object>();

	public ProcessContextImpl(ContainerContext ctx) {
		containerContext = ctx;
		log.debug("Creating new ProcessContext, parent context is {}", ctx);
	}

	/**
	 * @see stream.service.LookupService#lookup(java.lang.String)
	 */
	@Override
	public Service lookup(String ref) throws Exception {
		return containerContext.lookup(ref);
	}

	/**
	 * @see stream.service.LookupService#register(java.lang.String,
	 *      stream.Processor)
	 */
	@Override
	public void register(String ref, Service p) throws Exception {
		containerContext.register(ref, p);
	}

	/**
	 * @see stream.service.LookupService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		containerContext.unregister(ref);
	}

	/**
	 * @see stream.ProcessContext#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		return context.get(key);
	}

	/**
	 * @see stream.ProcessContext#set(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void set(String key, Object o) {
		context.put(key, o);
	}

	/**
	 * @see stream.Context#resolve(java.lang.String)
	 */
	@Override
	public Object resolve(String variable) {
		if (!variable.startsWith("%{process.")) {
			return containerContext.resolve(variable);
		}

		return get(variable.substring("%{process.".length(),
				variable.length() - 1));
	}
}