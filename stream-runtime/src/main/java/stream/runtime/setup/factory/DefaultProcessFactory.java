package stream.runtime.setup.factory;

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
import stream.runtime.ProcessContainer;
import stream.runtime.ProcessContextImpl;
import stream.runtime.setup.handler.ProcessElementHandler;
import stream.service.Service;
import stream.util.Variables;

/**
 * @author hendrik
 * 
 */
public class DefaultProcessFactory implements ProcessFactory {

	static Logger log = LoggerFactory.getLogger(ProcessElementHandler.class);

	private final ProcessContainer processContainer;
	private final ObjectFactory objectFactory;
	private final ComputeGraph computeGraph;
	protected String defaultProcessImplementation = "stream.runtime.DefaultProcess";
	private final DependencyInjection dependencyInjection;
	protected String processType;

	public DefaultProcessFactory(ProcessContainer processContainer,
			ObjectFactory objectFactory, DependencyInjection dependencyInjection) {
		this.processContainer = processContainer;
		this.objectFactory = objectFactory;
		this.computeGraph = processContainer.computeGraph();
		this.dependencyInjection = dependencyInjection;
		this.processType = "process";
	}

	@Override
	public ProcessConfiguration[] createConfigurations(Element e, Variables v) {

		ProcessConfiguration[] configs;

		ProcessConfiguration config = new ProcessConfiguration();
		Map<String, String> attr = objectFactory.getAttributes(e);

		config.setAttributes(attr);
		config.setElement(e);
		config.setProcessType(this.processType);

		// Get Input Output
		String src = attr.get("source");
		if (src == null)
			src = attr.get("input");

		String out = attr.get("output");

		config.setInput(src);
		config.setOutput(out);

		// Set Process class
		//
		String processClass = defaultProcessImplementation;
		if (attr.containsKey("class")) {
			processClass = attr.get("class");
			log.debug("Using custom process class '{}'", processClass);
		}

		config.setProcessClass(processClass);

		String id = attr.get("id");

		if (id == null || "".equals(id.trim())) {
			id = "process-" + UUID.randomUUID().toString();
		}
		id = v.expand(id);

		// Create copies and set process-local Properties
		String copies = attr.get("copies");
		if (attr.containsKey("multiply")) {
			copies = attr.get("multiply");
			log.warn("The attribute 'multiply' is deprecated for element 'Process'");
			log.warn("Please use 'copies' instead of 'multiply'.");
		}

		if (copies != null && !"".equals(copies.trim())) {

			// Take original properties
			log.debug("Expanding '{}'", copies);
			copies = v.expand(copies);

			// incrementing ids or predefinied copies?
			String[] ids;

			// predefinied
			if (copies.indexOf(",") >= 0) {
				ids = copies.split(",");
			}
			// incrementing ids
			else {
				try {
					Integer times = new Integer(copies);
					ids = new String[times];
					for (int i = 0; i < times; i++) {
						ids[i] = "" + i;
					}
				} catch (NumberFormatException ex) {
					ids = new String[1];
					ids[0] = copies;
				}

			}
			log.debug("Creating {} processes due to copies='{}'", ids.length,
					copies);

			configs = new ProcessConfiguration[ids.length];
			int i = 0;
			// create process-local properties
			for (String pid : ids) {

				ProcessConfiguration configi = null;
				try {
					configi = (ProcessConfiguration) config.clone();
				} catch (CloneNotSupportedException e1) {
					e1.printStackTrace();
				}
				configs[i] = configi;

				configi.setVariables(v);
				Variables local = configi.getVariables();

				String idpid = id + "-" + pid;
				idpid = local.expand(idpid);
				configi.setId(idpid);
				configi.setCopyId(pid);

				// input output
				String input = local.expand(src);
				log.debug("Setting source for process {} to {}", idpid, input);
				configi.setInput(input);

				if (out != null) {
					String processOut = local.expand(out);
					log.debug("Setting process output for process {} to {}",
							idpid, processOut);
					configi.setOutput(processOut);

				} else
					log.debug("Process has no output connection...");
				i++;
			}

			return configs;
		}

		else {
			Variables local = new Variables(v);
			config.setVariables(local);
			id = local.expand(id);
			config.setId(id);
			return new ProcessConfiguration[] { config };
		}
	}

	@Override
	public void createAndRegisterProcesses(ProcessConfiguration[] configs)
			throws Exception {

		for (ProcessConfiguration config : configs) {

			log.trace("Creating 'process' element, variable context is:");
			for (String key : config.getVariables().keySet()) {
				log.trace("  '{}' = '{}'", key, config.getVariables().get(key));
			}

			DefaultProcess process = (DefaultProcess) objectFactory.create(
					config.getProcessClass(), config.getAttributes(),
					config.getElement(), config.getVariables());

			processContainer.getProcesses().add(process);
			computeGraph.addProcess(config.getId(), process);

			// process local source
			log.debug("Created Process object: {}", process);

			log.debug("Process input is: '{}'", config.getInput());

			// add to local properties
			process.getProperties().putAll(config.getAttributes());
			process.getProperties().putAll(config.getVariables());

			// Add a source-reference for later dependency injection. The source
			// is injected into the processes as property 'source'.
			//
			SourceRef sourceRef = new SourceRef(process, "input",
					config.getInput());
			dependencyInjection.add(sourceRef);

			// this should not be required in the future - handled
			// by dependencyInjection class
			computeGraph.addReference(sourceRef);

			// check if a sink is referenced with the 'output'
			//

			String outputId = config.getOutput();
			if (outputId != null && !outputId.trim().isEmpty()) {
				SinkRef sinkRef = new SinkRef(process, "output", outputId);
				log.debug("Adding output reference for process {} to {}",
						process, outputId);
				dependencyInjection.add(sinkRef);

				// this should not be required in the future - handled
				// by dependencyInjection class
				computeGraph.addReference(sinkRef);
			}

			ProcessContext ctx = new ProcessContextImpl(
					processContainer.getContext());

			for (Map.Entry<String, String> e : config.getAttributes()
					.entrySet()) {
				ctx.set(e.getKey(), e.getValue());
			}
			// add to local process context
			for (Map.Entry<String, String> e : config.getVariables().entrySet()) {
				ctx.set(e.getKey(), e.getValue());
			}
			processContainer.setProcessContext(process, ctx);

			List<Processor> procs = createNestedProcessors(config.getElement(),
					config.getVariables());
			for (Processor p : procs) {
				process.add(p);
				processContainer.computeGraph().add(process, p);
			}
		}
	}

	/**
	 * @param container
	 * @param child
	 * @param variables
	 * @param dependencyInjection
	 * @return
	 * @throws Exception
	 */
	protected List<Processor> createNestedProcessors(Element child,
			Variables local) throws Exception {
		List<Processor> procs = new ArrayList<Processor>();

		NodeList pnodes = child.getChildNodes();
		for (int j = 0; j < pnodes.getLength(); j++) {

			Node cnode = pnodes.item(j);
			if (cnode.getNodeType() == Node.ELEMENT_NODE) {
				Processor p = createProcessorAndRegisterServices(
						(Element) cnode, local);
				if (p != null) {
					log.debug("Found processor...");
					procs.add(p);
				}
			}
		}
		return procs;
	}

	protected Processor createProcessorAndRegisterServices(Element child,
			Variables local) throws Exception {

		Map<String, String> params = objectFactory.getAttributes(child);

		Object o = objectFactory.create(child, params, local);

		if (o instanceof ProcessorList) {

			NodeList children = child.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {

				Node node = children.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) node;
					Processor proc = createProcessorAndRegisterServices(
							element, local);
					if (proc != null) {
						((ProcessorList) o).getProcessors().add(proc);
					} else {
						log.warn(
								"Nested element {} is not of type 'stream.data.Processor': ",
								node.getNodeName());
					}
				}
			}
			// return (Processor) o;
		}
		// A processorList is also a processor
		if (o instanceof Processor) {
			// Services
			// expand and handle id
			if (params.containsKey("id") && !"".equals(params.get("id").trim())) {
				if (o instanceof Service) {
					String id = params.get("id").trim();

					id = local.expand(id);
					log.debug(
							"Registering processor with id '{}' in look-up service",
							id);
					processContainer.getContext().register(id, (Service) o);
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
					throw new Exception(
							"'-ref' attributes are no longer supported!");

				final String value = local.expand(params.get(k));

				// make the key SinkInjectionAware
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

				// make the key ServiceInjectionAware
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

}
