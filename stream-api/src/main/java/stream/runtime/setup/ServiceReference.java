/**
 * 
 */
package stream.runtime.setup;

import java.io.Serializable;

/**
 * <p>
 * This class is a simple service-reference object. It provides the service
 * reference and the receiver object, i.e. the object that specified the service
 * as a consumer. Additionally it provides a property name, which is the name of
 * the reference attribute in the XML source document.
 * </p>
 * <p>
 * The following snippet illustrates the role of the service reference element.
 * The processor <code>MyProcessor</code> will provide a
 * <code>setService(...)</code> method that will be fed with the service looked
 * up via its name <code>srvId</code>.
 * </p>
 * 
 * <pre>
 *      &lt;MyProcessor service-ref="srvId" /&gt;
 * </pre>
 * <p>
 * The service reference element for this case represents the tuple
 * </p>
 * 
 * <pre>
 *       ( "srvId", myProcessor-object, "service-ref" )
 * </pre>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public final class ServiceReference implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -1468042011864480553L;

	protected final String ref;
	protected final Object receiver;
	protected final String property;

	public ServiceReference(String ref, Object receiver, String property) {
		this.ref = ref;
		this.receiver = receiver;
		this.property = property;
	}

	/**
	 * @return the ref
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * @return the receiver
	 */
	public Object getReceiver() {
		return receiver;
	}

	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}
}
