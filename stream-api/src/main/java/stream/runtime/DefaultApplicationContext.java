/**
 * 
 */
package stream.runtime;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.Context;
import stream.service.NamingService;
import stream.service.Service;
import stream.service.ServiceInfo;
import stream.util.Variables;

/**
 * @author chris
 *
 */
public class DefaultApplicationContext implements ApplicationContext, Serializable {

	private static final long serialVersionUID = -5614833980900180506L;

	final String[] scopes = new String[] { "application", "container" };
	final String id;
	final Map<String, Object> content = new LinkedHashMap<String, Object>();

	NamingService namingService;

	public DefaultApplicationContext(String id, Variables variables) {
		this.id = id;
		this.content.putAll(variables);
	}

	/**
	 * @return the namingService
	 */
	public NamingService getNamingService() {
		return namingService;
	}

	/**
	 * @param namingService
	 *            the namingService to set
	 */
	public void setNamingService(NamingService namingService) {
		this.namingService = namingService;
	}

	/**
	 * @see stream.Context#resolve(java.lang.String)
	 */
	@Override
	public Object resolve(String key) {

		for (String scope : scopes) {
			if (key.startsWith(scope + ".")) {
				String k = key.substring(scope.length() + 1);
				if ("id".equals(k)) {
					return getId();
				}
				return content.get(k);
			}
		}

		return null;
	}

	/**
	 * @see stream.Context#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String key) {
		if (key.equals("id")) {
			return true;
		}

		return content.containsKey(key);
	}

	/**
	 * @see stream.Context#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see stream.Context#getParent()
	 */
	@Override
	public Context getParent() {
		return null;
	}

	public String prefix() {
		return "application";
	}

	/**
	 * @see stream.Context#path()
	 */
	@Override
	public String path() {
		return "application:" + getId();
	}

	/**
	 * @see stream.service.NamingService#lookup(java.lang.String,
	 *      java.lang.Class)
	 */
	@Override
	public <T extends Service> T lookup(String ref, Class<T> serviceClass) throws Exception {
		return namingService.lookup(ref, serviceClass);
	}

	/**
	 * @see stream.service.NamingService#register(java.lang.String,
	 *      stream.service.Service)
	 */
	@Override
	public void register(String ref, Service p) throws Exception {
		namingService.register(ref, p);
	}

	/**
	 * @see stream.service.NamingService#unregister(java.lang.String)
	 */
	@Override
	public void unregister(String ref) throws Exception {
		namingService.unregister(ref);
	}

	/**
	 * @see stream.service.NamingService#list()
	 */
	@Override
	public Map<String, ServiceInfo> list() throws Exception {
		return namingService.list();
	}

	/**
	 * @see stream.service.NamingService#addContainer(java.lang.String,
	 *      stream.service.NamingService)
	 */
	@Override
	public void addContainer(String key, NamingService remoteNamingService) throws Exception {
		namingService.addContainer(key, remoteNamingService);
	}
}
