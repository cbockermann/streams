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
package stream.runtime.setup.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import stream.container.IContainer;
import stream.runtime.DependencyInjection;
import stream.util.Variables;

/**
 * <p>
 * This handler extracts all properties defined in a document and adds these to
 * the process container.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class SystemPropertiesHandler implements DocumentHandler {

	static Logger log = LoggerFactory.getLogger(SystemPropertiesHandler.class);

	/**
	 * @see stream.runtime.setup.handler.DocumentHandler#handle(stream.runtime.ProcessContainer,
	 *      org.w3c.dom.Document)
	 */
	@Override
	public void handle(IContainer container, Document doc, Variables variables,
			DependencyInjection depInj) throws Exception {
		// add system properties, e.g defined at command line using the -D flag:
		// java -Dproperty-name=property-value
		//
		addSystemProperties(container, variables);
	}

	/**
	 * This method adds all the system properties to the container properties,
	 * possibly overwriting pre-defined properties.
	 * 
	 * @param container
	 */
	private void addSystemProperties(IContainer container, Variables variables) {
		for (Object key : System.getProperties().keySet()) {
			// log.debug("Adding system property '{}' = {}", key,
			// System.getProperty(key.toString()));
			variables.set(key.toString(), System.getProperty(key.toString()));
		}
	}
}
