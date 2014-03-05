/**
 * 
 */
package stream.data;

import java.util.HashMap;
import java.util.Map;

import stream.ProcessContext;

/**
 * @author chris
 * 
 */
public class ProcessContextMock2 implements ProcessContext {

	final Map<String, Object> ctx = new HashMap<String, Object>();

	/**
	 * @see stream.Context#resolve(java.lang.String)
	 */
	@Override
	public Object resolve(String variable) {
		return ctx.get(variable);
	}

	// /**
	// * @see stream.service.NamingService#lookup(java.lang.String)
	// */
	// @SuppressWarnings("unchecked")
	// @Override
	// public <T extends Service> T lookup(String ref, Class<T> serviceClass)
	// throws Exception {
	// return (T) services.get(ref);
	// }

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

	@Override
	public void clear() {
		ctx.clear();
	}
}
