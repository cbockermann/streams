/**
 * 
 */
package stream.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.ProcessContext;
import stream.service.NamingService;
import stream.service.Service;
import stream.service.ServiceInfo;

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
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Service> T lookup(String ref, Class<T> serviceClass)
			throws Exception {
		return (T) services.get(ref);
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
	 * @see stream.service.NamingService#list()
	 */
	@Override
	public Map<String, ServiceInfo> list() throws Exception {
		Map<String, ServiceInfo> classes = new LinkedHashMap<String, ServiceInfo>();
		for (String key : services.keySet()) {
			classes.put(key,
					ServiceInfo.createServiceInfo(key, services.get(key)));
		}
		return classes;
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

	/**
	 * @see stream.service.NamingService#addContainer(java.lang.String,
	 *      stream.service.NamingService)
	 */
	@Override
	public void addContainer(String key, NamingService remoteNamingService)
			throws Exception {
		throw new Exception("Remote containers not supported by this mock");
	}
}
