/**
 * 
 */
package stream.storm;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.Data;
import stream.Processor;
import stream.ProcessorList;
import stream.StormRunner;
import stream.data.DataFactory;
import stream.io.Sink;
import stream.runtime.Variables;
import stream.runtime.setup.ObjectFactory;
import stream.runtime.setup.ProcessorFactory;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * <p>
 * This bolt implementation wraps a process configuration and creates the
 * process including its inner element to provide a bolt that behaves the same
 * as a regular streams process.
 * </p>
 * <p>
 * TODO: Currently, services are not injected into bolts as there is no service
 * facade implementation for storm, yet.
 * </p>
 * 
 * @author Christian Bockermann
 * 
 */
public class ProcessBolt extends AbstractBolt {

	/** The unique class ID */
	private static final long serialVersionUID = -924312414467186051L;

	static Logger log = LoggerFactory.getLogger(ProcessBolt.class);

	transient ProcessorList process;

	// a list of the output references for each of the processors within this
	// process
	transient List<OutputRef> outputRefs = new ArrayList<OutputRef>();

	protected final Variables variables;
	protected String[] outputs;
	final BoltContext ctx = new BoltContext();

	/**
	 * The bolt implementation requires an XML configuration (the complete
	 * container XML as string) and the ID that identifies the corresponding
	 * process within that XML.
	 * 
	 * @param xmlConfig
	 *            The XML configuration as String.
	 * @param uuid
	 *            The ID of the process.
	 */
	public ProcessBolt(String xmlConfig, String uuid,
			Map<String, String> variables) throws Exception {
		super(xmlConfig, uuid);
		this.variables = new Variables(variables);

		// first step: instantiate the process and all of its
		// processors - this is required to determine any references to
		// sinks/services
		//
	}

	/**
	 * This method creates the inner processors of this process bolt.
	 * 
	 * @return
	 * @throws Exception
	 */
	protected ProcessorList createProcess() throws Exception {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document config = builder.parse(new ByteArrayInputStream(xmlConfig
				.getBytes()));

		Element element = StormRunner.findElementByUUID(
				config.getDocumentElement(), uuid);

		if (element == null) {
			log.error("Failed to find process for uuid '{}' in the XML!", uuid);
			throw new Exception("Failed to find process for uuid '" + uuid
					+ "' in the XML!");
		}

		ObjectFactory obf = ObjectFactory.newInstance();
		ProcessorFactory pf = new ProcessorFactory(obf);

		// The handler injects wrappers for any QueueService accesses, thus
		// effectively doing the queue-flow injection
		//
		pf.addCreationHandler(new QueueInjection(output));

		log.debug("Creating processor-list from element {}", element);
		List<Processor> list = pf.createNestedProcessors(element);

		process = new ProcessorList();
		for (Processor p : list) {
			process.getProcessors().add(p);
		}

		if (element.hasAttribute("output")) {
			String out = element.getAttribute("output");
			if (out.indexOf(",") > 0) {
				outputs = out.split(",");
			} else {
				outputs = new String[] { out };
			}
		}

		return process;
	}

	public List<Processor> getAllProcessors() {
		return getAllProcessors(process);
	}

	protected List<Processor> getAllProcessors(ProcessorList list) {
		List<Processor> ps = new ArrayList<Processor>();

		for (Processor p : list.getProcessors()) {
			if (p instanceof ProcessorList) {
				ps.addAll(getAllProcessors((ProcessorList) p));
			} else {
				ps.add(p);
			}
		}
		return ps;
	}

	protected Processor createProcessor(Element el, ProcessorFactory pf)
			throws Exception {

		Processor p = pf.createProcessor(el);

		if (p instanceof ProcessorList) {
			ProcessorList list = (ProcessorList) p;

			NodeList nested = el.getChildNodes();
			for (int i = 0; i < nested.getLength(); i++) {
				Node ch = nested.item(i);
				if (ch.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) ch;
					Processor inner = createProcessor(e, pf);
					list.getProcessors().add(inner);
				}
			}
		}

		return p;
	}

	/**
	 * @see backtype.storm.task.IBolt#prepare(java.util.Map,
	 *      backtype.storm.task.TopologyContext,
	 *      backtype.storm.task.OutputCollector)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {

		log.info("Preparing ProcessBolt {}", this.uuid);
		this.output = collector;
		log.info("   output collector: {}", output);

		try {

			process = createProcess();
			process.init(ctx);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to create process!");
		}
	}

	/**
	 * @see backtype.storm.task.IBolt#execute(backtype.storm.tuple.Tuple)
	 */
	@Override
	public void execute(Tuple input) {
		log.info("Tuple received: {}", input);

		Data item = null;

		try {
			Object data = input.getValueByField("stream.Data");
			log.debug("Data is: {}", data);
			if (data != null) {
				item = (Data) data;
			}
		} catch (Exception e) {
			log.debug("Error processing tuple: {}", e.getMessage());

			item = DataFactory.create();
			Fields fields = input.getFields();
			for (int i = 0; i < fields.size(); i++) {
				String key = fields.get(i);
				Object value = input.getValue(i);
				if (value instanceof Serializable) {
					item.put(key, (Serializable) value);
				}
			}
		}

		if (item != null) {
			log.debug("Processing item...");
			item = process.process(item);

			if (outputs != null) {
				for (String out : outputs) {
					log.debug("Emitting result item to {}: {}", out, item);
					output.emit(out, new Values(item));
				}
			} else {
				log.debug("Emitting item {}", item);
				output.emit(new Values(item));
			}
		} else {
			log.debug("No item to process!");
		}
	}

	public final class DataForwarder implements Sink {

		final String id;
		final OutputCollector output;

		public DataForwarder(String id, OutputCollector output) {
			this.id = id;
			this.output = output;
		}

		/**
		 * @see stream.io.Sink#getId()
		 */
		@Override
		public String getId() {
			return id;
		}

		/**
		 * @see stream.io.Sink#write(stream.Data)
		 */
		@Override
		public boolean write(Data item) throws Exception {
			if (item == null)
				return false;
			output.emit(id, new Values(item));
			return true;
		}

		@Override
		public void close() throws Exception {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean write(Data[] data) throws Exception {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean offer(Data d) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	public final class OutputRef {
		final Processor processor;
		final String property;
		final String[] refs;

		public OutputRef(Processor p, String property, String[] refs) {
			this.processor = p;
			this.property = property;
			this.refs = refs;
		}
	}
}