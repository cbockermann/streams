/**
 * 
 */
package stream.service;

/**
 * <p>
 * This interface provides a service registry. Services can be registered using
 * a simple string reference.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface NamingService {

	/**
	 * The lookup method provides the lookup of a given reference within the
	 * Lookup service. If there is no service registered for that reference,
	 * this method will return <code>null</code>.
	 * 
	 * @param ref
	 * @return
	 * @throws Exception
	 */
	public Service lookup(String ref) throws Exception;

	/**
	 * This method registers a given Service in the naming service.
	 * 
	 * @param ref
	 * @param p
	 * @throws Exception
	 */
	public void register(String ref, Service p) throws Exception;

	/**
	 * This method removed a service from the registry.
	 * 
	 * @param ref
	 * @throws Exception
	 */
	public void unregister(String ref) throws Exception;
}