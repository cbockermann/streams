/**
 * 
 */
package stream.data;

import java.util.HashMap;
import java.util.Map;

import stream.ProcessContext;
import stream.service.Service;

/**
 * @author chris
 * 
 */
public class ProcessContextMock implements ProcessContext {

	final Map<String, Service> services = new HashMap<String, Service>();
	final Map<String, Object> ctx = new HashMap<String, Object>();

	/**
	 * @see stream.Context#resolve(java.lang.String)
	 */
	@Override
	public Object resolve(String variable) {
		return ctx.get(variable);
	}

	/**
	 * @see stream.service.NamingService#lookup(java.lang.String)
	 */
	@Override
	public Service lookup(String ref) throws Exception {
		return services.get(ref);
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
	 * 
	 * @see stream.service.NamingService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		services.remove(ref);
	}

	/**
	 * @see stream.ProcessContext#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		return ctx.get(key);
	}

	/**
	 * @see stream.ProcessContext#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, Object o) {
		if (key != null)
			ctx.put(key, o);
	}

}
