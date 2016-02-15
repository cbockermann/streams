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
package stream.runtime.shutdown;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.Process;
import stream.Processor;
import stream.io.Source;
import stream.runtime.AbstractProcess;
import stream.runtime.Monitor;
import stream.utils.XMLFormatter;
import streams.application.ComputeGraph;

/**
 * @author chris
 * 
 */
public class LocalShutdownCondition extends AbstractShutdownCondition {

	static Logger log = LoggerFactory.getLogger(LocalShutdownCondition.class);

	/**
	 * @see stream.runtime.ShutdownCondition#isMet(streams.application.ComputeGraph)
	 */
	@Override
	public boolean isMet(ComputeGraph graph) {

		log.debug("Checking if shutdown condition is met...");
		synchronized (graph) {

			if (graph.nodes().isEmpty())
				return true;

			List<Monitor> monitors = new ArrayList<Monitor>();
			int processes = 0;
			int monitorCount = 0;

			for (Object node : graph.nodes()) {
				if (node instanceof Monitor) {
					monitors.add((Monitor) node);
					monitorCount++;
					continue;
				}

				if (node instanceof Process && !(node instanceof Monitor)) {
					processes++;
				}
			}

			if (processes == 0) {
				log.debug("No more processes running...");
				return true;
			}

			if (processes == 0 && monitorCount == 0) {
				log.debug("All monitors, all processes have finished!");
				return true;
			}

			int hangon = 0;

			// for (Object root : graph.getRootSources()) {
			// log.info("Root source: {}", root);
			// }
			log.debug("config:\n{}", XMLFormatter.createXMLString(graph));

			for (Object node : graph.nodes()) {
				if (node instanceof Source) {
					continue;
				}

				if (node instanceof Monitor) {
					continue;
				}
				if (node instanceof Processor
						&& (!(node instanceof AbstractProcess))) {
					// log.debug(
					// "Ignoring dependency-condition for processor '{}'",
					// node);
					// continue;
					hangon++;
				}

				/*
				 * if (!graph.getSourcesFor(node).isEmpty()) { //log.info( //
				 * "Found referenced node '{}' with {} references -> shutdown condition not met."
				 * , // node, graph.getSourcesFor(node).size());
				 * //log.info("   references are: {}",
				 * graph.getSourcesFor(node)); hangon++; }
				 */
			}
			if (hangon > 0)
				return false;

			log.debug("shutdown-condition fulfilled!");
			return true;
		}
	}

}
