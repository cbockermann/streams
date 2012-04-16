/**
 * 
 */
package stream.runtime;

import java.util.HashMap;
import java.util.Map;

import stream.ProcessContext;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public class LocalContext implements ProcessContext {

	final Map<String, Service> lookupService = new HashMap<String, Service>();
	final Map<String, Object> context = new HashMap<String, Object>();

	/**
	 * @see stream.service.LookupService#lookup(java.lang.String)
	 */
	@Override
	public Service lookup(String ref) throws Exception {
		return lookupService.get(ref);
	}

	/**
	 * @see stream.service.LookupService#register(java.lang.String,
	 *      stream.Processor)
	 */
	@Override
	public void register(String ref, Service p) throws Exception {
		lookupService.put(ref, p);
	}

	/**
	 * @see stream.service.LookupService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		lookupService.remove(ref);
	}

	/**
	 * @see stream.ProcessContext#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		return context.get(key);
	}

	/**
	 * 
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
		return get(variable);
	}

}
