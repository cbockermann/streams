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

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.CopiesUtils;
import stream.Copy;
import stream.io.Stream;
import stream.runtime.DependencyInjection;
import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.setup.factory.ObjectFactory;
import stream.runtime.setup.factory.ProcessorFactory;
import stream.runtime.setup.factory.StreamFactory;
import stream.service.Service;
import stream.util.Variables;
import streams.application.ComputeGraph;

/**
 * @author chris,hendrik
 * 
 */
public class StreamElementHandler implements ElementHandler {

    static Logger log = LoggerFactory.getLogger(StreamElementHandler.class);
    final ObjectFactory objectFactory;
    final ProcessorFactory processorFactory;

    public StreamElementHandler(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
        this.processorFactory = new ProcessorFactory(objectFactory);
    }

    /**
     * @see stream.runtime.ElementHandler#getKey()
     */
    @Override
    public String getKey() {
        return "Stream";
    }

    /**
     * @see stream.runtime.ElementHandler#handlesElement(org.w3c.dom.Element)
     */
    @Override
    public boolean handlesElement(Element element) {
        if (element == null)
            return false;

        return "Stream".equalsIgnoreCase(element.getNodeName()) || "DataStream".equalsIgnoreCase(element.getNodeName());
    }

    /**
     * @see stream.runtime.ElementHandler#handleElement(stream.container.ProcessContainer
     *      , org.w3c.dom.Element)
     */
    @Override
    public void handleElement(ProcessContainer container, Element element, Variables variables,
            DependencyInjection dependencyInjection) throws Exception {
        try {
            final ComputeGraph computeGraph = container.computeGraph();
            Map<String, String> attr = objectFactory.getAttributes(element);
            String id = attr.get("id");

            List<Copy> cp = new ArrayList<Copy>();
            String copies = element.getAttribute("copies");
            log.debug("found 'copies' attribute, value is: '{}'", copies);

            // Single stream
            if (copies == null || copies.trim().isEmpty()) {
                log.debug("Processing single-stream (no copies)");
                id = variables.expand(id);
                Copy c = new Copy();
                c.setId(id);
                cp.add(c);
            }
            // multiple streams
            else {
                log.debug("Processing multiple copies of the stream element...");
                copies = variables.expand(copies);
                cp = Arrays.asList(CopiesUtils.parse(copies));
            }

            for (Copy copy : cp) {
                log.debug("Creating stream for copy '{}'", copy.getId());
                Variables local = new Variables(variables);

                CopiesUtils.addCopyIds(local, copy);

                String lid = local.expand(id);

                Stream stream = StreamFactory.createStream(objectFactory, element, local);
                if (stream != null) {
                    if (lid == null)
                        lid = "" + stream;
                    stream.setId(lid);

                    try {
                        Method m = stream.getClass().getMethod("read", (Class<?>[]) null);
                        int mod = m.getModifiers();
                        if (!Modifier.isSynchronized(mod)) {
                            log.warn("DANGER: Use of non-synchronized read() method in stream implementation!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    computeGraph.addStream(lid, stream);
                    container.registerStream(lid, stream);
                }

                if (stream instanceof Service) {
                    container.getContext().register(lid, (Service) stream);
                }
            }
        } catch (FileNotFoundException fnfe) {
            throw new Exception("Cannot create stream from referenced file: " + fnfe.getMessage());
        } catch (Exception e) {

            if (e.getCause() != null)
                throw new Exception(e.getCause());

            log.error("Failed to create stream-object: {}", e.getMessage());
            e.printStackTrace();
            throw new Exception("Failed to create data-stream: " + e.getMessage());
        }
    }
}