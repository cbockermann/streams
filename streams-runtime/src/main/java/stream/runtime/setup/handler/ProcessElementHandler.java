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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.CopiesUtils;
import stream.Processor;
import stream.ProcessorList;
import stream.container.IContainer;
import stream.io.Sink;
import stream.runtime.DependencyInjection;
import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.setup.factory.DefaultProcessFactory;
import stream.runtime.setup.factory.ObjectFactory;
import stream.runtime.setup.factory.ProcessConfiguration;
import stream.runtime.setup.factory.ProcessFactory;
import stream.runtime.setup.factory.ProcessorFactory;
import stream.service.Service;
import stream.util.Variables;
import streams.application.ComputeGraph;
import streams.application.ComputeGraph.ServiceRef;
import streams.application.ComputeGraph.SinkRef;

/**
 * @author chris, Hendrik
 * 
 */
public class ProcessElementHandler implements ElementHandler {

    static Logger log = LoggerFactory.getLogger(ProcessElementHandler.class);
    protected final ObjectFactory objectFactory;
    protected final ProcessorFactory processorFactory;
    protected final String defaultProcessImplementation = "stream.runtime.DefaultProcess";

    public ProcessElementHandler(ObjectFactory objectFactory, ProcessorFactory processorFactory) {
        this.objectFactory = objectFactory;
        this.processorFactory = processorFactory;
    }

    /**
     * @see stream.runtime.ElementHandler#getKey()
     */
    @Override
    public String getKey() {
        return "Process";
    }

    /**
     * @see stream.runtime.ElementHandler#handlesElement(org.w3c.dom.Element)
     */
    @Override
    public boolean handlesElement(Element element) {
        return "process".equalsIgnoreCase(element.getNodeName());
    }

    /**
     * @see stream.runtime.ElementHandler#handleElement(stream.container.ProcessContainer
     *      , org.w3c.dom.Element)
     */
    @Override
    public void handleElement(ProcessContainer container, Element element, Variables variables,
            DependencyInjection dependencyInjection) throws Exception {
        ProcessFactory pf = new DefaultProcessFactory(container, objectFactory, dependencyInjection);
        ProcessConfiguration[] configs = pf.createConfigurations(element, variables);
        pf.createAndRegisterProcesses(configs);

    }

    protected Processor createProcessor(IContainer container, Element child, Variables local,
            DependencyInjection dependencyInjection) throws Exception {

        Map<String, String> params = objectFactory.getAttributes(child);
        final ComputeGraph computeGraph = container.computeGraph();

        Object o = objectFactory.create(child, params, local);

        if (o instanceof ProcessorList) {

            NodeList children = child.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {

                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    Processor proc = createProcessor(container, element, local, dependencyInjection);
                    if (proc != null) {
                        ((ProcessorList) o).getProcessors().add(proc);
                    } else {
                        log.warn("Nested element {} is not of type 'stream.data.Processor': ", node.getNodeName());
                    }
                }
            }
            return (Processor) o;
        } else if (o instanceof Processor) {
            // Services
            // expand and handle id
            if (params.containsKey("id") && !"".equals(params.get("id").trim())) {
                if (o instanceof Service) {
                    String id = params.get("id").trim();

                    id = local.expand(id);
                    log.debug("Registering processor with id '{}' in look-up service", id);
                    container.getNamingService().register(id, (Service) o);
                }
                // false id
                else {
                    log.warn(
                            "Processor '{}' specifies an ID attribute '{}' but does not implement a Service interface. Processor will *not* be registered!",
                            o.getClass().getName(), params.get("id"));
                }
            }

            // For all keys do Service- and Sink-injection
            for (String key : params.keySet()) {

                // remove obsolete "-ref" string, this is to keep
                // backwards-compatibility
                //
                String k = key;
                if (key.endsWith("-ref"))
                    throw new Exception("'-ref' attributes are no longer supported!");

                final String value = local.expand(params.get(k));

                // make the key SinkInjectionAware
                Class<? extends Sink> sinkClass = DependencyInjection.hasSinkSetter(key, o);
                if (sinkClass != null) {
                    log.debug("Found queue-injection for key '{}' in processor '{}'", key, o);

                    // String[] refs = value.split(",");
                    String[] refs = CopiesUtils.parseIds(value, true);
                    SinkRef sinkRefs = new SinkRef(o, key, refs);
                    computeGraph.addReference(sinkRefs);
                    dependencyInjection.add(sinkRefs);
                    log.debug("Adding QueueRef to '{}' for object {}", refs, o);
                    continue;
                }

                // make the key ServiceInjectionAware
                Class<? extends Service> serviceClass = DependencyInjection.hasServiceSetter(key, o);
                if (serviceClass != null) {
                    log.debug("Found service setter for key '{}' in processor {}", key, o);

                    // String[] refs = value.split(",");
                    String[] refs = CopiesUtils.parseIds(value, true);
                    log.debug("Adding ServiceRef to '{}' for object {}", refs, o);
                    ServiceRef serviceRef = new ServiceRef(o, key, refs, serviceClass);
                    computeGraph.addReference(serviceRef);
                    dependencyInjection.add(serviceRef);
                    continue;
                }

            }

            return (Processor) o;
        }

        return null;
    }

    /**
     * @param container
     * @param child
     * @param variables
     * @param dependencyInjection
     * @return
     * @throws Exception
     */
    protected List<Processor> createNestedProcessors(IContainer container, Element child, Variables local,
            DependencyInjection dependencyInjection) throws Exception {
        List<Processor> procs = new ArrayList<Processor>();

        NodeList pnodes = child.getChildNodes();
        for (int j = 0; j < pnodes.getLength(); j++) {

            Node cnode = pnodes.item(j);
            if (cnode.getNodeType() == Node.ELEMENT_NODE) {
                Processor p = createProcessor(container, (Element) cnode, local, dependencyInjection);
                if (p != null) {
                    log.debug("Found processor...");
                    procs.add(p);
                }
            }
        }
        return procs;
    }

}
