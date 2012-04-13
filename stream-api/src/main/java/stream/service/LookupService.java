/**
 * 
 */
package stream.service;


/**
 * @author chris
 * 
 */
public interface LookupService {

	/**
	 * The lookup method provides the lookup of a given reference within the
	 * Lookup service. If there is no processor registered for that reference,
	 * this method will return <code>null</code>.
	 * 
	 * @param ref
	 * @return
	 * @throws Exception
	 */
	public Service lookup(String ref) throws Exception;

	/**
	 * This method registers a given Processor in the lookup service.
	 * 
	 * @param ref
	 * @param p
	 * @throws Exception
	 */
	public void register(String ref, Service p) throws Exception;

	public void unregister(String ref) throws Exception;
}