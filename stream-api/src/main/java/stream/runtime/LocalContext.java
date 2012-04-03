/**
 * 
 */
package stream.runtime;

import java.util.HashMap;
import java.util.Map;

import stream.data.Processor;

/**
 * @author chris
 * 
 */
public class LocalContext implements ProcessContext {

	final Map<String, Processor> lookupService = new HashMap<String, Processor>();
	final Map<String, Object> context = new HashMap<String, Object>();

	/**
	 * @see stream.runtime.LookupService#lookup(java.lang.String)
	 */
	@Override
	public Processor lookup(String ref) throws Exception {
		return lookupService.get(ref);
	}

	/**
	 * @see stream.runtime.LookupService#register(java.lang.String,
	 *      stream.data.Processor)
	 */
	@Override
	public void register(String ref, Processor p) throws Exception {
		lookupService.put(ref, p);
	}

	/**
	 * @see stream.runtime.LookupService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		lookupService.remove(ref);
	}

	/**
	 * @see stream.runtime.ProcessContext#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		return context.get(key);
	}

	/**
	 * 
	 * @see stream.runtime.ProcessContext#set(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void set(String key, Object o) {
		context.put(key, o);
	}

	/**
	 * @see stream.runtime.Context#resolve(java.lang.String)
	 */
	@Override
	public Object resolve(String variable) {
		return get(variable);
	}

}
