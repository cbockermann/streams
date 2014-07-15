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
package stream;

import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import stream.io.SourceURL;
import stream.util.XMLUtils;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;

/**
 * @author chris
 * 
 */
public class Test {

	static Logger log = LoggerFactory.getLogger(Test.class);

	/**
	 * @param args
	 */
	public static void main(String[] cliArgs) throws Exception {
		log.info("Submitting streams container as storm topology...");
		TopologyBuilder builder = new TopologyBuilder();

		List<String> params = stream.run.handleArguments(cliArgs);
		String[] args = params.toArray(new String[params.size()]);

		Properties p = new Properties();
		URL purl = Test.class.getResource("/test.properties");
		if (purl != null) {
			log.info("Loading properties from {}", purl);
			p.load(purl.openStream());
			System.getProperties().putAll(p);
		}

		URL url = Test.class.getResource("/test.xml");
		if (System.getProperty("xml") != null) {
			log.info("Trying to use XML configuration from {}",
					System.getProperty("xml"));
			url = new URL(System.getProperty("xml"));
		}

		Document xml;

		if (args.length > 0) {
			SourceURL src = new SourceURL(args[0]);
			log.info("Ttying to read configuration from {}", src);
			xml = XMLUtils.parseDocument(src.openStream());
		} else {
			log.info("Reading XML configuration from {}", url);
			xml = XMLUtils.parseDocument(url.openStream());
		}

		String id = xml.getDocumentElement().getAttribute("id");
		log.info("Container ID is '{}'", id);
		if (id == null || id.isEmpty()) {
			id = UUID.randomUUID().toString().toLowerCase();
		}

		if (System.getProperty("id") != null) {
			id = System.getProperty("id");
		}
		log.info("Using topology id '{}'", id);

		Config config = new Config();
		config.put(Config.NIMBUS_HOST,
				System.getProperty("nimbus.host", "192.168.10.100"));
		config.put(Config.NIMBUS_THRIFT_PORT,
				new Integer(System.getProperty("nimbus.port", "6627")));

		StreamTopology streamGraph = StreamTopology.build(xml, builder);

		StormTopology topology = streamGraph.createTopology();
		StormSubmitter.submitTopology(id, config, topology);

		// NimbusClient nimbusClient = NimbusClient
		// .getConfiguredClient(config);
		// Client client = nimbusClient.getClient();
		// String jsonConfig = JSONValue.toJSONString(config);
		// client.submitTopology("CB:test", "", jsonConfig, stormTop);
	}
}
