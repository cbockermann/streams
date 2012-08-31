/**
 * 
 */
package stream.storm;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import stream.Processor;
import stream.ProcessorList;
import stream.StormRunner;
import stream.data.Data;
import stream.runtime.setup.ObjectFactory;
import stream.runtime.setup.ProcessorFactory;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * @author chris
 * 
 */
public class ProcessBolt extends BaseRichBolt {

	/** The unique class ID */
	private static final long serialVersionUID = -924312414467186051L;

	static Logger log = LoggerFactory.getLogger(ProcessBolt.class);

	protected OutputCollector output;
	transient ProcessorList process;
	protected final String xmlConfig;
	protected final String uuid;

	public ProcessBolt(String xmlConfig, String uuid) {
		this.xmlConfig = xmlConfig;
		this.uuid = uuid;
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
		this.output = collector;

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document config = builder.parse(new ByteArrayInputStream(xmlConfig
					.getBytes()));

			Element element = StormRunner.findElementByUUID(
					config.getDocumentElement(), uuid);

			if (element == null) {
				throw new Exception("Fuck! You screwed the XML!!");
			}

			ObjectFactory obf = ObjectFactory.newInstance();
			ProcessorFactory pf = new ProcessorFactory(obf);
			log.info("Creating processor-list from element {}", element);
			List<Processor> list = pf.createNestedProcessors(element);

			process = new ProcessorList();
			for (Processor p : list) {
				process.getProcessors().add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see backtype.storm.task.IBolt#execute(backtype.storm.tuple.Tuple)
	 */
	@Override
	public void execute(Tuple input) {
		log.debug("Tuple received: {}", input);

		Object data = input.getValueByField("stream.Data");
		log.debug("Data is: {}", data);
		if (data != null) {
			Data item = (Data) data;
			item = process.process(item);
			log.debug("Emitting result item: {}", item);
			output.emit(new Values(item));
			log.debug("ack'ing item {}", input);
			output.ack(input);
		}
	}

	/**
	 * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		log.debug("Declaring Bolt-output field 'stream.Data'");
		declarer.declare(new Fields("stream.Data"));
	}
}