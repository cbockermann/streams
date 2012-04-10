/**
 * 
 */
package stream.service;

import java.rmi.Remote;

/**
 * <p>
 * This interface defines an abstract service. The interface is a marker for any
 * service-provider classes that may be registered to the context
 * (lookup-service) and that will provide anytime capabilities such as
 * prediction, statistics, etc.
 * </p>
 * <p>
 * To implement or provide a service, one has to define the interface of that
 * service and implement a DataProcessor that implements that service interface.
 * This DataProcessor can then be used in the XML and will be registered to the
 * lookup-service if it has an <code>id</code> attribute provided in the XML.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public interface Service extends Remote {

}
