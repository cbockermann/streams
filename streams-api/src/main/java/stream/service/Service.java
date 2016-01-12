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
package stream.service;

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
public interface Service {

	public void reset() throws Exception;
}
