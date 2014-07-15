/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.runtime;

import java.io.Serializable;

import stream.service.Service;

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
	protected final Class<? extends Service> serviceClass;

	public ServiceReference(String ref, Object receiver, String property,
			Class<? extends Service> serviceClass) {
		this.ref = ref;
		this.receiver = receiver;
		this.property = property;
		this.serviceClass = serviceClass;
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

	public Class<? extends Service> getServiceClass() {
		return serviceClass;
	}

	public boolean hasSameComsumer(ServiceReference ref) {

		if (property == null || ref == null)
			return false;

		return (this.property.equals(ref.property))
				&& this.receiver == ref.receiver;
	}
}
