/**
 * 
 */
package storm;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import stream.StreamTopology;
import stream.runtime.StreamRuntime;
import stream.util.XMLUtils;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;

/**
 * @author chris
 * 
 */
public class Deploy {

	static Logger log = LoggerFactory.getLogger(Deploy.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			StreamRuntime.setupLogging();

			if (args.length != 1) {
				System.err
						.println("Expecting exactly a single XML configuration file.");
				System.exit(-1);
			}

			log.info("Testing data-enrichment engine (topology)");
			Properties p = new Properties();

			File file = new File("storm.proprties");
			if (file.exists()) {
				log.info("Loading properties from {}", file);
				p.load(new FileInputStream(file));
				System.getProperties().putAll(p);
			}

			URL url = new File(args[0]).toURI().toURL();
			log.info("Creating topology with config from '{}'", url);

			Document doc = XMLUtils.parseDocument(url.openStream());
			Element root = doc.getDocumentElement();
			String id = root.getAttribute("id");
			log.info("Container/topology ID is: '{}'", id);

			StreamTopology topology = StreamTopology.create(doc);
			Config config = new Config();

			log.info("Building sub-topology...");

			StormTopology stormTop = topology.createTopology();

			String name = id;
			if (id == null || id.trim().isEmpty()) {
				name = UUID.randomUUID().toString().toLowerCase();
			}

			log.info("Submitting topology '{}'", name);
			StormSubmitter.submitTopology(name, config, stormTop);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
