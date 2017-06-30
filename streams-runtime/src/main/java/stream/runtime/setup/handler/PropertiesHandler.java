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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import stream.container.IContainer;
import stream.runtime.DependencyInjection;
import stream.util.Variables;

/**
 * <p>
 * This handler extracts all properties defined in a document and adds these to
 * the process container.
 * </p>
 * 
 * @author Christian Bockermann, Hendrik Blom
 * 
 */
public class PropertiesHandler implements DocumentHandler {

    static Logger log = LoggerFactory.getLogger(PropertiesHandler.class);

    private stream.util.PropertiesHandler pHandle;

    public PropertiesHandler() {
        pHandle = new stream.util.PropertiesHandler();
    }

    /**
     * 
     * @see stream.runtime.setup.handler.DocumentHandler#handle(stream.runtime.ProcessContainer,
     *      org.w3c.dom.Document)
     */
    @Override
    public void handle(IContainer container, Document doc, Variables variables, DependencyInjection depInj)
            throws Exception {
        log.debug("Running properties handler...");
        // ${Home}/streams.properties already added.(streams.run())

        // Read system properties, e.g defined at command line using the -D
        // flag:
        // java -Dproperty-name=property-value
        //
        Variables systemVariables = new Variables();
        pHandle.addSystemProperties(systemVariables);

        // // Add variables to systemVariables to have original state (Not
        // Needed)
        // systemVariables.addVariables(variables);

        // handle maven-like properties, e.g.
        // <properties>
        // <property-name>value-of-property</property-name>
        // </properties>
        // and <properties url="${urlToProperties}"

        NodeList list = doc.getElementsByTagName("properties");
        // handle
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            pHandle.handlePropertiesElement(e, variables, systemVariables);
        }
        // find
        list = doc.getElementsByTagName("Properties");
        // handle
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            pHandle.handlePropertiesElement(e, variables, systemVariables);
        }

        // handle property elements, i.e.
        // <property>
        // <name>property-name</name>
        // <value>property-value</value>
        // </property>
        //
        list = doc.getElementsByTagName("property");
        // handle
        for (int i = 0; i < list.getLength(); i++) {
            Element prop = (Element) list.item(i);
            pHandle.handlePropertyElement(prop, variables, systemVariables);
        }
        // find
        list = doc.getElementsByTagName("Property");
        // handle
        for (int i = 0; i < list.getLength(); i++) {
            Element prop = (Element) list.item(i);
            pHandle.handlePropertyElement(prop, variables, systemVariables);
        }

        // add system properties, e.g defined at command line using the -D flag:
        // java -Dproperty-name=property-value
        //
        // java.net.InetAddress localMachine =
        // java.net.InetAddress.getLocalHost();
        // if
        // (!localMachine.getHostAddress().equals(localMachine.getHostName()))
        // variables.put("machine.name", localMachine.getHostName());
        // pHandle.addSystemProperties(variables);

        // process-local properties at processElementHandler
    }
}
