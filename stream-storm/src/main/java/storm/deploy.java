/**
 * 
 */
package storm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class deploy {

	static Logger log = LoggerFactory.getLogger(deploy.class);

	public static List<String> handleArgs(String[] args) throws IOException {

		File userProps = new File(System.getProperty("user.home")
				+ File.separator + ".streams.properties");

		if (userProps.canRead()) {
			System.out.println("Reading user-properties from "
					+ userProps.getAbsolutePath() + ":");
			Properties p = new Properties();
			for (Object k : p.keySet()) {
				System.out.println("  " + k + " = "
						+ p.getProperty(k.toString()));
			}

			p.load(new FileInputStream(userProps));
			System.getProperties().putAll(p);
		}

		if (args.length != 1) {
			System.err
					.println("Expecting exactly a single XML configuration file.");
			System.exit(-1);
		}

		List<String> params = new ArrayList<String>();
		for (String arg : args) {
			params.add(arg);
		}

		Iterator<String> it = params.iterator();
		while (it.hasNext()) {

			String param = it.next();
			if (param.startsWith("-")) {

				if (param.startsWith("--")) {
					param = param.substring(2);
				} else {
					param = param.substring(1);
				}

				if (param.indexOf("=") > 0) {
					String[] kv = param.split("=", 2);
					System.setProperty(kv[0], kv[1]);
				} else {
					System.setProperty(param, "true");
				}

				it.remove();
			}
		}

		return params;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			stream.runtime.StreamRuntime.loadUserProperties();

			StreamRuntime.setupLogging();
			List<String> params = handleArgs(args);

			if (params.isEmpty()) {
				System.err.println("You need to specify an XML configuration!");
				System.exit(-1);
			}

			URL url = new File(params.get(0)).toURI().toURL();
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