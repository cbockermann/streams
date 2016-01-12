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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import stream.CopiesUtils;
import stream.Copy;
import stream.app.ComputeGraph;
import stream.io.Queue;
import stream.runtime.DependencyInjection;
import stream.runtime.ElementHandler;
import stream.runtime.ProcessContainer;
import stream.runtime.setup.factory.ObjectFactory;
import stream.service.Service;
import stream.util.Variables;

/**
 * @author chris, Hendrik
 * 
 */
public class QueueElementHandler implements ElementHandler {

    static Logger log = LoggerFactory.getLogger(QueueElementHandler.class);

    final static String DEFAULT_QUEUE_IMPL = "stream.io.DefaultBlockingQueue";

    /**
     * @see stream.runtime.ElementHandler#getKey()
     */
    @Override
    public String getKey() {
        return "Queue";
    }

    /**
     * @see stream.runtime.ElementHandler#handlesElement(org.w3c.dom.Element)
     */
    @Override
    public boolean handlesElement(Element element) {
        return getKey().equalsIgnoreCase(element.getNodeName());
    }

    /**
     * @see stream.runtime.ElementHandler#handleElement(stream.container.ProcessContainer,
     *      org.w3c.dom.Element)
     */
    @Override
    public void handleElement(ProcessContainer container, Element element, Variables variables,
            DependencyInjection dependencyInjection) throws Exception {

        final ComputeGraph computeGraph = container.computeGraph();

        String className = element.getAttribute("class");
        if (className == null || className.trim().isEmpty())
            className = DEFAULT_QUEUE_IMPL;

        Map<String, String> params = container.getObjectFactory().getAttributes(element);
        if (!params.containsKey("class")) {
            params.put("class", DEFAULT_QUEUE_IMPL);
        }

        String id = element.getAttribute("id");
        if (id == null || id.trim().isEmpty())
            throw new Exception("No 'id' attribute defined for queue!");

        String copiesString = element.getAttribute("copies");
        Copy[] copies = null;
        if (copiesString != null && !copiesString.isEmpty()) {
            copiesString = variables.expand(copiesString);
            copies = CopiesUtils.parse(copiesString);
        } else {
            Copy c = new Copy();
            c.setId(id);
            copies = new Copy[] { c };
        }
        if (copies == null) {
            log.info("queues where not created, due to 'zero' copies");
            return;
        }
        for (Copy copy : copies) {
            Variables local = new Variables(variables);

            CopiesUtils.addCopyIds(local, copy);
            String cid = local.expand(id);

            Queue queue = (Queue) container.getObjectFactory().create(className, params,
                    ObjectFactory.createConfigDocument(element), local);
            container.registerQueue(copy.getId(), queue, true);
            computeGraph.addQueue(cid, queue);

            if (queue instanceof Service) {
                container.getContext().register(cid, (Service) queue);
                computeGraph.addService(cid, (Service) queue);
            }
        }
    }
}