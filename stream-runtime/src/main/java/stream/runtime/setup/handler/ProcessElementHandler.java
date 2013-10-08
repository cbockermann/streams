/*
 *  streams library
 *
 *  Copyright (C) 2011-2012 by Christian Bockermann, Hendrik Blom
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
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.ComputeGraph;
import stream.ComputeGraph.ServiceRef;
import stream.ComputeGraph.SinkRef;
import stream.ComputeGraph.SourceRef;
import stream.ProcessContext;
import stream.Processor;
import stream.ProcessorList;
import stream.io.Sink;
import stream.runtime.DefaultProcess;
import stream.runtime.DependencyInjection;
import stream.runtime.ElementHandler;
import stream.runtime.IContainer;
import stream.runtime.ProcessContainer;
import stream.runtime.ProcessContextImpl;
import stream.runtime.setup.ObjectFactory;
import stream.runtime.setup.ProcessorFactory;
import stream.service.Service;
import stream.util.Variables;

/**
 * @author chris
 * 
 */
public class ProcessElementHandler implements ElementHandler {

	static Logger log = LoggerFactory.getLogger(ProcessElementHandler.class);
	protected final ObjectFactory objectFactory;
	protected final ProcessorFactory processorFactory;
	protected final String defaultProcessImplementation = "stream.runtime.DefaultProcess";

	public ProcessElementHandler(ObjectFactory objectFactory,
			ProcessorFactory processorFactory) {
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
	 * @see stream.runtime.ElementHandler#handleElement(stream.runtime.ProcessContainer
	 *      , org.w3c.dom.Element)
	 */
	@Override
	public void handleElement(ProcessContainer container, Element element,
			Variables variables, DependencyInjection dependencyInjection)
			throws Exception {

		final ComputeGraph computeGraph = container.computeGraph();

		Map<String, String> attr = objectFactory.getAttributes(element);
		String src = attr.get("source");
		if (src == null)
			src = attr.get("input");

		String out = attr.get("output");

		// Create the default data-stream process
		//
		String processClass = defaultProcessImplementation;
		if (attr.containsKey("class")) {
			processClass = attr.get("class");
			log.debug("Using custom process class '{}'", processClass);
		}

		String id = attr.get("id");
		if (id == null || "".equals(id.trim())) {
			id = "process-" + UUID.randomUUID().toString();
		}

		String copies = attr.get("copies");
		if (attr.containsKey("multiply")) {
			copies = attr.get("multiply");
			log.warn("The attribute 'multiply' is deprecated for element 'Process'");
			log.warn("Please use 'copies' instead of 'multiply'.");
		}

		if (copies != null && !"".equals(copies.trim())) {

			Variables var = new Variables(variables);
			log.debug("Expanding '{}'", copies);
			copies = var.expand(copies);

			String[] ids;
			if (copies.indexOf(",") >= 0) {
				ids = copies.split(",");
			} else {
				Integer times = new Integer(copies);
				ids = new String[times];
				for (int i = 0; i < times; i++) {
					ids[i] = "" + i;
				}
			}
			log.debug("Creating {} processes due to copies='{}'", ids.length,
					copies);

			// Integer times = new Integer(copies);

			for (String pid : ids) {
				Variables local = new Variables(variables);
				local.put("process.id", pid);
				local.put("copy.id", pid);
				log.debug("Creating process '{}'", pid);
				DefaultProcess process = createProcess(processClass, attr,
						container, element, local, dependencyInjection);

				String input = local.expand(src);
				log.debug("Setting source for process {} to {}", process, input);

				dependencyInjection.add(new SourceRef(process, "input", input));

				if (out != null) {
					String processOut = local.expand(out);
					log.debug("Setting process output for process {} to {}",
							process, processOut);
					dependencyInjection.add(new SinkRef(process, "output",
							processOut));
				} else {
					log.debug("Process has no output connection...");
				}

				computeGraph.addProcess(pid, process);
				container.getProcesses().add(process);
			}

		} else {
			Variables local = new Variables(variables);
			objectFactory.set("process.id", id);
			local.put("process.id", id);
			DefaultProcess process = createProcess(processClass, attr,
					container, element, local, dependencyInjection);
			log.debug("Created Process object: {}", process);
			container.getProcesses().add(process);
			computeGraph.addProcess(id, process);
		}
	}

	protected DefaultProcess createProcess(String processClass,
			Map<String, String> attr, ProcessContainer container,
			Element element, Variables extraVariables,
			DependencyInjection dependencyInjection) throws Exception {

		final ComputeGraph computeGraph = container.computeGraph();

		log.trace("Creating 'process' element, variable context is:");
		for (String key : extraVariables.keySet()) {
			log.trace("  '{}' = '{}'", key, extraVariables.get(key));
		}

		DefaultProcess process = (DefaultProcess) objectFactory.create(
				processClass, attr,
				ObjectFactory.createConfigDocument(element), extraVariables);
		String inputId = extraVariables.expand(attr.get("input"));
		log.debug("Created Process object: {}", process);
		log.debug("Process input is: '{}'", inputId);

		process.getProperties().putAll(attr);
		process.getProperties().putAll(extraVariables);

		// Add a source-reference for later dependency injection. The source
		// is injected into the processes as property 'source'.
		//
		SourceRef sourceRef = new SourceRef(process, "input", inputId);
		dependencyInjection.add(sourceRef);

		// this should not be required in the future - handled
		// by dependencyInjection class
		computeGraph.addReference(sourceRef);

		// check if a sink is referenced with the 'output'
		//

		String outputId = attr.get("output");
		if (outputId != null && !outputId.trim().isEmpty()) {
			outputId = extraVariables.expand(outputId);

			SinkRef sinkRef = new SinkRef(process, "output", outputId);
			log.debug("Adding output reference for process {} to {}", process,
					outputId);
			dependencyInjection.add(sinkRef);

			// this should not be required in the future - handled
			// by dependencyInjection class
			computeGraph.addReference(sinkRef);
		}

		ProcessContext ctx = new ProcessContextImpl(container.getContext());
		for (String key : attr.keySet()) {
			ctx.set(key, attr.get(key));
		}

		for (String key : extraVariables.keySet()) {
			ctx.set(key, extraVariables.get(key));
		}
		container.setProcessContext(process, ctx);

		List<Processor> procs = createNestedProcessors(container, element,
				extraVariables, dependencyInjection);
		for (Processor p : procs) {
			process.add(p);
			container.computeGraph().add(process, p);
		}
		return process;
	}

	protected Processor createProcessor(IContainer container, Element child,
			Variables variables, DependencyInjection dependencyInjection)
			throws Exception {

		Map<String, String> params = objectFactory.getAttributes(child);
		final ComputeGraph computeGraph = container.computeGraph();

		Object o = objectFactory.create(child, variables);
		if (o instanceof Processor) {

			Variables vctx = new Variables(variables);
			if (o instanceof ProcessorList) {

				NodeList children = child.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {

					Node node = children.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {

						Element element = (Element) node;
						Processor proc = createProcessor(container, element,
								variables, dependencyInjection);
						if (proc != null) {
							((ProcessorList) o).getProcessors().add(proc);
						} else {
							log.warn(
									"Nested element {} is not of type 'stream.data.Processor': ",
									node.getNodeName());
						}
					}
				}
			}

			if (params.containsKey("id") && !"".equals(params.get("id").trim())) {

				if (o instanceof Service) {
					String id = params.get("id").trim();

					id = vctx.expand(id);

					log.debug(
							"Registering processor with id '{}' in look-up service",
							id);

					container.getContext().register(id, (Service) o);

				} else {
					log.warn(
							"Processor '{}' specifies an ID attribute '{}' but does not implement a Service interface. Processor will *not* be registered!",
							o.getClass().getName(), params.get("id"));
				}
			}

			for (String key : params.keySet()) {

				// remove obsolete "-ref" string, this is to keep
				// backwards-compatibility
				//
				String k = key;
				if (key.endsWith("-ref"))
					throw new Exception(
							"'-ref' attributes are no longer supported!");

				final String value = vctx.expand(params.get(k));

				Class<? extends Sink> sinkClass = DependencyInjection
						.hasSinkSetter(key, o);
				if (sinkClass != null) {
					log.debug(
							"Found queue-injection for key '{}' in processor '{}'",
							key, o);

					String[] refs = value.split(",");
					SinkRef sinkRefs = new SinkRef(o, key, refs);
					computeGraph.addReference(sinkRefs);
					dependencyInjection.add(sinkRefs);
					log.debug("Adding QueueRef to '{}' for object {}", refs, o);
					continue;
				}

				Class<? extends Service> serviceClass = DependencyInjection
						.hasServiceSetter(key, o);
				if (serviceClass != null) {
					log.debug(
							"Found service setter for key '{}' in processor {}",
							key, o);

					String[] refs = value.split(",");
					log.debug("Adding ServiceRef to '{}' for object {}", refs,
							o);
					ServiceRef serviceRef = new ServiceRef(o, key, refs,
							serviceClass);
					computeGraph.addReference(serviceRef);
					dependencyInjection.add(serviceRef);

					// ServiceReference serviceRef = new ServiceReference(ref,
					// o,
					// key, serviceClass);
					// container.getServiceRefs().add(serviceRef);
					continue;
				}

			}

			return (Processor) o;
		}

		return null;
	}

	protected List<Processor> createNestedProcessors(IContainer container,
			Element child, Variables variables,
			DependencyInjection dependencyInjection) throws Exception {
		List<Processor> procs = new ArrayList<Processor>();

		NodeList pnodes = child.getChildNodes();
		for (int j = 0; j < pnodes.getLength(); j++) {

			Node cnode = pnodes.item(j);
			if (cnode.getNodeType() == Node.ELEMENT_NODE) {
				Processor p = createProcessor(container, (Element) cnode,
						variables, dependencyInjection);
				if (p != null) {
					log.debug("Found processor...");
					procs.add(p);
				}
			}
		}
		return procs;
	}

}
